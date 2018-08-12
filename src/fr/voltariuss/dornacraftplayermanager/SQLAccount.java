package fr.voltariuss.dornacraftplayermanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.voltariuss.dornacraftapi.sql.SQLConnection;

public class SQLAccount {
	
	/**
	 * Cr�er un compte au joueur concern�.
	 * 
	 * @param player Le joueur concern�, non null
	 * @throws SQLException 
	 */
	public static void createAccount(Player player) throws SQLException {		
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("INSERT INTO F1_Player(uuid,name) VALUES(?,?)");
		query.setString(1, player.getUniqueId().toString());
		query.setString(2, player.getPlayerListName());
		query.execute();
		query.close();	
	}
	
	/**
	 * V�rifie si le joueur poss�de un compte sur le serveur.
	 * 
	 * @param player Le joueur concern�, non null
	 * @return Retourne "vrai" si le joueur poss�de un compte, "faux" sinon
	 * @throws SQLException
	 */
	public static boolean hasAccount(Player player) throws SQLException {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT uuid FROM F1_Player WHERE uuid = ?");
		query.setString(1, uuid.toString());
		
		ResultSet resultat = query.executeQuery();
		boolean hasAccount = resultat.next();
		
		query.close();
		
		return hasAccount;
	}
	
	/**
	 * Actualise la date de la derni�re connexion/deconnexion du joueur.
	 * 
	 * @param player Le joueur concern�, non null
	 * @throws SQLException 
	 */
	public static void updateLastLogin(Player player) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("UPDATE F1_Player SET lastlogin = ? WHERE uuid = ?");
		query.setTimestamp(1, new Timestamp(new Date().getTime()));
		query.setString(2, player.getUniqueId().toString());
		query.execute();
		query.close();
	}
	
	/**
	 * R�cup�re l'UUID d'un joueur � partir de son nom et le retourne.
	 * 
	 * @param playerName Le nom du joueur concern�, non null
	 * @return L'UUID correspondant au joueur ayant comme nom celui entr� en param�tres, peut �tre null
	 * @throws SQLException
	 */
	public static UUID getUUIDOfPlayer(String playerName) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT uuid FROM F1_Player WHERE name = ?");
		query.setString(1, playerName);
		
		ResultSet resultat = query.executeQuery();
		UUID uuid = resultat.next() ? UUID.fromString(resultat.getString("uuid")) : null;
		query.close();
		return uuid;
	}
}