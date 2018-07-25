package fr.voltariuss.dornacraftplayermanager.cache.playercache;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;

public class PlayerCacheManager {
		
	/**
	 * Charge le cache du joueur.
	 * 
	 * @param player Le joueur concerné.
	 * @throws Exception 
	 */
	public void loadPlayerCache(Player player) throws Exception {
		UUID uuid = player.getUniqueId();
		HashMap<UUID,PlayerCache> playersCacheMap = DornacraftPlayerManager.getInstance().getPlayerCacheMap();
		PlayerCache playerCache = DornacraftPlayerManager.getInstance().getSQLPlayerCache().createPlayerCache(player);
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
		HashMap<UUID,PlayerCache> playersCacheMap = DornacraftPlayerManager.getInstance().getPlayerCacheMap();
		DornacraftPlayerManager.getInstance().getSQLPlayerCache().savePlayerCache(player);
		playersCacheMap.remove(uuid);
	}
}
