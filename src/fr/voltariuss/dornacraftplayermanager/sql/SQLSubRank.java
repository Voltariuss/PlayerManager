package fr.voltariuss.dornacraftplayermanager.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraftapi.DornacraftApi;
import fr.voltariuss.dornacraftplayermanager.SubRank;

public class SQLSubRank {

	/**
	 * R�cup�re la liste des des sous-rangs du joueur et la retourne.
	 * 
	 * @param player Le joueur concern�.
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
	 * @param player Le joueur concern�.
	 * @param subRank Le sous-rang � ajouter.
	 * @throws Exception 
	 */
	public void addSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("INSERT INTO F1_SubRank VALUES(?,?)");
		query.setString(1, uuid.toString());
		query.setString(2, subRank.getName());
		query.execute();
		query.close();
	}
	
	/**
	 * Retire un sous-rang au joueur.
	 * 
	 * @param player Le joueur concern�.
	 * @param subRank Le sous-rang � retirer.
	 * @throws Exception 
	 */
	public void removeSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("DELETE FROM F1_SubRank WHERE uuid = ? AND subrank = ?");
		query.setString(1, uuid.toString());
		query.setString(2, subRank.getName());
		query.execute();
		query.close();
	}
	
	/**
	 * Retire tous les sous-rangs du joueur.
	 * 
	 * @param player Le joueur concern�.
	 * @throws Exception
	 */
	public void removeAllSubRanks(OfflinePlayer player) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("DELETE FROM F1_SubRank WHERE uuid = ?");
		query.setString(1, uuid.toString());
		query.execute();
		query.close();
	}
	
	/**
	 * V�rifie si le joueur poss�de le sous-rang entr� en param�tres.
	 * 
	 * @param player Le joueur concern�.
	 * @param subRank Le sous-rang � v�rifier.
	 * @return "vrai" si le joueur poss�de le sous-rang.
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
}
