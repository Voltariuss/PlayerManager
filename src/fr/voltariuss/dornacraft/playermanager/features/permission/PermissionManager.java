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

public final class PermissionManager {
	
	public static final String NO_HAS_PERMISSIONS = "Ce joueur ne poss�de pas de permissions particuli�res.";
	public static final String ALREADY_HAS_PERMISSION = "Ce joueur poss�de d�j� cette permission.";
	public static final String NO_HAS_SPECIFIED_PERMISSION = "Ce joueur ne poss�de pas cette permission.";
	
	private static final HashMap<UUID,PermissionAttachment> permissionAttachmentMap = new HashMap<>();
	
	/**
	 * @return La liste des UUIDs associ�s chacun � une {@link PermissionAttachment}, non null
	 */
	public static final HashMap<UUID,PermissionAttachment> getPermissionAttachmentMap() {
		return permissionAttachmentMap;
	}
	
	/**
	 * R�cup�re les permissions sp�cifiques du joueur dans la m�moire centrale si il est connect�,
	 * dans la base de donn�es sinon.
	 * 
	 * @param target Le joueur cibl�, non null
	 * @return La liste des permissions sp�cifiques du joueur cibl�, non null
	 * @throws SQLException
	 */
	public static ArrayList<String> getPermissions(OfflinePlayer target) throws SQLException {
		ArrayList<String> permissions = new ArrayList<>();
		
		if(target.isOnline()) {
			permissions = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getPermissions();
		} else {
			permissions = SQLPermission.getPermissions(target);
		}
		return permissions;
	}
	
