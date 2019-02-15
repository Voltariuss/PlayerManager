package fr.voltariuss.dornacraft.playermanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import fr.voltariuss.dornacraft.sql.SQLConnection;

public class SQLDatabase {
	
	static void checkTables() {
		HashMap<String, ArrayList<String>> tableMap = new HashMap<>();
		tableMap.put(Utils.TABLE_NAME_PLAYERS, new ArrayList<>(Arrays.asList(
				""
				)));
		tableMap.put(Utils.TABLE_NAME_SUBRANKS, new ArrayList<>(Arrays.asList()));
		tableMap.put(Utils.TABLE_NAME_PERMISSIONS, new ArrayList<>(Arrays.asList()));
		
		for(String table : tableMap.keySet()) {
			try {
				PreparedStatement query = SQLConnection.getInstance().getConnection().prepareStatement("SHOW TABLES LIKE ?");
				query.setString(0, table);
				
				ResultSet result = query.executeQuery();
				
				if(!result.next()) {
					PreparedStatement query2 = SQLConnection.getInstance().getConnection().prepareStatement("CREATE TABLE ? (" + tableMap.get(table) + ")");
					query2.execute();
					query2.close();
				}
				query.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
}
