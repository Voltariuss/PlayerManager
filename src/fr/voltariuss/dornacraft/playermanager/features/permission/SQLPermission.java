package fr.voltariuss.dornacraft.playermanager.features.permission;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.playermanager.Utils;
import fr.voltariuss.dornacraft.sql.SQLConnection;

/**
 * Classe comportant les requ�tes SQL relatives � la gestion des permissions des
 * joueurs
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class SQLPermission {

	/**
	 * R�cup�re les permissions sp�cifiques au joueur dans la base de donn�es et les
	 * retourne.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return La liste des permissions du joueur, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static ArrayList<String> getPermissions(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("SELECT permission FROM " + Utils.TABLE_NAME_PERMISSIONS + " WHERE uuid = ?");
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
	 * Ajoute une permission sp�cifique au joueur dans la base de donn�es.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param permission
	 *            La permission � ajouter, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static void addPermission(OfflinePlayer target, String permission) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("INSERT INTO " + Utils.TABLE_NAME_PERMISSIONS + " VALUES(?,?)");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, permission.toLowerCase());
		query.execute();
		query.close();
	}

	/**
	 * Retire une permission sp�cifique au joueur de la base de donn�es.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param permission
	 *            La permission a retirer, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static void removePermission(OfflinePlayer target, String permission) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("DELETE FROM " + Utils.TABLE_NAME_PERMISSIONS + " WHERE uuid = ? AND permission = ?");
		query.setString(1, target.getUniqueId().toString());
		query.setString(2, permission.toLowerCase());
		query.execute();
		query.close();
	}

	/**
	 * Retire toutes les permissions sp�cifiques au joueur de la base de donn�es.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	static void removeAllPermissions(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection()
				.prepareStatement("DELETE FROM " + Utils.TABLE_NAME_PERMISSIONS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());
		query.execute();
		query.close();
	}
}
