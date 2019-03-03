package fr.voltariuss.dornacraft.playermanager.features.rank;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.MessageLevel;
import fr.voltariuss.dornacraft.api.UtilsAPI;
import fr.voltariuss.dornacraft.playermanager.UtilsPlayerManager;
import fr.voltariuss.dornacraft.playermanager.features.permission.PermissionManager;
import fr.voltariuss.dornacraft.playermanager.features.prefix.PrefixManager;

/**
 * Classe de gestion du rang d'un joueur
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class RankManager {

	/**
	 * R�cup�re le rank du joueur dans la m�moire centrale si il est connect�, dans
	 * la base de donn�es sinon.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return Le rank du joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static Rank getRank(OfflinePlayer target) throws SQLException {
		Rank rank = Rank.getDefault();

		if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
			rank = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getRank();
		} else {
			rank = SQLRank.getRank(target);
		}
		return rank;
	}

	/**
	 * D�finit le rang du joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param rank
	 *            Le rang � d�finir, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void setRank(CommandSender sender, OfflinePlayer target, Rank rank) throws SQLException {
		Rank playerRank = getRank(target);
		boolean hasAlreadyRank = playerRank == rank;

		if (!hasAlreadyRank) {
			SQLRank.setRank(target, rank);

			if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
				// Actualise le rang du joueur dans la m�moire centrale si il est connect�
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setRank(rank);
				// Actualise les permissions du joueur
				PermissionManager.updatePermissions(target.getPlayer());
			}

			if (rank == Rank.MODERATOR || rank == Rank.ADMIN) {
				PrefixManager.setPrefixType(null, target, UtilsPlayerManager.PREFIX_DEFAULT_TYPE);
			}
		}

		if (sender != null) {
			if (!hasAlreadyRank) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.RANK_UPDATED, target.getName());
				sendRankInfoMessage(sender, target);
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.RANK_ALREADY_OWNED);
			}
		}
	}

	/**
	 * Retire le rang du joueur cibl� et lui attribut celui par d�faut.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void removeRank(CommandSender sender, OfflinePlayer target) throws SQLException {
		Rank playerRank = getRank(target);
		boolean isDefaultRank = playerRank == Rank.getDefault();

		if (!isDefaultRank) {
			setRank(sender, target, Rank.getDefault());
		} else if (sender != null) {
			UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.RANK_HAS_LOWER);
		}
	}

	/**
	 * Promeut le joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void promote(CommandSender sender, OfflinePlayer target) throws SQLException {
		Rank playerRank = getRank(target);
		boolean hasHigherRank = playerRank == Rank.values()[Rank.values().length - 1];

		if (!hasHigherRank) {
			setRank(sender, target, Rank.fromPower(playerRank.getPower() + 1));
		} else if (sender != null) {
			UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.RANK_HAS_HIGHEST);
		}
	}

	/**
	 * R�trograde le joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void demote(CommandSender sender, OfflinePlayer target) throws SQLException {
		Rank playerRank = getRank(target);
		boolean hasLowerRank = playerRank == Rank.getDefault();

		if (!hasLowerRank) {
			setRank(sender, target, Rank.fromPower(playerRank.getPower() - 1));
		} else if (sender != null) {
			UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.RANK_HAS_LOWER);
		}
	}

	/**
	 * Envoie un message � l'�metteur de la requ�te contenant le rang du joueur
	 * cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, non null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void sendRankInfoMessage(CommandSender sender, OfflinePlayer target) throws SQLException {
		UtilsAPI.sendSystemMessage(MessageLevel.INFO, sender, UtilsPlayerManager.RANK_INFO, target.getName(), getRank(target).getColoredName());
	}
}
