package fr.voltariuss.dornacraftplayermanager.features.subrank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraftapi.sql.SQLConnection;

public class SQLSubRank {
	
	public static final String SUBRANK_TABLE_NAME = "F1_SubRank";
	
	/**
	 * Récupère et retourne la liste des des sous-rangs du joueur depuis la base de données.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return La liste des sous-rangs du joueur ciblé, non null
	 * @throws SQLException 
	 */
	public static ArrayList<SubRank> getSubRanks(OfflinePlayer player) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT subrank FROM " + SUBRANK_TABLE_NAME + " WHERE uuid = ?");
		query.setString(1, player.getUniqueId().toString());
		
		ResultSet resultat = query.executeQuery();
		ArrayList<SubRank> subRankList = new ArrayList<>();
		
		while(resultat.next()) {
			subRankList.add(SubRank.fromString(resultat.getString("subrank")));
		}
		query.close();
		return subRankList;
	}
	
	/**
	 * Ajoute un sous-rang au joueur ciblé dans la base de données.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @param subRank Le sous-rang à ajouter au joueur ciblé, non null
	 * @throws SQLException 
	 */
	public static void addSubRank(OfflinePlayer player, SubRank subRank) throws SQLException {		
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("INSERT INTO " + SUBRANK_TABLE_NAME + " VALUES(?,?)");
		query.setString(1, player.getUniqueId().toString());
		query.setString(2, subRank.getName());
		query.execute();
		query.close();
	}
	
	/**
	 * Retire un sous-rang au joueur ciblé de la base de données.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @param subRank Le sous-rang à retirer du joueur ciblé, non null
	 * @throws SQLException 
	 */
	public static void removeSubRank(OfflinePlayer player, SubRank subRank) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("DELETE FROM " + SUBRANK_TABLE_NAME + " WHERE uuid = ? AND subrank = ?");
		query.setString(1, player.getUniqueId().toString());
		query.setString(2, subRank.getName());
		query.execute();
		query.close();
	}
	
	/**
	 * Retire tous les sous-rangs du joueur ciblé de la base de données.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void removeAllSubRanks(OfflinePlayer player) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("DELETE FROM " + SUBRANK_TABLE_NAME + " WHERE uuid = ?");
		query.setString(1, player.getUniqueId().toString());
		query.execute();
		query.close();
	}
}
