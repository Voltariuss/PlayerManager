package fr.voltariuss.dornacraft.playermanager.features.permission;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import fr.dornacraft.cache.PlayerCache;
import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.DornacraftAPI;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraft.playermanager.features.rank.Rank;

public class PermissionManager {
	
	//Messages d'erreur
	public static final String NO_HAS_PERMISSIONS = "�cCe joueur ne poss�de pas de permissions particuli�res.";
	public static final String ALREADY_HAS_PERMISSION = "Ce joueur poss�de d�j� cette permission.";
	public static final String NO_HAS_SPECIFIED_PERMISSION = "Ce joueur ne poss�de pas cette permission.";
	
	//Autres messages
	public static final String SUCCESS_ADD_PERMISSION = "�aLa permission �6% �aa �t� ajout�e au joueur �b%�a.";
	public static final String SUCCESS_REMOVE_PERMISSION = "�aLa permission �6% �aa �t� retir�e au joueur �b%�a.";
	public static final String SUCCESS_REMOVEALL_PERMISSION = "�aToutes les permissions sp�cifiques ont �t� retir�es au joueur �b%�a.";
	public static final String LIST_PERMISSIONS = "�6Permissions du joueur �b% �6: %";
	
	private static final HashMap<UUID,PermissionAttachment> permissionAttachmentMap = new HashMap<>();
	
	public static final HashMap<UUID,PermissionAttachment> getPermissionAttachmentMap() {
		return permissionAttachmentMap;
	}
	
	/**
	 * R�cup�re les permissions sp�cifiques du joueur dans la m�moire centrale si il est connect�,
	 * dans la base de donn�es sinon.
	 * 
	 * @param player Le joueur cibl�, non null
	 * @return La liste des permissions sp�cifiques du joueur cibl�, non null
	 * @throws SQLException
	 */
	public static ArrayList<String> getPermissions(OfflinePlayer player) throws SQLException {
		ArrayList<String> permissions = new ArrayList<>();
		
		if(player.isOnline()) {
			permissions = PlayerCacheManager.getPlayerCacheMap().get(player.getUniqueId()).getPermissions();
		} else {
			permissions = SQLPermission.getPermissions(player);
		}
		return permissions;
	}
	
	/**
	 * Active les permissions sp�cifiques au joueur cibl�.
	 * 
	 * @param player Le joueur cibl�, non null
	 * @throws SQLException 
	 */
	public static void setPermissions(Player player) throws SQLException {
		UUID uuid = player.getUniqueId();
		PlayerCache playerCache = PlayerCacheManager.getPlayerCacheMap().get(uuid);
		
		//Cr�ation et stockage de la liaison de l'attachement avec le joueur dans la m�moire centrale
		PermissionAttachment attachment = player.addAttachment(DornacraftAPI.getInstance());
		PermissionManager.getPermissionAttachmentMap().put(uuid, attachment);
		
		//Ajout des permissions du rang du joueur et des inheritances au joueurs
		FileConfiguration config = DornacraftPlayerManager.getInstance().getConfig();
		
		for(String group : config.getConfigurationSection("groups").getKeys(false)) {
			if(group.equals(playerCache.getRank().getName())) {
				String ext = "";
				
				while(ext.equals("") || (!ext.equals("[]") && Rank.exist(ext))) {
					if(!ext.equals(""))
						group = ext;
					
					for(String permission : config.getStringList("groups." + group + ".permissions")) {
						attachment.setPermission(permission, true);
					}
					ext = config.getString("groups." + group + ".inheritance");
				}
				break;
			}
		}
		//Actualisation des permissions sp�cifiques du joueur dans le cache
		playerCache.setPermissions(SQLPermission.getPermissions(player));
		
		for(String permission : getPermissions(player)) {
			attachment.setPermission(permission, true);
		}
	}
	
	/**
	 * Supprime les permissions sp�cifiques du joueur et les red�finit.
	 * 
	 * @param player Le joueur concern�, non null
	 * @throws SQLException 
	 */
	public static void updatePermissions(Player player) throws SQLException {
		//Suppression de la liaison de l'attachement avec le joueur dans la m�moire centrale
		UUID uuid = player.getUniqueId();
		player.removeAttachment(getPermissionAttachmentMap().get(uuid));
		getPermissionAttachmentMap().remove(uuid);
		//Red�finition des permissions du joueur
		setPermissions(player);
	}
	
	/**
	 * Ajoute une permission sp�cifique au joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param player Le joueur cibl�, non null
	 * @param permission La permission � ajouter, non null
	 * @throws SQLException
	 */
	public static void addPermission(CommandSender sender, OfflinePlayer player, String permission) throws SQLException {
		boolean hasPermission = hasPermission(player, permission);
		
		//Tentative d'ajout de la permission
		if(!hasPermission) {
			SQLPermission.addPermission(player, permission);
			//Actualisation des permissions dans la m�moire cache si le joueur est connect�
			if(player.isOnline()) {
				UUID uuid = player.getUniqueId();
				getPermissionAttachmentMap().get(uuid).setPermission(permission, true);
				PlayerCacheManager.getPlayerCacheMap().get(uuid).getPermissions().add(permission);
			}
		}
		
		
		//Si l'�metteur de la requ�te n'est pas null, envoie un retour de l'ex�cution
		if(sender != null) {
			if(!hasPermission) {
				sendSuccessAddPermissionMessage(sender, permission, player.getName());
			} else {
				Utils.sendErrorMessage(sender, ALREADY_HAS_PERMISSION);			
			}
		}
	}
	
