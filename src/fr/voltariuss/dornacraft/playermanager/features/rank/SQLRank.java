package fr.voltariuss.dornacraft.playermanager.features.rank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.playermanager.UtilsPlayerManager;
import fr.voltariuss.dornacraft.sql.SQLConnection;

/**
 * Classe relative aux requêtes SQL correspants aux rangs des joueurs
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class SQLRank {

	/**
	 * Récupère le rang du joueur dans la base de données.
	 * 
	 * @param target
	 *            Le joueur ciblé, non null
	 * @return Le rang du joueur, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	static Rank getRank(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("SELECT rank FROM " + UtilsPlayerManager.TABLE_NAME_PLAYERS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());

		ResultSet resultat = query.executeQuery();
		resultat.next();
		Rank rank = Rank.valueOf(resultat.getString("rank"));
		return rank;
	}

	/**
	 * Modifie le rang du joueur dans la base de données.
	 * 
	 * @param target
	 *            Le joueur ciblé, non null
	 * @param rank
	 *            Le nouveau rang du joueur, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	static void setRank(OfflinePlayer target, Rank rank) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("UPDATE " + UtilsPlayerManager.TABLE_NAME_PLAYERS + " SET rank = ? WHERE uuid = ?");
		query.setString(1, rank.name());
		query.setString(2, target.getUniqueId().toString());
		query.executeUpdate();
		query.close();
	}
}
