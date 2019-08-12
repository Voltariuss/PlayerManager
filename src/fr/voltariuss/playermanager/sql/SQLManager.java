package fr.voltariuss.playermanager.sql;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import fr.voltariuss.simpledevapi.UtilsAPI;
import fr.voltariuss.simpledevapi.sql.SQLConnection;

public class SQLManager {

	private static boolean isDatabaseAvailable;

    public static void checkDatabase() {
        try {
            if (isDatabaseConnectionValid()) {
                setDatabaseAvailable(true);
                createTablesIfNotExists();
            } else {
                throw new SQLException();
            }
        } catch (SQLException e) {
            setDatabaseAvailable(false);
            Bukkit.getLogger().log(Level.WARNING, UtilsAPI.SQL_NO_CONNECTION);
            e.printStackTrace();
        }
    }

    public static boolean isDatabaseConnectionValid() {
        return SQLConnection.getInstance().getConnection() != null;
    }

    public static void createTablesIfNotExists() throws SQLException {
        SQLTablePlayers.createTableIfNotExists();
        SQLTablePermissions.createTableIfNotExists();
        SQLTableSubRanks.createTableIfNotExists();
    }

    public static boolean isDatabaseAvailable() {
        return isDatabaseAvailable;
    }

    public static void setDatabaseAvailable(boolean isDatabaseAvailable) {
        SQLManager.isDatabaseAvailable = isDatabaseAvailable;
    }
}