	/**
	 * Active les permissions sp�cifiques au joueur cibl�.
	 * 
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException 
	 */
	public static void setPermissions(Player target) throws SQLException {
		UUID uuid = target.getUniqueId();
		PlayerCache playerCache = PlayerCacheManager.getPlayerCacheMap().get(uuid);
		
		//Cr�ation et stockage de la liaison de l'attachement avec le joueur dans la m�moire centrale
		PermissionAttachment attachment = target.addAttachment(DornacraftAPI.getInstance());
		PermissionManager.getPermissionAttachmentMap().put(uuid, attachment);
		
		//Ajout des permissions du rang du joueur et des inheritances au joueurs
		FileConfiguration config = DornacraftPlayerManager.getInstance().getConfig();
		
		for(String group : config.getConfigurationSection("groups").getKeys(false)) {
			if(group.equals(playerCache.getRank().getName())) {
				String ext = "";
				
				while(ext.equals("") || (!ext.equals("[]") && Rank.valueOf(ext) != null)) {
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
		playerCache.setPermissions(SQLPermission.getPermissions(target));
		
		for(String permission : getPermissions(target)) {
			attachment.setPermission(permission, true);
		}
	}
	
	/**
	 * Supprime les permissions sp�cifiques du joueur et les red�finit.
	 * 
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException 
	 */
	public static void updatePermissions(Player target) throws SQLException {
		//Suppression de la liaison de l'attachement avec le joueur dans la m�moire centrale
		UUID uuid = target.getUniqueId();
		target.removeAttachment(getPermissionAttachmentMap().get(uuid));
		getPermissionAttachmentMap().remove(uuid);
		//Red�finition des permissions du joueur
		setPermissions(target);
	}
	
	/**
	 * Ajoute une permission sp�cifique au joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @param permission La permission � ajouter, non null
	 * @throws SQLException
	 */
	public static void addPermission(CommandSender sender, OfflinePlayer target, String permission) throws SQLException {
		boolean hasPermission = hasPermission(target, permission);
		
		//Tentative d'ajout de la permission
		if(!hasPermission) {
			SQLPermission.addPermission(target, permission);
			//Actualisation des permissions dans la m�moire cache si le joueur est connect�
			if(target.isOnline()) {
				UUID uuid = target.getUniqueId();
				getPermissionAttachmentMap().get(uuid).setPermission(permission, true);
				PlayerCacheManager.getPlayerCacheMap().get(uuid).getPermissions().add(permission);
			}
		}
		
		
		//Si l'�metteur de la requ�te n'est pas null, envoie un retour de l'ex�cution
		if(sender != null) {
			if(!hasPermission) {
				sender.sendMessage("�aLa permission �6" + permission + " �aa �t� ajout�e au joueur �b" + target.getName() + "�a.");
			} else {
				Utils.sendErrorMessage(sender, ALREADY_HAS_PERMISSION);			
			}
		}
	}
	
	/**
	 * Retire une permission sp�cifique au joueur cibl�.
	 * 
	 * @param sender L'�metteur de la reque�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @param permission La permission � retirer, non null
	 * @throws SQLException
	 */
	public static void removePermission(CommandSender sender, OfflinePlayer target, String permission) throws SQLException {
		boolean hasPermission = hasPermission(target, permission);
		
		//Tentative d'ajout de la permission
		if(hasPermission) {
			SQLPermission.removePermission(target, permission);
			//Actualisation des permissions dans la m�moire cache si le joueur est connect�
			if(target.isOnline()) {
				UUID uuid = target.getUniqueId();
				getPermissionAttachmentMap().get(uuid).unsetPermission(permission);
				PlayerCacheManager.getPlayerCacheMap().get(uuid).getPermissions().remove(permission);
			}
		}
		
		//Si l'�metteur de la requ�te n'est pas null, envoie un retour de l'ex�cution
		if(sender != null) {
			if(hasPermission) {
				sender.sendMessage("�aLa permission �6" + permission + " �aa �t� retir�e au joueur �b" + target.getName() + "�a.");
			} else {
				Utils.sendErrorMessage(sender, NO_HAS_SPECIFIED_PERMISSION);			
			}
		}
	}
	
	/**
	 * Retire toutes les permissions sp�ciques du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void removeAllPermissions(CommandSender sender, OfflinePlayer target) throws SQLException {
		boolean hasPermission = hasPermission(target);
		
		//Tentative d'ajout de la permission
		if(hasPermission) {
			SQLPermission.removeAllPermissions(target);
			//Actualisation des permissions dans la m�moire cache si le joueur est connect�
			if(target.isOnline()) {
				updatePermissions((Player) target);
			}
		}
		
		//Si l'�metteur de la requ�te n'est pas null, envoie un retour de l'ex�cution
		if(sender != null) {
			if(hasPermission) {
				sender.sendMessage("�aToutes les permissions sp�cifiques ont �t� retir�es au joueur �b" + target.getName() + "�a.");
			} else {
				Utils.sendErrorMessage(sender, NO_HAS_PERMISSIONS);			
			}
		}
	}
	
	/**
	 * V�rifie si le joueur poss�de une permission sp�cifique dans la base de donn�es.
	 * 
	 * @param target Le joueur cibl�, non null
	 * @param permission La permission � v�rifier par rapport au joueur, non null
	 * @return "vrai" si le joueur poss�de la permission
	 * @throws SQLException
	 */
	public static boolean hasPermission(OfflinePlayer target, String permission) throws SQLException {
		return getPermissions(target).contains(permission);
	}
	
	/**
	 * V�rifie si le joueur poss�de au moins une permission.
	 * 
	 * @param target Le joueur cibl�, non null
	 * @return "vrai" si le joueur poss�de au moins une permission
	 * @throws SQLException
	 */
	public static boolean hasPermission(OfflinePlayer target) throws SQLException {
		return !getPermissions(target).isEmpty();
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te comportant toutes les permissions sp�cifiques du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void sendListPermissions(CommandSender sender, OfflinePlayer target) throws SQLException {
		ArrayList<String> permissions = getPermissions(target);
		
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
			sender.sendMessage("�6Permissions du joueur �b" + target.getName() + " �6: " + listPermissions);
		} else {
			Utils.sendErrorMessage(sender, NO_HAS_PERMISSIONS);
		}
	}
}