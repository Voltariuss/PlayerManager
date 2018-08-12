package fr.voltariuss.dornacraftplayermanager.features.prefix;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraftapi.sql.SQLConnection;

public class SQLPrefixType {

	/**
	 * R�cup�re et retourne le type de pr�fixe du joueur cibl� depuis la base de donn�es.
	 * 
	 * @param player Le joueur cibl�, non null
	 * @return Le type de pr�fixe du joueur cibl�, non null
	 * @throws SQLException
	 */
	public static String getPrefixType(OfflinePlayer player) throws SQLException {
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("SELECT prefix_type FROM F1_Player WHERE uuid = ?");
		query.setString(1, player.getUniqueId().toString());
		
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
	public static void setPrefixType(OfflinePlayer player, String prefixType) throws SQLException {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = SQLConnection.getConnection().prepareStatement("UPDATE F1_Player SET prefix_type = ? WHERE uuid = ?");
		query.setString(1, prefixType);
		query.setString(2, uuid.toString());
		query.executeUpdate();
		query.close();
	}
}
