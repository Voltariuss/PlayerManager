package fr.voltariuss.dornacraft.playermanager.features.level;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.api.SQLConnection;
import fr.voltariuss.dornacraft.playermanager.Utils;

public final class SQLLevel {
	
	/**
	 * Récupère et retourne le niveau du joueur ciblé depuis la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return Le niveau du joueur
	 * @throws SQLException
	 */
	static int getLevel(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT level FROM " + Utils.TABLE_NAME_PLAYERS + " WHERE uuid = ?");
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
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("UPDATE " + Utils.TABLE_NAME_PLAYERS + " SET level = ? WHERE uuid = ?");
		query.setInt(1, level);
		query.setString(2, target.getUniqueId().toString());
		query.executeUpdate();
		query.close();
	}
}
