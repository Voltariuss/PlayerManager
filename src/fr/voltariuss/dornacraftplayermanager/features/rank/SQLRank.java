package fr.voltariuss.dornacraftplayermanager.features.rank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraftapi.sql.SQLConnection;

public class SQLRank {

	/**
	 * Récupère le rang du joueur dans la base de données.
	 * 
	 * @param player Le joueur concerné.
	 * @return Le rang du joueur.
	 * @throws SQLException 
	 */
	public static Rank getRank(OfflinePlayer player) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT rank FROM F1_Player WHERE uuid = ?");
		query.setString(1, player.getUniqueId().toString());
		
		ResultSet resultat = query.executeQuery();
		Rank rank = Rank.getDefault();
		resultat.next();
		rank = Rank.fromString(resultat.getString("rank"));
		return rank;
	}
	
	/**
	 * Modifie le rang du joueur dans la base de données et en jeu puis actualise ses permissions.
	 * 
	 * @param player Le joueur concerné.
	 * @param newRank Le nouveau rang du joueur.
	 * @throws SQLException 
	 */
	public static void setRank(OfflinePlayer player, Rank rank) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("UPDATE F1_Player SET rank = ? WHERE uuid = ?");
		query.setString(1, rank.getName());
		query.setString(2, player.getUniqueId().toString());
		query.executeUpdate();
		query.close();
	}
}
