package fr.voltariuss.dornacraft.playermanager.features.rank;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.features.permission.PermissionManager;
import fr.voltariuss.dornacraft.playermanager.features.prefix.Prefix;
import fr.voltariuss.dornacraft.playermanager.features.prefix.PrefixManager;

public final class RankManager {
		
	public static final String HAS_HIGHEST_RANK = "Le joueur poss�de d�j� le rang le plus �lev�.";
	public static final String HAS_LOWER_RANK = "Le joueur poss�de d�j� le rang le plus bas.";
	public static final String ALREADY_HAS_RANK = "Le joueur poss�de d�j� ce rang.";
		
	/**
	 * R�cup�re le rank du joueur dans la m�moire centrale si il est connect�,
	 * dans la base de donn�es sinon.
	 * 
	 * @param target Le joueur cibl�, non null
	 * @return Le rank du joueur cibl�, non null
	 * @throws SQLException
	 */
	public static Rank getRank(OfflinePlayer target) throws SQLException {
		Rank rank = Rank.getDefault();
		
		if(target.isOnline()) {
			rank = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getRank();
		} else {
			rank = SQLRank.getRank(target);
		}
		return rank;
	}
	
	/**
	 * D�finit le rang du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @param rank Le rang � d�finir, non null
	 * @throws SQLException
	 */
	public static void setRank(CommandSender sender, OfflinePlayer target, Rank rank) throws SQLException {
		Rank playerRank = getRank(target);
		boolean hasAlreadyRank = playerRank == rank;
		
		if(!hasAlreadyRank) {
			SQLRank.setRank(target, rank);
			
			if(target.isOnline()) {
				//Actualise le rang du joueur dans la m�moire centrale si il est connect�
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setRank(rank);
				//Actualise les permissions du joueur
				PermissionManager.updatePermissions(target.getPlayer());
			}
			
			if(rank == Rank.MODERATEUR || rank == Rank.ADMINISTRATEUR) {
				PrefixManager.setPrefixType(null, target, Prefix.getDefault());
			}
		}
		
		if(sender != null) {
			if(!hasAlreadyRank) {
				sender.sendMessage("�aLe rang du joueur �b" + target.getName() + " �aa �t� modifi� avec succ�s !");
				sendRankInfoMessage(sender, target);
			} else {
				Utils.sendErrorMessage(sender, ALREADY_HAS_RANK);							
			}				
		}
	}
	
	/**
	 * Retire le rang du joueur cibl� et lui attribut celui par d�faut.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void removeRank(CommandSender sender, OfflinePlayer target) throws SQLException {
		Rank playerRank = getRank(target);
		boolean isDefaultRank = playerRank == Rank.getDefault();
		
		if(!isDefaultRank) {
			setRank(sender, target, Rank.getDefault());
		} else if(sender != null) {
			Utils.sendErrorMessage(sender, HAS_LOWER_RANK);
		}
	}
	
	/**
	 * Promeut le joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void promote(CommandSender sender, OfflinePlayer target) throws SQLException {
		Rank playerRank = getRank(target);
		boolean hasHigherRank = playerRank == Rank.values()[Rank.values().length - 1];
		
		if(!hasHigherRank) {
			setRank(sender, target, Rank.fromPower(playerRank.getPower() + 1));
		} else if(sender != null) {
			Utils.sendErrorMessage(sender, HAS_HIGHEST_RANK);
		}
	}
	
	/**
	 * R�trograde le joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void demote(CommandSender sender, OfflinePlayer target) throws SQLException {
		Rank playerRank = getRank(target);
		boolean hasLowerRank = playerRank == Rank.getDefault();
		
		if(!hasLowerRank) {
			setRank(sender, target, Rank.fromPower(playerRank.getPower() - 1));
		} else if(sender != null) {
			Utils.sendErrorMessage(sender, HAS_LOWER_RANK);
		}
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te contenant le rang du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void sendRankInfoMessage(CommandSender sender, OfflinePlayer target) throws SQLException {
		sender.sendMessage("�6Rang du joueur �b" + target.getName() + " �6: " + getRank(target));
	}
}
