package fr.voltariuss.dornacraft.playermanager.features.subrank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.api.SQLConnection;
import fr.voltariuss.dornacraft.playermanager.Utils;

public final class SQLSubRank {
		
	/**
	 * Récupère et retourne la liste des des sous-rangs du joueur depuis la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return La liste des sous-rangs du joueur ciblé, non null
	 * @throws SQLException 
	 */
	static ArrayList<SubRank> getSubRanks(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT subrank FROM " + Utils.TABLE_NAME_SUBRANKS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());
		
		ResultSet resultat = query.executeQuery();
		ArrayList<SubRank> subRankList = new ArrayList<>();
		
		while(resultat.next()) {
			subRankList.add(SubRank.valueOf(resultat.getString("subrank")));
		}
		query.close();
		return subRankList;
	}
	
	/**
	 * Ajoute un sous-rang au joueur ciblé dans la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @param subRank Le sous-rang à ajouter au joueur ciblé, non null
	 * @throws SQLException 
	 */
	static void addSubRank(OfflinePlayer target, SubRank subRank) throws SQLException {		
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("INSERT INTO " + Utils.TABLE_NAME_SUBRANKS + " VALUES(?,?)");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, subRank.name());
		query.execute();
		query.close();
	}
	
	/**
	 * Retire un sous-rang au joueur ciblé de la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @param subRank Le sous-rang à retirer du joueur ciblé, non null
	 * @throws SQLException 
	 */
	static void removeSubRank(OfflinePlayer target, SubRank subRank) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("DELETE FROM " + Utils.TABLE_NAME_SUBRANKS + " WHERE uuid = ? AND subrank = ?");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, subRank.name());
		query.execute();
		query.close();
	}
	
	/**
	 * Retire tous les sous-rangs du joueur ciblé de la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException
	 */
	static void removeAllSubRanks(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("DELETE FROM " + Utils.TABLE_NAME_SUBRANKS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());
		query.execute();
		query.close();
	}
}
