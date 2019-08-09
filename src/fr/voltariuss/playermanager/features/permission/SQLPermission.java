package fr.voltariuss.playermanager.features.permission;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.simpledevapi.sql.SQLConnection;

public final class SQLPermission {

	/**
	 * Récupère les permissions spécifiques au joueur dans la base de données et les
	 * retourne.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return La liste des permissions du joueur, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	static ArrayList<String> getPermissions(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement(
				"SELECT permission FROM " + UtilsPlayerManager.TABLE_NAME_PERMISSIONS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());

		ResultSet resultat = query.executeQuery();
		ArrayList<String> permissions = new ArrayList<>();

		while (resultat.next()) {
			permissions.add(resultat.getString("permission"));
		}
		query.close();
		return permissions;
	}

	/**
	 * Ajoute une permission spécifique au joueur dans la base de données.
	 * 
	 * @param target     Le joueur ciblé, non null
	 * @param permission La permission à ajouter, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	static void addPermission(OfflinePlayer target, String permission) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("INSERT INTO " + UtilsPlayerManager.TABLE_NAME_PERMISSIONS + " VALUES(?,?)");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, permission.toLowerCase());
		query.execute();
		query.close();
	}

	/**
	 * Retire une permission spécifique au joueur de la base de données.
	 * 
	 * @param target     Le joueur ciblé, non null
	 * @param permission La permission a retirer, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	static void removePermission(OfflinePlayer target, String permission) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement(
				"DELETE FROM " + UtilsPlayerManager.TABLE_NAME_PERMISSIONS + " WHERE uuid = ? AND permission = ?");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, permission.toLowerCase());
		query.execute();
		query.close();
	}

	/**
	 * Retire toutes les permissions spécifiques au joueur de la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	static void removeAllPermissions(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("DELETE FROM " + UtilsPlayerManager.TABLE_NAME_PERMISSIONS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());
		query.execute();
		query.close();
	}
}
