package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.playermanager.features.level.LevelManager;
import fr.voltariuss.dornacraft.playermanager.features.rank.RankManager;

public final class PrefixManager {
	
	/**
	 * R�cup�re le pr�fixe du joueur dans la m�moire centrale si il est connect�,
	 * dans la base de donn�es sinon.
	 * 
	 * @param target Le joueur cibl�, non null
	 * @return Le pr�fixe du joueur, non null
	 * @throws SQLException 
	 */
	public static String getPrefixType(OfflinePlayer target) throws SQLException {
		String prefixType = Prefix.getDefault();
		
		if(target.isOnline()) {
			prefixType = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getPrefixType();
		} else {
			prefixType = SQLPrefixType.getPrefixType(target);
		}
		return prefixType;
	}
	
	/**
	 * Modifie le type de pr�fixe du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @param prefixType Le nouveau type de pr�fixe du joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void setPrefixType(CommandSender sender, OfflinePlayer target, String prefixType) throws SQLException {
		SQLPrefixType.setPrefixType(target, prefixType);
		//Actualise le type de pr�fixe du joueur cibl� dans la m�moire centrale
		if(target.isOnline()) {
			PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setPrefixType(prefixType);
		}
		
		if(sender != null) {
			sender.sendMessage("�aPr�fixe modifi� avec succ�s !");
			sender.sendMessage("�ePr�fixe actuel : " + Prefix.fromString(getPrefixType(target), RankManager.getRank(target), LevelManager.getLevel(target)).toString());
		}
	}
}
