package fr.voltariuss.playermanager.cache;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.voltariuss.playermanager.AccountManager;
import fr.voltariuss.playermanager.features.permission.PermissionManager;
import fr.voltariuss.playermanager.features.subrank.SubRankManager;

public class PlayerCacheManager {

	private static final HashMap<UUID, PlayerCache> playerCacheMap = new HashMap<>();

	/**
	 * @return La liste des UUID de joueurs correspondant pour chaque
	 *         {@link PlayerCache}, non null
	 */
	public static HashMap<UUID, PlayerCache> getPlayerCacheMap() {
		return playerCacheMap;
	}

	/**
	 * Charge le cache du joueur.
	 * 
	 * @param player
	 *            Le joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static void loadPlayerCache(Player player) throws SQLException {
		AccountManager.checkAccount(player);

		PlayerCache playerCache = SQLPlayerCache.createPlayerCache(player);
		playerCache.setSubRanks(SubRankManager.getSubRanks(player));
		getPlayerCacheMap().put(player.getUniqueId(), playerCache);
		PermissionManager.setPermissions(player);
	}

	/**
	 * Sauvegarde et décharge le cache du joueur.
	 * 
	 * @param player
	 *            Le joueur concerné, non null
	 */
	public static void unloadPlayerCache(Player player) {
		PermissionManager.getPermissionAttachmentMap().remove(player.getUniqueId());
		getPlayerCacheMap().remove(player.getUniqueId());
	}
}
