package fr.voltariuss.dornacraft.playermanager.features.level;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.api.SQLConnection;

public class SQLLevel {
	
	public static final String TABLE_NAME = "F1_Player";

	/**
	 * Récupère et retourne le niveau du joueur ciblé depuis la base de données.
	 * 
	 * @param player Le joueur concerné, non null
	 * @return Le niveau du joueur
	 * @throws SQLException
	 */
	static int getLevel(OfflinePlayer player) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT level FROM " + TABLE_NAME + " WHERE uuid = ?");
		query.setString(1, player.getUniqueId().toString());
		
		ResultSet resultat = query.executeQuery();
		resultat.next();
		int level = resultat.getInt("level");
		query.close();
		return level;
	}
	
	/**
	 * Modifie le niveau du joueur dans la base de données.
	 * 
	 * @param player Le joueur concerné, non null
	 * @param level Le nouveau niveau du joueur
	 * @throws SQLException
	 */
	static void setLevel(OfflinePlayer player, int level) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("UPDATE " + TABLE_NAME + " SET level = ? WHERE uuid = ?");
		query.setInt(1, level);
		query.setString(2, player.getUniqueId().toString());
		query.executeUpdate();
		query.close();
	}
}
