package fr.voltariuss.dornacraftplayermanager.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraftapi.DornacraftApi;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.Rank;
import fr.voltariuss.dornacraftplayermanager.playercache.PlayerCache;

public class SQLAccount {
	
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();

	/**
	 * Créer un compte au joueur s'il n'en possède pas.
	 * 
	 * @param player Joueur concerné.
	 * @throws Exception 
	 */
	public void checkAccount(Player player) throws Exception {
		if(!hasAccount(player)) {
			UUID uuid = player.getUniqueId();
			
			PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("INSERT INTO F1_Player(uuid,name) VALUES(?,?)");
			query.setString(1, uuid.toString());
			query.setString(2, player.getPlayerListName());
			query.execute();
			query.close();	
			updateLastLogin(player);
		}
	}
	
	/**
	 * Actualise la date de dernière connexion du joueur.
	 * 
	 * @param player Joueur concerné.
	 * @throws Exception 
	 */
	public void updateLastLogin(Player player) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("UPDATE F1_Player SET lastlogin = ? WHERE uuid = ?");
		query.setTimestamp(1, new Timestamp(new Date().getTime()));
		query.setString(2, uuid.toString());
		query.execute();
		query.close();
	}
	
	/**
	 * Récupère l'UUID d'un joueur à partir de son nom et le retourne.
	 * 
	 * @param player Le nom du joueur concerné.
	 * @return L'UUID correspondant au joueur ayant comme nom celui entré en paramètres. 
	 * @throws Exception
	 */
	public UUID getUUIDOfPlayer(String player) throws Exception {
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("SELECT uuid FROM F1_Player WHERE name = ?");
		query.setString(1, player);
		
		ResultSet resultat = query.executeQuery();
		UUID uuid = null;
		
		if(resultat.next()) {
			uuid = UUID.fromString(resultat.getString("uuid"));
		}
		query.close();
		
		return uuid;
	}
	
	/**
	 * Vérifie si le joueur possède un compte sur le serveur.
	 * 
	 * @param player Le joueur concerné.
	 * @return Retourne "vrai" si le joueur possède un compte.
	 * @throws Exception
	 */
	public boolean hasAccount(Player player) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("SELECT uuid FROM F1_Player WHERE uuid = ?");
		query.setString(1, uuid.toString());
		
		ResultSet resultat = query.executeQuery();
		boolean hasAccount = resultat.next();
		
		query.close();
		
		return hasAccount;
	}
	
	/**
	 * Récupère le rang du joueur dans la base de données.
	 * 
	 * @param player Le joueur concerné.
	 * @return Le rang du joueur.
	 * @throws Exception 
	 */
	public Rank getRank(OfflinePlayer player) throws Exception {
		Rank rank = Rank.getDefault();
		UUID uuid = player.getUniqueId();
		HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
		
		if(playerCacheMap.containsKey(uuid)) {
			PlayerCache playerCache = playerCacheMap.get(uuid);
			rank = playerCache.getRank();
		} else {
			PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("SELECT rank FROM F1_Player WHERE uuid = ?");
			query.setString(1, uuid.toString());
			
			ResultSet resultat = query.executeQuery();
			
			if(resultat.next()) {
				rank = Rank.fromString(resultat.getString("Rank"));
			} else {
				throw new Exception();
			}
		}
		
		return rank;
	}
	
	/**
	 * Définit le rang du joueur dans la base de données et en jeu puis actualise ses permissions.
	 * 
	 * @param player Le joueur concerné.
	 * @param newRank Le nouveau rang du joueur.
	 * @throws Exception 
	 */
	public void setRank(OfflinePlayer player, Rank newRank) throws Exception {
		UUID uuid = player.getUniqueId();
		
		//Modification du rang du joueur dans la base de données.
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("UPDATE F1_Player SET rank = ? WHERE uuid = ?");
		query.setString(1, newRank.getRankName());
		query.setString(2, uuid.toString());
		query.executeUpdate();
		query.close();
		
		//Modification du rang du joueur dans la mémoire centrale.
		HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
		
		if(playerCacheMap.containsKey(uuid)) {
			PlayerCache playerCache = playerCacheMap.get(uuid);
			playerCache.setRank(newRank);			
		}
		
		//Actualisation des permissions du joueur.
		if(Bukkit.getOnlinePlayers().contains(player)) {
			main.getPermissionManager().updatePermissions(player.getPlayer());
		}
	}
	
	/**
	 * Récupère et retourne le niveau du joueur ciblé.
	 * 
	 * @param player Le joueur concerné.
	 * @return Le niveau du joueur.
	 * @throws Exception
	 */
	public int getLevel(OfflinePlayer player) throws Exception {
		int level = 1;
		UUID uuid = player.getUniqueId();
		HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
		
		if(playerCacheMap.containsKey(uuid)) {
			PlayerCache playerCache = playerCacheMap.get(uuid);
			level = playerCache.getLevel();
		} else {
			PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("SELECT level FROM F1_Player WHERE uuid = ?");
			query.setString(1, uuid.toString());
			
			ResultSet resultat = query.executeQuery();
			
			if(resultat.next()) {
				level = resultat.getInt("level");
			} else {
				query.close();
				throw new Exception();
			}
			query.close();
		}
		
		return level;
	}
	
	/**
	 * Définit le niveau du joueur.
	 * 
	 * @param player Le joueur concerné.
	 * @param level Le nouveau niveau du joueur.
	 * @throws Exception
	 */
	public void setLevel(OfflinePlayer player, int level) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("UPDATE F1_Player SET level = ? WHERE uuid = ?");
		query.setInt(1, level);
		query.setString(2, uuid.toString());
		query.executeUpdate();
		query.close();
		
		HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
		
		if(playerCacheMap.containsKey(uuid)) {
			PlayerCache playerCache = playerCacheMap.get(uuid);
			playerCache.setLevel(level);
		}
	}
	
	/**
	 * Récupère et retourne le préfixe du joueur ciblé.
	 * 
	 * @param player Le joueur concenré.
	 * @return Le préfixe du joueur.
	 * @throws Exception
	 */
	public String getPrefixType(OfflinePlayer player) throws Exception {
		String prefixType = "";
		UUID uuid = player.getUniqueId();
		
		HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
		
		if(playerCacheMap.containsKey(uuid)) {
			PlayerCache playerCache = playerCacheMap.get(uuid);
			prefixType = playerCache.getPrefixType();
		} else {
			PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("SELECT prefix_type FROM F1_Player WHERE uuid = ?");
			query.setString(1, uuid.toString());
			
			ResultSet resultat = query.executeQuery();
			
			if(resultat.next()) {
				prefixType = resultat.getString("prefix_type");
			} else {
				query.close();
				throw new Exception();
			}
			query.close();
		}
		
		return prefixType;
	}
	
	/**
	 * Définit le préfixe du joueur ciblé.
	 * 
	 * @param player Le joueur concerné.
	 * @throws Exception
	 */
	public void setPrefixType(OfflinePlayer player, String prefixType) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("UPDATE F1_Player SET prefix_type = ? WHERE uuid = ?");
		query.setString(1, prefixType);
		query.setString(2, uuid.toString());
		query.executeUpdate();
		query.close();
		
		HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
		
		if(playerCacheMap.containsKey(uuid)) {
			PlayerCache playerCache = playerCacheMap.get(uuid);
			playerCache.setPrefixType(prefixType);
		}
	}
}