	/**
	 * Retire une permission sp�cifique au joueur cibl�.
	 * 
	 * @param sender L'�metteur de la reque�te, peut �tre null
	 * @param player Le joueur cibl�, non null
	 * @param permission La permission � retirer, non null
	 * @throws SQLException
	 */
	public static void removePermission(CommandSender sender, OfflinePlayer player, String permission) throws SQLException {
		boolean hasPermission = hasPermission(player, permission);
		
		//Tentative d'ajout de la permission
		if(hasPermission) {
			SQLPermission.removePermission(player, permission);
			//Actualisation des permissions dans la m�moire cache si le joueur est connect�
			if(player.isOnline()) {
				UUID uuid = player.getUniqueId();
				getPermissionAttachmentMap().get(uuid).unsetPermission(permission);
				PlayerCacheManager.getPlayerCacheMap().get(uuid).getPermissions().remove(permission);
			}
		}
		
		//Si l'�metteur de la requ�te n'est pas null, envoie un retour de l'ex�cution
		if(sender != null) {
			if(hasPermission) {
				sendSuccessRemovePermissionMessage(sender, permission, player.getName());
			} else {
				Utils.sendErrorMessage(sender, NO_HAS_SPECIFIED_PERMISSION);			
			}
		}
	}
	
	/**
	 * Retire toutes les permissions sp�ciques du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param player Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void removeAllPermissions(CommandSender sender, OfflinePlayer player) throws SQLException {
		boolean hasPermission = hasPermission(player);
		
		//Tentative d'ajout de la permission
		if(hasPermission) {
			SQLPermission.removeAllPermissions(player);
			//Actualisation des permissions dans la m�moire cache si le joueur est connect�
			if(player.isOnline()) {
				updatePermissions((Player) player);
			}
		}
		
		//Si l'�metteur de la requ�te n'est pas null, envoie un retour de l'ex�cution
		if(sender != null) {
			if(hasPermission) {
				sendSuccessRemoveAllPermissionsMessage(sender, player.getName());
			} else {
				Utils.sendErrorMessage(sender, NO_HAS_PERMISSIONS);			
			}
		}
	}
	
	/**
	 * V�rifie si le joueur poss�de une permission sp�cifique dans la base de donn�es.
	 * 
	 * @param player Le joueur concern�, non null
	 * @param permission La permission � v�rifier par rapport au joueur, non null
	 * @return "vrai" si le joueur poss�de la permission, "faux" sinon
	 * @throws SQLException
	 */
	public static boolean hasPermission(OfflinePlayer player, String permission) throws SQLException {
		return getPermissions(player).contains(permission);
	}
	
	/**
	 * V�rifie si le joueur poss�de au moins une permission.
	 * 
	 * @param player Le joueur concern�, non null
	 * @return "vrai" si le joueur poss�de au moins une permission, "faux" sinon
	 * @throws SQLException
	 */
	public static boolean hasPermission(OfflinePlayer player) throws SQLException {
		return !getPermissions(player).isEmpty();
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te comportant toutes les permissions sp�cifiques du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param player Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void sendListPermissions(CommandSender sender, OfflinePlayer player) throws SQLException {
		ArrayList<String> permissions = getPermissions(player);
		
		if(!permissions.isEmpty()) {
			String listPermissions = "";
			Iterator<String> iterator = permissions.iterator();
			
			while(iterator.hasNext()) {
				String permission = "�f" + iterator.next();
				listPermissions = listPermissions + permission;
				
				if(iterator.hasNext()) {
					listPermissions = listPermissions + "�e, ";
				}
			}
			sendPlayerListPermissionsMessage(sender, player.getName(), listPermissions);
		} else {
			Utils.sendErrorMessage(sender, NO_HAS_PERMISSIONS);
		}
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te annon�ant le succ�s de l'ajout d'une permission sp�cifique au joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param permission La permission ajout�e, non null
	 * @param playerName Le nom du joueur cibl�, non null
	 */
	public static void sendSuccessAddPermissionMessage(CommandSender sender, String permission, String playerName) {
		sender.sendMessage(SUCCESS_ADD_PERMISSION.replaceFirst("%", permission).replaceFirst("%", playerName));
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te annon�ant le succ�s de la suppression d'une permission sp�cifique du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param permission La permission retir�e, non null
	 * @param playerName Le nom du joueur cibl�, non null
	 */
	public static void sendSuccessRemovePermissionMessage(CommandSender sender, String permission, String playerName) {
		sender.sendMessage(SUCCESS_REMOVE_PERMISSION.replaceFirst("%", permission).replaceFirst("%", playerName));
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te annon�ant le succ�s de la suppression de toutes les permissions sp�cifiques du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param playerName Le nom du joueur cibl�, non null
	 */
	public static void sendSuccessRemoveAllPermissionsMessage(CommandSender sender, String playerName) {
		sender.sendMessage(SUCCESS_REMOVEALL_PERMISSION.replaceFirst("%", playerName));
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te comportant toutes les permissions sp�cifiques du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param playerName Le nom du joueur cibl�, non null
	 * @param listPermissions La liste des permission sous la forme d'une cha�ne de caract�res, non null
	 */
	public static void sendPlayerListPermissionsMessage(CommandSender sender, String playerName, String listPermissions) {
		sender.sendMessage(LIST_PERMISSIONS.replaceFirst("%", playerName).replaceFirst("%", listPermissions));
	}
}