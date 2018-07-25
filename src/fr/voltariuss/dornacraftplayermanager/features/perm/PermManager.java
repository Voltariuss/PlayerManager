package fr.voltariuss.dornacraftplayermanager.features.perm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import fr.voltariuss.dornacraftapi.FeatureManager;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.cache.playercache.PlayerCache;
import fr.voltariuss.dornacraftplayermanager.features.rank.Rank;

public class PermManager extends FeatureManager {
	
	//Messages d'erreur
	public static final String NO_HAS_PERMISSIONS = "§cCe joueur ne possède pas de permissions particulières.";
	public static final String ALREADY_HAS_PERMISSION = "Ce joueur possède déjà cette permission.";
	public static final String NO_HAS_SPECIFIED_PERMISSION = "Ce joueur ne possède pas cette permission.";
	
	//Autres messages
	public static final String SUCCESS_ADD_PERMISSION = "§aLa permission §6% §aa été ajoutée au joueur §b%§a.";
	public static final String SUCCESS_REMOVE_PERMISSION = "§aLa permission §6% §aa été retirée au joueur §b%§a.";
	public static final String SUCCESS_REMOVEALL_PERMISSION = "§aToutes les permissions spécifiques ont été retirées au joueur §b%§a.";
	public static final String LIST_PERMISSIONS = "§6Permissions du joueur §b% §6: %";
	
	/**
	 * Active les permissions spécifiques au joueur.
	 * 
	 * @param player Le joueur concerné.
	 * @throws Exception 
	 */
	public static void setPermissions(Player player) throws Exception {
		UUID uuid = player.getUniqueId();
		PermissionAttachment attachment = player.addAttachment(DornacraftPlayerManager.getInstance());
		HashMap<UUID,PlayerCache> playerCacheMap = DornacraftPlayerManager.getInstance().getPlayerCacheMap();
		PlayerCache playerCache = null;
		
		playerCache = playerCacheMap.get(uuid);
		DornacraftPlayerManager.getInstance().getPermissionAttachmentMap().put(uuid, attachment);
		
		//Ajout des permissions du rang du joueur et des inheritances au joueur.
		for(String group : DornacraftPlayerManager.getInstance().getConfig().getConfigurationSection("groups").getKeys(false)) {
			if(group.equals(playerCache.getRank().getRankName())) {
				String ext = "";
				
				while(ext.equals("") || (!ext.equals("[]") && Rank.exist(ext))) {
					if(!ext.equals(""))
						group = ext;
					
					for(String permission : DornacraftPlayerManager.getInstance().getConfig().getStringList("groups." + group + ".permissions")) {
						attachment.setPermission(permission, true);
					}
					ext = DornacraftPlayerManager.getInstance().getConfig().getString("groups." + group + ".inheritance");
				}
				break;
			}
		}
		//Ajout des permissions spécifiques au joueur.
		ArrayList<String> permissions = DornacraftPlayerManager.getInstance().getSqlPerm().getPermissions(player);
		
		for(String permission : permissions) {
			attachment.setPermission(permission, true);
		}
	}
	
	/**
	 * Supprime les permissions du joueur et les redéfini.
	 * 
	 * @param player Joueur concerné.
	 * @throws Exception 
	 */
	public static void updatePermissions(Player player) throws Exception {
		//On supprime l'association du joueur avec l'objet de type PermissionAttachment en jeu et en mémoire centrale.
		UUID uuid = player.getUniqueId();
		HashMap<UUID,PermissionAttachment> permissionAttachmentMap = DornacraftPlayerManager.getInstance().getPermissionAttachmentMap();
		PermissionAttachment attachment = permissionAttachmentMap.get(uuid);
		
		player.removeAttachment(attachment);
		permissionAttachmentMap.remove(uuid);
		
		//Puis on redéfini ses permissions
		setPermissions(player);
	}
	
	private final SQLPerm sqlPerm = DornacraftPlayerManager.getInstance().getSqlPerm();

	public PermManager(CommandSender sender) {
		super(sender);
	}
	
	public void addPerm(OfflinePlayer player, String permission) throws Exception {
		if(!sqlPerm.hasPermission(player, permission)) {
			sqlPerm.addPermission(player, permission);
			this.sendSuccessAddPermission(permission, player.getName());
		} else {
			this.sendErrorMessage(ALREADY_HAS_PERMISSION);
		}
	}
	
	public void removePerm(OfflinePlayer player, String permission) throws Exception {
		if(sqlPerm.hasPermission(player, permission)) {
			sqlPerm.removePermission(player, permission);
			this.sendSuccessRemovePermission(permission, player.getName());
		} else {
			this.sendErrorMessage(NO_HAS_SPECIFIED_PERMISSION);
		}
	}
	
	public void removeAllPerm(OfflinePlayer player) throws Exception {
		if(sqlPerm.hasPermission(player)) {
			sqlPerm.removeAllPermissions(player);
			this.sendSuccessRemoveAllPermission(player.getName());
		} else {
			this.sendErrorMessage(NO_HAS_PERMISSIONS);
		}
	}
	
	public void sendListPerm(OfflinePlayer player) throws Exception {
		ArrayList<String> permissions = sqlPerm.getPermissions(player);
		
		if(!permissions.isEmpty()) {
			String listPermissions = "";
			Iterator<String> iterator = permissions.iterator();
			
			while(iterator.hasNext()) {
				String permission = "§f" + iterator.next();
				listPermissions = listPermissions + permission;
				
				if(iterator.hasNext()) {
					listPermissions = listPermissions + "§e, ";
				}
			}
			this.sendPlayerListPermMessage(player.getName(), listPermissions);
		} else {
			this.sendErrorMessage(NO_HAS_PERMISSIONS);
		}
	}
	
	public void sendSuccessAddPermission(String permission, String playerName) {
		this.sendMessage(SUCCESS_ADD_PERMISSION.replaceFirst("%", permission).replaceFirst("%", playerName));
	}
	
	public void sendSuccessRemovePermission(String permission, String playerName) {
		this.sendMessage(SUCCESS_REMOVE_PERMISSION.replaceFirst("%", permission).replaceFirst("%", playerName));
	}
	
	public void sendSuccessRemoveAllPermission(String playerName) {
		this.sendMessage(SUCCESS_REMOVEALL_PERMISSION.replaceFirst("%", playerName));
	}
	
	public void sendPlayerListPermMessage(String playerName, String listPermissions) {
		this.sendMessage(LIST_PERMISSIONS.replaceFirst("%", playerName).replaceFirst("%", listPermissions));
	}
}
