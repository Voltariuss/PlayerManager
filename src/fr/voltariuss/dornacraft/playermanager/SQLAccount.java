package fr.voltariuss.dornacraft.playermanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.SQLConnection;

public final class SQLAccount {
	
	/**
	 * Créer un compte au joueur.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException 
	 */
	static void createAccount(Player target) throws SQLException {		
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("INSERT INTO F1_Player(uuid,name) VALUES(?,?)");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, target.getPlayerListName());
		query.execute();
		query.close();	
	}
	
	/**
	 * Vérifie si le joueur possède un compte sur le serveur.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return Retourne "vrai" si le joueur possède un compte
	 * @throws SQLException
	 */
	static boolean hasAccount(Player target) throws SQLException {		
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT uuid FROM F1_Player WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());
		
		ResultSet resultat = query.executeQuery();
		boolean hasAccount = resultat.next();
		query.close();
		return hasAccount;
	}
	
	/**
	 * Actualise la date de la dernière connexion/deconnexion du joueur.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException 
	 */
	static void updateLastLogin(Player target) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("UPDATE F1_Player SET lastlogin = ? WHERE uuid = ?");
		query.setTimestamp(1, new Timestamp(new Date().getTime()));
		query.setString(2, target.getUniqueId().toString());
		query.execute();
		query.close();
	}
	
	/**
	 * Récupère l'UUID d'un joueur à partir de son nom et le retourne.
	 * 
	 * @param playerName Le nom du joueur ciblé, non null
	 * @return L'UUID correspondant au joueur ayant comme nom celui entré en paramètres, peut être null
	 * @throws SQLException
	 */
	static UUID getUUIDOfPlayer(String playerName) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT uuid FROM F1_Player WHERE name = ?");
		query.setString(1, playerName);
		
		ResultSet resultat = query.executeQuery();
		UUID uuid = resultat.next() ? UUID.fromString(resultat.getString("uuid")) : null;
		query.close();
		return uuid;
	}
}