package fr.voltariuss.playermanager.features.permission;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import fr.voltariuss.playermanager.PlayerManager;
import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.playermanager.cache.PlayerCacheManager;
import fr.voltariuss.playermanager.features.rank.Rank;
import fr.voltariuss.playermanager.features.rank.RankManager;
import fr.voltariuss.simpledevapi.MessageLevel;
import fr.voltariuss.simpledevapi.UtilsAPI;

public final class PermissionManager {

	private static final HashMap<UUID, PermissionAttachment> permissionAttachmentMap = new HashMap<>();

	/**
	 * @return La liste des UUIDs associés chacun à un {@link PermissionAttachment},
	 *         non null
	 */
	public static final HashMap<UUID, PermissionAttachment> getPermissionAttachmentMap() {
		return permissionAttachmentMap;
	}

	/**
	 * Récupère les permissions spécifiques du joueur dans la mémoire centrale s'il
	 * est connecté, dans la base de données sinon.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return La liste des permissions spécifiques du joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static ArrayList<String> getPermissions(OfflinePlayer target) throws SQLException {
		ArrayList<String> permissions = new ArrayList<>();

		if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
			permissions = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getPermissions();
		} else {
			permissions = SQLPermission.getPermissions(target);
		}
		return permissions;
	}

	/**
	 * Active les permissions spécifiques au joueur ciblé.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void setPermissions(Player target) throws SQLException {
		UUID uuid = target.getUniqueId();

		// Création et stockage de la liaison de l'attachement avec le joueur dans la
		// mémoire centrale
		PermissionAttachment attachment = target.addAttachment(JavaPlugin.getPlugin(PlayerManager.class));
		PermissionManager.getPermissionAttachmentMap().put(uuid, attachment);
		Rank rank = RankManager.getRank(target);

		if (rank != Rank.ADMIN) {
			attachment.setPermission("bukkit.command.version", false);
			attachment.setPermission("bukkit.command.plugins", false);
			attachment.setPermission("bukkit.command.help", false);
			attachment.setPermission("bukkit.command.me", false);
			attachment.setPermission("bukkit.command.tell", false);
		}

		for (String permission : rank.getPermissions()) {
			attachment.setPermission(permission, true);
		}

		// Actualisation des permissions spécifiques du joueur dans le cache
		PlayerCacheManager.getPlayerCacheMap().get(uuid).setPermissions(SQLPermission.getPermissions(target));

		for (String permission : getPermissions(target)) {
			attachment.setPermission(permission, true);
		}

		// Actualisation du Nametag du joueur
		if (rank == Rank.ADMIN) {
			if (target.getName().equals(UtilsPlayerManager.SERVER_OWNER)) {
				attachment.setPermission("nte.administrateur", false);
				attachment.setPermission("nte.co-fondateur", false);
				attachment.setPermission("nte.fondateur", true);
			} else if (target.getName().equals(UtilsPlayerManager.SERVER_CO_OWNER)) {
				attachment.setPermission("nte.administrateur", false);
				attachment.setPermission("nte.fondateur", false);
				attachment.setPermission("nte.co-fondateur", true);
			}
		}
	}

	/**
	 * Supprime les permissions spécifiques du joueur et les redéfinit.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void updatePermissions(Player target) throws SQLException {
		// Suppression de la liaison de l'attachement avec le joueur dans la mémoire
		// centrale
		UUID uuid = target.getUniqueId();
		target.removeAttachment(getPermissionAttachmentMap().get(uuid));
		getPermissionAttachmentMap().remove(uuid);
		// Redéfinition des permissions du joueur
		setPermissions(target);
		PlayerCacheManager.getPlayerCacheMap().get(uuid).setPermissions(getPermissions(target));
	}

	/**
	 * Ajoute une permission spécifique au joueur ciblé.
	 * 
	 * @param sender     L'émetteur de la requête, peut être null
	 * @param target     Le joueur ciblé, non null
	 * @param permission La permission à ajouter, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void addPermission(CommandSender sender, OfflinePlayer target, String permission)
			throws SQLException {
		boolean hasPermission = hasPermission(target, permission);

		// Tentative d'ajout de la permission
		if (!hasPermission) {
			UUID uuid = target.getUniqueId();
			SQLPermission.addPermission(target, permission);
			// Actualisation des permissions dans la mémoire cache si le joueur est connecté
			if (PlayerCacheManager.getPlayerCacheMap().containsKey(uuid)) {
				getPermissionAttachmentMap().get(uuid).setPermission(permission, true);
				PlayerCacheManager.getPlayerCacheMap().get(uuid).getPermissions().add(permission);
			}
		}

		// Si l'émetteur de la requête n'est pas null, envoie un retour de l'exécution
		if (sender != null) {
			if (!hasPermission) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.PERMISSION_ADDED,
						permission, target.getName());
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.PERMISSION_ALREADY_OWNED);
			}
		}
	}

	/**
	 * Retire une permission spécifique au joueur ciblé.
	 * 
	 * @param sender     L'émetteur de la requeête, peut être null
	 * @param target     Le joueur ciblé, non null
	 * @param permission La permission à retirer, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void removePermission(CommandSender sender, OfflinePlayer target, String permission)
			throws SQLException {
		boolean hasPermission = hasPermission(target, permission);

		// Tentative d'ajout de la permission
		if (hasPermission) {
			UUID uuid = target.getUniqueId();
			SQLPermission.removePermission(target, permission);
			// Actualisation des permissions dans la mémoire cache si le joueur est connecté
			if (PlayerCacheManager.getPlayerCacheMap().containsKey(uuid)) {
				getPermissionAttachmentMap().get(uuid).setPermission(permission, false);
				PlayerCacheManager.getPlayerCacheMap().get(uuid).getPermissions().remove(permission);
			}
		}

		// Si l'émetteur de la requête n'est pas null, envoie un retour de l'exécution
		if (sender != null) {
			if (hasPermission) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.PERMISSION_REMOVED,
						permission, target.getName());
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.PERMISSION_MISSING);
			}
		}
	}

	/**
	 * Retire toutes les permissions spéciques du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void removeAllPermissions(CommandSender sender, OfflinePlayer target) throws SQLException {
		boolean hasPermission = hasPermission(target);

		// Tentative d'ajout de la permission
		if (hasPermission) {
			SQLPermission.removeAllPermissions(target);
			// Actualisation des permissions dans la m�moire cache si le joueur est connect�
			if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
				updatePermissions((Player) target);
			}
		}

		// Si l'émetteur de la requête n'est pas null, envoie un retour de l'exécution
		if (sender != null) {
			if (hasPermission) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.PERMISSIONS_CLEARED,
						target.getName());
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.PERMISSIONS_EMPTY);
			}
		}
	}

	/**
	 * Vérifie si le joueur possède une permission spécifique dans la base de
	 * données.
	 * 
	 * @param target     Le joueur ciblé, non null
	 * @param permission La permission à vérifier par rapport au joueur, non null
	 * @return Vrai si le joueur possède la permission
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static boolean hasPermission(OfflinePlayer target, String permission) throws SQLException {
		return getPermissions(target).contains(permission);
	}

	/**
	 * Vérifie si le joueur possède au moins une permission.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return Vrai si le joueur possède au moins une permission
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static boolean hasPermission(OfflinePlayer target) throws SQLException {
		return !getPermissions(target).isEmpty();
	}

	/**
	 * Envoie un message à l'émetteur de la requête comportant toutes les
	 * permissions spécifiques du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void sendListPermissions(CommandSender sender, OfflinePlayer target) throws SQLException {
		ArrayList<String> permissions = getPermissions(target);

		if (!permissions.isEmpty()) {
			StringJoiner list = new StringJoiner(ChatColor.YELLOW + ", ");
			Iterator<String> iterator = permissions.iterator();

			while (iterator.hasNext()) {
				list.add(ChatColor.WHITE + iterator.next());
			}
			UtilsAPI.sendSystemMessage(MessageLevel.NORMAL, sender, UtilsPlayerManager.PERMISSIONS_LIST,
					target.getName(), list.toString());
		} else {
			UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.PERMISSIONS_EMPTY);
		}
	}
}