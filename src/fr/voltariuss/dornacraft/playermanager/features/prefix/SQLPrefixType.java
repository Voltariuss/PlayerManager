package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.playermanager.Utils;
import fr.voltariuss.dornacraft.sql.SQLConnection;

public final class SQLPrefixType {

	/**
	 * Récupère et retourne le type de préfixe du joueur ciblé depuis la base de données.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return Le type de préfixe du joueur ciblé, non null
	 * @throws SQLException
	 */
	static String getPrefixType(OfflinePlayer target) throws SQLException {
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement("SELECT prefix_type FROM " + Utils.TABLE_NAME_PLAYERS + " WHERE uuid = ?");
		query.setString(1, target.getUniqueId().toString());
		
		ResultSet resultat = query.executeQuery();
		String prefixType = Prefix.getDefault();
		resultat.next();
		prefixType = resultat.getString("prefix_type");
		query.close();
		return prefixType;
	}
	
	/**
	 * Modifie le type de préfixe du joueur ciblé dans la base de données.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @param prefixType Le type de préfixe du joueur ciblé, non null
	 * @throws SQLException
	 */
	static void setPrefixType(OfflinePlayer target, String prefixType) throws SQLException {		
		PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement("UPDATE " + Utils.TABLE_NAME_PLAYERS + " SET prefix_type = ? WHERE uuid = ?");
		query.setString(1, prefixType);
		query.setString(2, target.getUniqueId().toString());
		query.executeUpdate();
		query.close();
	}
}
