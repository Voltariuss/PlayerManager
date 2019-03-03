package fr.voltariuss.dornacraft.playermanager.features.permission;

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

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.DornacraftAPI;
import fr.voltariuss.dornacraft.api.MessageLevel;
import fr.voltariuss.dornacraft.api.UtilsAPI;
import fr.voltariuss.dornacraft.playermanager.UtilsPlayerManager;
import fr.voltariuss.dornacraft.playermanager.features.rank.Rank;
import fr.voltariuss.dornacraft.playermanager.features.rank.RankManager;

/**
 * Classe de gestion des permissions des joueurs
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class PermissionManager {

	private static final HashMap<UUID, PermissionAttachment> permissionAttachmentMap = new HashMap<>();

	/**
	 * @return La liste des UUIDs associ�s chacun � un {@link PermissionAttachment},
	 *         non null
	 */
	public static final HashMap<UUID, PermissionAttachment> getPermissionAttachmentMap() {
		return permissionAttachmentMap;
	}

	/**
	 * R�cup�re les permissions sp�cifiques du joueur dans la m�moire centrale si il
	 * est connect�, dans la base de donn�es sinon.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return La liste des permissions sp�cifiques du joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
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
	 * Active les permissions sp�cifiques au joueur cibl�.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void setPermissions(Player target) throws SQLException {
		UUID uuid = target.getUniqueId();

		// Cr�ation et stockage de la liaison de l'attachement avec le joueur dans la
		// m�moire centrale
		PermissionAttachment attachment = target.addAttachment(DornacraftAPI.getPlugin(DornacraftAPI.class));
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

		// Actualisation des permissions sp�cifiques du joueur dans le cache
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
	 * Supprime les permissions sp�cifiques du joueur et les red�finit.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void updatePermissions(Player target) throws SQLException {
		// Suppression de la liaison de l'attachement avec le joueur dans la m�moire
		// centrale
		UUID uuid = target.getUniqueId();
		target.removeAttachment(getPermissionAttachmentMap().get(uuid));
		getPermissionAttachmentMap().remove(uuid);
		// Red�finition des permissions du joueur
		setPermissions(target);
		PlayerCacheManager.getPlayerCacheMap().get(uuid).setPermissions(getPermissions(target));
	}

	/**
	 * Ajoute une permission sp�cifique au joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param permission
	 *            La permission � ajouter, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void addPermission(CommandSender sender, OfflinePlayer target, String permission)
			throws SQLException {
		boolean hasPermission = hasPermission(target, permission);

		// Tentative d'ajout de la permission
		if (!hasPermission) {
			UUID uuid = target.getUniqueId();
			SQLPermission.addPermission(target, permission);
			// Actualisation des permissions dans la m�moire cache si le joueur est connect�
			if (PlayerCacheManager.getPlayerCacheMap().containsKey(uuid)) {
				getPermissionAttachmentMap().get(uuid).setPermission(permission, true);
				PlayerCacheManager.getPlayerCacheMap().get(uuid).getPermissions().add(permission);
			}
		}

		// Si l'�metteur de la requ�te n'est pas null, envoie un retour de l'ex�cution
		if (sender != null) {
			if (!hasPermission) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.PERMISSION_ADDED, permission,
						target.getName());
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.PERMISSION_ALREADY_OWNED);
			}
		}
	}

	/**
	 * Retire une permission sp�cifique au joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la reque�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param permission
	 *            La permission � retirer, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void removePermission(CommandSender sender, OfflinePlayer target, String permission)
			throws SQLException {
		boolean hasPermission = hasPermission(target, permission);

		// Tentative d'ajout de la permission
		if (hasPermission) {
			UUID uuid = target.getUniqueId();
			SQLPermission.removePermission(target, permission);
			// Actualisation des permissions dans la m�moire cache si le joueur est connect�
			if (PlayerCacheManager.getPlayerCacheMap().containsKey(uuid)) {
				getPermissionAttachmentMap().get(uuid).setPermission(permission, false);
				PlayerCacheManager.getPlayerCacheMap().get(uuid).getPermissions().remove(permission);
			}
		}

		// Si l'�metteur de la requ�te n'est pas null, envoie un retour de l'ex�cution
		if (sender != null) {
			if (hasPermission) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.PERMISSION_REMOVED, permission,
						target.getName());
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.PERMISSION_MISSING);
			}
		}
	}

	/**
	 * Retire toutes les permissions sp�ciques du joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
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

		// Si l'�metteur de la requ�te n'est pas null, envoie un retour de l'ex�cution
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
	 * V�rifie si le joueur poss�de une permission sp�cifique dans la base de
	 * donn�es.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param permission
	 *            La permission � v�rifier par rapport au joueur, non null
	 * @return True si le joueur poss�de la permission, false sinon
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static boolean hasPermission(OfflinePlayer target, String permission) throws SQLException {
		return getPermissions(target).contains(permission);
	}

	/**
	 * V�rifie si le joueur poss�de au moins une permission.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return True si le joueur poss�de au moins une permission, false sinon
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static boolean hasPermission(OfflinePlayer target) throws SQLException {
		return !getPermissions(target).isEmpty();
	}

	/**
	 * Envoie un message � l'�metteur de la requ�te comportant toutes les
	 * permissions sp�cifiques du joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, non null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void sendListPermissions(CommandSender sender, OfflinePlayer target) throws SQLException {
		ArrayList<String> permissions = getPermissions(target);

		if (!permissions.isEmpty()) {
			StringJoiner list = new StringJoiner(ChatColor.YELLOW + ", ");
			Iterator<String> iterator = permissions.iterator();

			while (iterator.hasNext()) {
				list.add(ChatColor.WHITE + iterator.next());
			}
			UtilsAPI.sendSystemMessage(MessageLevel.NORMAL, sender, UtilsPlayerManager.PERMISSIONS_LIST, target.getName(),
					list.toString());
		} else {
			UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.PERMISSIONS_EMPTY);
		}
	}
}