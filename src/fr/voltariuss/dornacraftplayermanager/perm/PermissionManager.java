package fr.voltariuss.dornacraftplayermanager.perm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.Rank;
import fr.voltariuss.dornacraftplayermanager.playercache.PlayerCache;
import fr.voltariuss.dornacraftplayermanager.sql.SQLPerm;

public class PermissionManager {
	
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
	private SQLPerm sqlPerm = main.getSqlPerm();

	/**
	 * Active les permissions spécifiques au joueur.
	 * 
	 * @param player Le joueur concerné.
	 * @throws SQLException 
	 * @throws Exception 
	 */
	public void setPermissions(Player player) throws SQLException, Exception {
		UUID uuid = player.getUniqueId();
		PermissionAttachment attachment = player.addAttachment(main);
		HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
		PlayerCache playerCache = null;
		
		playerCache = playerCacheMap.get(uuid);
		main.getPermissionAttachmentMap().put(uuid, attachment);
		
		//Ajout des permissions du rang du joueur et des inheritances au joueur.
		for(String group : main.getConfig().getConfigurationSection("groups").getKeys(false)) {
			if(group.equals(playerCache.getRank().getRankName())) {
				String ext = "";
				
				while(ext.equals("") || (!ext.equals("[]") && Rank.exist(ext))) {
					if(!ext.equals(""))
						group = ext;
					
					for(String permission : main.getConfig().getStringList("groups." + group + ".permissions")) {
						attachment.setPermission(permission, true);
					}
					ext = main.getConfig().getString("groups." + group + ".inheritance");
				}
				break;
			}
		}
		//Ajout des permissions spécifiques au joueur.
		ArrayList<String> permissions = sqlPerm.getPermissions(player);
		
		for(String permission : permissions) {
			attachment.setPermission(permission, true);
		}
	}
	
	/**
	 * Supprime les permissions du joueur et les redéfini.
	 * 
	 * @param player Joueur concerné.
	 * @throws SQLException 
	 * @throws PlayerCacheAccessException 
	 */
	public void updatePermissions(Player player) throws SQLException, Exception {
		//On supprime l'association du joueur avec l'objet de type PermissionAttachment en jeu et en mémoire centrale.
		UUID uuid = player.getUniqueId();
		HashMap<UUID,PermissionAttachment> permissionAttachmentMap = main.getPermissionAttachmentMap();
		PermissionAttachment attachment = permissionAttachmentMap.get(uuid);
		
		player.removeAttachment(attachment);
		permissionAttachmentMap.remove(uuid);
		
		//Puis on redéfini ses permissions
		setPermissions(player);
	}
}
