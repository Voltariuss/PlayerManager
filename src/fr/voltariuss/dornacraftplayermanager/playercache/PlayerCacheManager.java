package fr.voltariuss.dornacraftplayermanager.playercache;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.sql.SQLPlayerCache;

public class PlayerCacheManager {
	
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
	private SQLPlayerCache sqlPlayerCache = main.getSQLPlayerCache();
	
	/**
	 * Charge le cache du joueur.
	 * 
	 * @param player Le joueur concerné.
	 * @throws Exception 
	 */
	public void loadPlayerCache(Player player) throws Exception {
		UUID uuid = player.getUniqueId();
		HashMap<UUID,PlayerCache> playersCacheMap = main.getPlayerCacheMap();
		PlayerCache playerCache = sqlPlayerCache.createPlayerCache(player);
		playersCacheMap.put(uuid, playerCache);
		
	}
	
	/**
	 * Sauvegarde et décharge le cache du joueur.
	 * 
	 * @param player Le joueur concerné.
	 * @throws Exception 
	 */
	public void unloadPlayerCache(Player player) throws Exception {
		UUID uuid = player.getUniqueId();
		HashMap<UUID,PlayerCache> playersCacheMap = main.getPlayerCacheMap();
		sqlPlayerCache.savePlayerCache(player);
		playersCacheMap.remove(uuid);
	}
}
