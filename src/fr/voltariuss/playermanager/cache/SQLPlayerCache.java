package fr.voltariuss.playermanager.cache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.playermanager.features.rank.Rank;
import fr.voltariuss.simpledevapi.sql.SQLConnection;

public class SQLPlayerCache {

	/**
	 * Créer un nouveau cache pour le joueur et le retourne.
	 * 
	 * @param player
	 *            Le joueur concerné, non null
	 * @return Le cache du joueur, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static PlayerCache createPlayerCache(Player player) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement(
				"SELECT rank, prefix_type, level, xp FROM " + UtilsPlayerManager.TABLE_NAME_PLAYERS + " WHERE uuid = ?");
		query.setString(1, player.getUniqueId().toString());
		
		ResultSet resultat = query.executeQuery();
		resultat.next();
		
		Rank rank = Rank.valueOf(resultat.getString("rank"));
		int level = resultat.getInt("level");
		int xp = resultat.getInt("xp");
		String prefixType = resultat.getString("prefix_type");
		PlayerCache playerCache = new PlayerCache(rank, level, xp, prefixType);
		
		query.close();
		return playerCache;
	}
}
