package fr.voltariuss.dornacraft.playermanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.sql.SQLConnection;

/**
 * Classe comportant les requ�tes SQL relatives � la gestion du compte des
 * joueurs
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class SQLAccount {

	/**
	 * Cr�er un compte au joueur.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static void createAccount(Player target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("INSERT INTO " + Utils.TABLE_NAME_PLAYERS + "(uuid,pseudo) VALUES(?,?)");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, target.getPlayerListName());
		query.execute();
		query.close();
	}

	/**
	 * V�rifie si le joueur poss�de un compte sur le serveur.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return True si le joueur poss�de un compte, false sinon
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static boolean hasAccount(Player target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("SELECT uuid FROM " + Utils.TABLE_NAME_PLAYERS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());

		ResultSet resultat = query.executeQuery();
		boolean hasAccount = resultat.next();
		query.close();
		return hasAccount;
	}

	/**
	 * Actualise la date de la derni�re connexion/d�connexion du joueur ainsi que
	 * son pseudonyme.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static void updateAccount(Player target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement(
				"UPDATE " + Utils.TABLE_NAME_PLAYERS + " SET lastlogin = ?, pseudo = ? WHERE uuid = ?");
		query.setTimestamp(1, new Timestamp(new Date().getTime()));
		query.setString(2, target.getName());
		query.setString(3, target.getUniqueId().toString());
		query.execute();
		query.close();
	}

	/**
	 * R�cup�re l'UUID d'un joueur � partir de son nom et le retourne.
	 * 
	 * @param playerName
	 *            Le nom du joueur cibl�, non null
	 * @return L'UUID correspondant au joueur ayant comme nom celui entr� en
	 *         param�tres, peut �tre null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static UUID getUUIDOfPlayer(String playerName) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("SELECT uuid FROM " + Utils.TABLE_NAME_PLAYERS + " WHERE pseudo = ?");
		query.setString(1, playerName);

		ResultSet resultat = query.executeQuery();
		UUID uuid = resultat.next() ? UUID.fromString(resultat.getString("uuid")) : null;
		query.close();
		return uuid;
	}
}