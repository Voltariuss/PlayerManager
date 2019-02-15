package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.playermanager.Utils;
import fr.voltariuss.dornacraft.sql.SQLConnection;

public final class SQLPrefixType {

	/**
	 * R�cup�re et retourne le type de pr�fixe du joueur cibl� depuis la base de donn�es.
	 * 
	 * @param target Le joueur cibl�, non null
	 * @return Le type de pr�fixe du joueur cibl�, non null
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
	 * Modifie le type de pr�fixe du joueur cibl� dans la base de donn�es.
	 * 
	 * @param player Le joueur cibl�, non null
	 * @param prefixType Le type de pr�fixe du joueur cibl�, non null
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
