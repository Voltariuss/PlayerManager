package fr.voltariuss.dornacraftplayermanager.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.voltariuss.dornacraftapi.DornacraftApi;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.Rank;
import fr.voltariuss.dornacraftplayermanager.playercache.PlayerCache;

public class SQLPlayerCache {
	
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();

	/**
	 * Créer un nouveau cache pour le joueur et le retourne.
	 * 
	 * @param player Le joueur concerné.
	 * @return Le cache du joueur sous le type PlayerCache.
	 * @throws Exception
	 */
	public PlayerCache createPlayerCache(Player player) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("SELECT rank,level,prefix_type FROM F1_Player WHERE uuid = ?");
		query.setString(1, uuid.toString());
		
		ResultSet resultat = query.executeQuery();
		PlayerCache playerCache = new PlayerCache();
		Rank rank = Rank.getDefault();
		int level = 1;
		String prefixType = "Default";
		
		if(resultat.next()) {
			rank = Rank.fromString(resultat.getString("rank"));
			level = resultat.getInt("level");
			prefixType = resultat.getString("prefix_type");
		}
		
		playerCache.setRank(rank);
		playerCache.setLevel(level);
		playerCache.setPrefixType(prefixType);
		
		query.close();
		
		return playerCache;
	}
	
	/**
	 * Sauvegarde le cache du joueur dans la base de données.
	 * 
	 * @param player Le joueur concerné.
	 * @throws Exception 
	 */
	public void savePlayerCache(Player player) throws Exception {
		UUID uuid = player.getUniqueId();
		HashMap<UUID,PlayerCache> playersCacheMap = main.getPlayerCacheMap();
		PlayerCache playerCache = playersCacheMap.get(uuid);
		Rank rank = playerCache.getRank();
		int level = playerCache.getLevel();
		String prefix = playerCache.getPrefixType(); 
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("UPDATE F1_Player SET level = ?, rank = ?, prefix_type = ? WHERE uuid = ?");
		query.setInt(1, level);
		query.setString(2, rank.getRankName());
		query.setString(3, prefix);;
		query.setString(4, uuid.toString());
		query.executeUpdate();
		query.close();
	}
}
