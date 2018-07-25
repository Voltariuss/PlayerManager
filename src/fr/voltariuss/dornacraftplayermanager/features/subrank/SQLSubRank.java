package fr.voltariuss.dornacraftplayermanager.features.subrank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraftapi.DornacraftApi;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.cache.playercache.PlayerCache;
import fr.voltariuss.dornacraftplayermanager.features.perm.PermManager;

public class SQLSubRank {

	/**
	 * Récupère la liste des des sous-rangs du joueur et la retourne.
	 * 
	 * @param player Le joueur concerné.
	 * @return La liste des sous-rangs du joueur cible.
	 * @throws Exception 
	 */
	public ArrayList<SubRank> getSubRanks(OfflinePlayer player) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("SELECT subrank FROM F1_SubRank WHERE uuid = ?");
		query.setString(1, uuid.toString());
		
		ResultSet resultat = query.executeQuery();
		ArrayList<SubRank> subRankList = new ArrayList<>();
		
		while(resultat.next()) {
			subRankList.add(SubRank.fromString(resultat.getString("subrank")));
		}
		query.close();
		
		return subRankList;
	}
	
	/**
	 * Ajoute un sous-rang au joueur.
	 * 
	 * @param player Le joueur concerné.
	 * @param subRank Le sous-rang à ajouter.
	 * @throws Exception 
	 */
	public void addSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("INSERT INTO F1_SubRank VALUES(?,?)");
		query.setString(1, uuid.toString());
		query.setString(2, subRank.getName());
		query.execute();
		query.close();
		
		HashMap<UUID, PlayerCache> playerCacheMap = DornacraftPlayerManager.getInstance().getPlayerCacheMap();
		
		if(playerCacheMap.containsKey(uuid)) {
			PlayerCache playerCache = playerCacheMap.get(uuid);
			playerCache.getSubRanks().add(subRank);
		}
		
		if(Bukkit.getOnlinePlayers().contains(player)) {
			PermManager.updatePermissions(player.getPlayer());
		}
	}
	
	/**
	 * Retire un sous-rang au joueur.
	 * 
	 * @param player Le joueur concerné.
	 * @param subRank Le sous-rang à retirer.
	 * @throws Exception 
	 */
	public void removeSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("DELETE FROM F1_SubRank WHERE uuid = ? AND subrank = ?");
		query.setString(1, uuid.toString());
		query.setString(2, subRank.getName());
		query.execute();
		query.close();
		
		HashMap<UUID, PlayerCache> playerCacheMap = DornacraftPlayerManager.getInstance().getPlayerCacheMap();
		
		if(playerCacheMap.containsKey(uuid)) {
			PlayerCache playerCache = playerCacheMap.get(uuid);
			playerCache.getSubRanks().remove(subRank);
		}
		
		if(Bukkit.getOnlinePlayers().contains(player)) {
			PermManager.updatePermissions(player.getPlayer());
		}
	}
	
	/**
	 * Retire tous les sous-rangs du joueur.
	 * 
	 * @param player Le joueur concerné.
	 * @throws Exception
	 */
	public void removeAllSubRanks(OfflinePlayer player) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("DELETE FROM F1_SubRank WHERE uuid = ?");
		query.setString(1, uuid.toString());
		query.execute();
		query.close();
		
		HashMap<UUID, PlayerCache> playerCacheMap = DornacraftPlayerManager.getInstance().getPlayerCacheMap();
		
		if(playerCacheMap.containsKey(uuid)) {
			PlayerCache playerCache = playerCacheMap.get(uuid);
			playerCache.getSubRanks().clear();
		}
		
		if(Bukkit.getOnlinePlayers().contains(player)) {
			PermManager.updatePermissions(player.getPlayer());
		}
	}
	
	/**
	 * Vérifie si le joueur possède le sous-rang entré en paramètres.
	 * 
	 * @param player Le joueur concerné.
	 * @param subRank Le sous-rang à vérifier.
	 * @return "vrai" si le joueur possède le sous-rang.
	 * @throws Exception
	 */
	public boolean hasSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		ArrayList<SubRank> subRankList = getSubRanks(player);
		
		for(SubRank sr : subRankList) {
			if(sr == subRank) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSubRank(OfflinePlayer player) throws Exception {
		boolean hasSubRank = false;
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("SELECT subrank FROM F1_SubRank WHERE uuid = ?");
		query.setString(1, uuid.toString());
		
		ResultSet resultat = query.executeQuery();
		
		if(resultat.next()) {
			hasSubRank = true;
		} 
		query.close();
		return hasSubRank;
	}
}
