package fr.voltariuss.playermanager.features.rank;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.playermanager.features.permission.PermissionManager;
import fr.voltariuss.playermanager.features.prefix.PrefixManager;
import fr.voltariuss.simpledevapi.MessageLevel;
import fr.voltariuss.simpledevapi.UtilsAPI;

public final class RankManager {

	/**
	 * Récupère le rank du joueur dans la mémoire centrale s'il est connecté, dans
	 * la base de données sinon.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return Le rank du joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
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
	 * Définit le rang du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param target Le joueur ciblé, non null
	 * @param rank   Le rang à définir, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void setRank(CommandSender sender, OfflinePlayer target, Rank rank) throws SQLException {
		Rank playerRank = getRank(target);
		boolean hasAlreadyRank = playerRank == rank;

		if (!hasAlreadyRank) {
			SQLRank.setRank(target, rank);

			if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
				// Actualise le rang du joueur dans la mémoire centrale s'il est connecté
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
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.RANK_UPDATED,
						target.getName());
				sendRankInfoMessage(sender, target);
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.RANK_ALREADY_OWNED);
			}
		}
	}

	/**
	 * Retire le rang du joueur ciblé et lui attribut celui par défaut.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
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
	 * Promeut le joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
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
	 * Rétrograde le joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
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
	 * Envoie un message à l'émetteur de la requête contenant le rang du joueur
	 * ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void sendRankInfoMessage(CommandSender sender, OfflinePlayer target) throws SQLException {
		UtilsAPI.sendSystemMessage(MessageLevel.INFO, sender, UtilsPlayerManager.RANK_INFO, target.getName(),
				getRank(target).getColoredName());
	}
}
