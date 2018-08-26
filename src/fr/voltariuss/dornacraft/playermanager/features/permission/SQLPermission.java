package fr.voltariuss.dornacraft.playermanager.features.permission;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.api.SQLConnection;

public class SQLPermission {
	
	/**
	 * R�cup�re les permissions sp�cifiques au joueur dans la base de donn�es et les retourne.
	 * 
	 * @param player Le joueur concern�, non null
	 * @return La liste des permissions du joueur, non null
	 * @throws SQLException 
	 */
	public static ArrayList<String> getPermissions(OfflinePlayer player) throws SQLException {		
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT permission FROM F1_Perm WHERE uuid = ?");
		query.setString(1, player.getUniqueId().toString());
		
		ResultSet resultat = query.executeQuery();
		ArrayList<String> permissions = new ArrayList<>();
		
		while(resultat.next()) {
			permissions.add(resultat.getString("permission"));
		}		
		query.close();
		return permissions;
	}
		
	/**
	 * Ajoute une permission sp�cifique au joueur dans la base de donn�es.
	 * 
	 * @param player Le joueur concern�, non null
	 * @param permission La permission � ajouter, non null
	 * @throws SQLException 
	 */
	public static void addPermission(OfflinePlayer player, String permission) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("INSERT INTO F1_Perm VALUES(?,?)");
		query.setString(1, player.getUniqueId().toString());
		query.setString(2, permission.toLowerCase());
		query.execute();
		query.close();
	}
	
	/**
	 * Retire une permission sp�cifique au joueur de la base de donn�es.
	 * 
	 * @param player Le joueur concern�e, non null
	 * @param permission La permission a retirer, non null
	 * @throws SQLException
	 */
	public static void removePermission(OfflinePlayer player, String permission) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("DELETE FROM F1_Perm WHERE uuid = ? AND permission = ?");
		query.setString(1, player.getUniqueId().toString());
		query.setString(2, permission.toLowerCase());
		query.execute();
		query.close();
	}
	
	/**
	 * Retire toutes les permissions sp�cifiques au joueur de la base de donn�es.
	 * 
	 * @param player Le joueur concern�, non null
	 * @throws SQLException
	 */
	public static void removeAllPermissions(OfflinePlayer player) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("DELETE FROM F1_Perm WHERE uuid = ?");
		query.setString(1, player.getUniqueId().toString());
		query.execute();
		query.close();
	}
}
