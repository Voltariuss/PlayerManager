package fr.voltariuss.dornacraftplayermanager.perm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.Rank;
import fr.voltariuss.dornacraftplayermanager.playercache.PlayerCache;

public class PermissionManager {
	
	/**
	 * Active les permissions spécifiques au joueur.
	 * 
	 * @param player Le joueur concerné.
	 * @throws Exception 
	 */
	public void setPermissions(Player player) throws Exception {
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
	public void updatePermissions(Player player) throws Exception {
		//On supprime l'association du joueur avec l'objet de type PermissionAttachment en jeu et en mémoire centrale.
		UUID uuid = player.getUniqueId();
		HashMap<UUID,PermissionAttachment> permissionAttachmentMap = DornacraftPlayerManager.getInstance().getPermissionAttachmentMap();
		PermissionAttachment attachment = permissionAttachmentMap.get(uuid);
		
		player.removeAttachment(attachment);
		permissionAttachmentMap.remove(uuid);
		
		//Puis on redéfini ses permissions
		setPermissions(player);
	}
}
