package fr.voltariuss.dornacraft.playermanager.features.subrank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.playermanager.Utils;
import fr.voltariuss.dornacraft.sql.SQLConnection;

/**
 * Classe comportant les requ�tes SQL relatives � la gestion des sous-rangs des
 * joueurs
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class SQLSubRank {

	/**
	 * R�cup�re et retourne la liste des des sous-rangs du joueur depuis la base de
	 * donn�es.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return La liste des sous-rangs du joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static ArrayList<SubRank> getSubRanks(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("SELECT subrank FROM " + Utils.TABLE_NAME_SUBRANKS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());

		ResultSet resultat = query.executeQuery();
		ArrayList<SubRank> subRankList = new ArrayList<>();

		while (resultat.next()) {
			subRankList.add(SubRank.valueOf(resultat.getString("subrank")));
		}
		query.close();
		return subRankList;
	}

	/**
	 * Ajoute un sous-rang au joueur cibl� dans la base de donn�es.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param subRank
	 *            Le sous-rang � ajouter au joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static void addSubRank(OfflinePlayer target, SubRank subRank) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("INSERT INTO " + Utils.TABLE_NAME_SUBRANKS + " VALUES(?,?)");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, subRank.name());
		query.execute();
		query.close();
	}

	/**
	 * Retire un sous-rang au joueur cibl� de la base de donn�es.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param subRank
	 *            Le sous-rang � retirer du joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static void removeSubRank(OfflinePlayer target, SubRank subRank) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("DELETE FROM " + Utils.TABLE_NAME_SUBRANKS + " WHERE uuid = ? AND subrank = ?");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, subRank.name());
		query.execute();
		query.close();
	}

	/**
	 * Retire tous les sous-rangs du joueur cibl� de la base de donn�es.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static void removeAllSubRanks(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("DELETE FROM " + Utils.TABLE_NAME_SUBRANKS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());
		query.execute();
		query.close();
	}
}
