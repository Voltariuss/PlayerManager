package fr.voltariuss.dornacraft.playermanager.features.level;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.playermanager.Utils;
import fr.voltariuss.dornacraft.sql.SQLConnection;

public final class SQLLevel {
	
	/**
	 * Récupère et retourne le niveau du joueur ciblé depuis la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return Le niveau du joueur
	 * @throws SQLException
	 */
	static int getLevel(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement("SELECT level FROM " + Utils.TABLE_NAME_PLAYERS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());
		
		ResultSet resultat = query.executeQuery();
		resultat.next();
		int level = resultat.getInt("level");
		query.close();
		return level;
	}
	
	/**
	 * Modifie le niveau du joueur dans la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @param level Le nouveau niveau du joueur
	 * @throws SQLException
	 */
	static void setLevel(OfflinePlayer target, int level) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement("UPDATE " + Utils.TABLE_NAME_PLAYERS + " SET level = ? WHERE uuid = ?");
		query.setInt(1, level);
		query.setString(2, target.getUniqueId().toString());
		query.executeUpdate();
		query.close();
	}
	
	/**
	 * 
	 * Récupère et retourne la quantité d'xp du joueur ciblé depuis la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return La quantité d'xp du joueur
	 * @throws SQLException
	 */
	static int getXp(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement("SELECT xp FROM " + Utils.TABLE_NAME_PLAYERS + " WHERE uuid = ?");
		query.setString(0, target.getUniqueId().toString());
		
		ResultSet result = query.executeQuery();
		result.next();
		int xp = result.getInt("xp");
		query.close();
		return xp;
	}
	
	/**
	 * Modifie la quantité d'xp du joueur dans la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @param xp Le nouvelle quantité d'xp du joueur
	 * @throws SQLException
	 */
	static void setXp(OfflinePlayer target, int xp) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement("UPDATE " + Utils.TABLE_NAME_PLAYERS + " SET xp = ? WHERE uuid = ?");
		query.setInt(1, xp);
		query.setString(2, target.getUniqueId().toString());
		query.executeUpdate();
		query.close();
	}
}
