package fr.voltariuss.dornacraftplayermanager.features.level;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraftapi.sql.SQLConnection;

public class SQLLevel {

	/**
	 * Récupère et retourne le niveau du joueur ciblé depuis la base de données.
	 * 
	 * @param player Le joueur concerné, non null
	 * @return Le niveau du joueur
	 * @throws SQLException
	 */
	public static int getLevel(OfflinePlayer player) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT level FROM F1_Player WHERE uuid = ?");
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
	public static void setLevel(OfflinePlayer player, int level) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("UPDATE F1_Player SET level = ? WHERE uuid = ?");
		query.setInt(1, level);
		query.setString(2, player.getUniqueId().toString());
		query.executeUpdate();
		query.close();
	}
}
