package fr.voltariuss.dornacraft.playermanager.features.permission;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.api.SQLConnection;
import fr.voltariuss.dornacraft.playermanager.Utils;

public final class SQLPermission {
	
	/**
	 * Récupère les permissions spécifiques au joueur dans la base de données et les retourne.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return La liste des permissions du joueur, non null
	 * @throws SQLException 
	 */
	static ArrayList<String> getPermissions(OfflinePlayer target) throws SQLException {		
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT permission FROM " + Utils.TABLE_NAME_PERMISSIONS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());
		
		ResultSet resultat = query.executeQuery();
		ArrayList<String> permissions = new ArrayList<>();
		
		while(resultat.next()) {
			permissions.add(resultat.getString("permission"));
		}		
		query.close();
		return permissions;
	}
		
	/**
	 * Ajoute une permission spécifique au joueur dans la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @param permission La permission à ajouter, non null
	 * @throws SQLException 
	 */
	static void addPermission(OfflinePlayer target, String permission) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("INSERT INTO " + Utils.TABLE_NAME_PERMISSIONS + " VALUES(?,?)");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, permission.toLowerCase());
		query.execute();
		query.close();
	}
	
	/**
	 * Retire une permission spécifique au joueur de la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @param permission La permission a retirer, non null
	 * @throws SQLException
	 */
	static void removePermission(OfflinePlayer target, String permission) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("DELETE FROM " + Utils.TABLE_NAME_PERMISSIONS + " WHERE uuid = ? AND permission = ?");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, permission.toLowerCase());
		query.execute();
		query.close();
	}
	
	/**
	 * Retire toutes les permissions spécifiques au joueur de la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException
	 */
	static void removeAllPermissions(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("DELETE FROM " + Utils.TABLE_NAME_PERMISSIONS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());
		query.execute();
		query.close();
	}
}
