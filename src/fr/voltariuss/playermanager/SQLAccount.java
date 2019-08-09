package fr.voltariuss.playermanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.voltariuss.simpledevapi.sql.SQLConnection;

public final class SQLAccount {

	/**
	 * Créer un compte au joueur.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	static void createAccount(Player target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("INSERT INTO " + UtilsPlayerManager.TABLE_NAME_PLAYERS + "(uuid,pseudo) VALUES(?,?)");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, target.getPlayerListName());
		query.execute();
		query.close();
	}

	/**
	 * Vérifie si le joueur possède un compte sur le serveur.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return Vrai si le joueur possède un compte
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	static boolean hasAccount(Player target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("SELECT uuid FROM " + UtilsPlayerManager.TABLE_NAME_PLAYERS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());

		ResultSet resultat = query.executeQuery();
		boolean hasAccount = resultat.next();
		query.close();
		return hasAccount;
	}

	/**
	 * Actualise la date de la dernière connexion/déconnexion du joueur ainsi que
	 * son pseudonyme.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	static void updateAccount(Player target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement(
				"UPDATE " + UtilsPlayerManager.TABLE_NAME_PLAYERS + " SET lastlogin = ?, pseudo = ? WHERE uuid = ?");
		query.setTimestamp(1, new Timestamp(new Date().getTime()));
		query.setString(2, target.getName());
		query.setString(3, target.getUniqueId().toString());
		query.execute();
		query.close();
	}

	/**
	 * Récupère l'UUID d'un joueur à partir de son nom et le retourne.
	 * 
	 * @param playerName Le nom du joueur ciblé, non null
	 * @return L'UUID correspondant au joueur ayant comme nom celui entré en
	 *         paramètres, peut être null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	static UUID getUUIDOfPlayer(String playerName) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("SELECT uuid FROM " + UtilsPlayerManager.TABLE_NAME_PLAYERS + " WHERE pseudo = ?");
		query.setString(1, playerName);

		ResultSet resultat = query.executeQuery();
		UUID uuid = resultat.next() ? UUID.fromString(resultat.getString("uuid")) : null;
		query.close();
		return uuid;
	}
}