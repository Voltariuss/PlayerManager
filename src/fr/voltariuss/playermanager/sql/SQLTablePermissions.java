package fr.voltariuss.playermanager.sql;

import java.sql.SQLException;
import java.sql.Statement;

import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.simpledevapi.sql.SQLConnection;

public class SQLTablePermissions {
	
	public static void createTableIfNotExists() throws SQLException {
		String query = "CREATE TABLE IF NOT EXISTS " + UtilsPlayerManager.TABLE_NAME_PERMISSIONS
		+ "("
		+ " 	uuid VARCHAR(36) PRIMARY KEY NOT NULL,"
		+ " 	permission VARCHAR(255)"
		+ ")";
		Statement statement = SQLConnection.getInstance().getConnection().createStatement();
		statement.executeUpdate(query);
		statement.close();
	}
}
