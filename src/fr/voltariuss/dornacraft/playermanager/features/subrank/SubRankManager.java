package fr.voltariuss.dornacraft.playermanager.features.subrank;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringJoiner;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.MessageLevel;
import fr.voltariuss.dornacraft.api.UtilsAPI;
import fr.voltariuss.dornacraft.playermanager.UtilsPlayerManager;
import fr.voltariuss.dornacraft.playermanager.features.permission.PermissionManager;
import fr.voltariuss.dornacraft.playermanager.features.prefix.PrefixManager;

/**
 * Classe de gestion des sous-rangs des joueurs
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class SubRankManager {

	/**
	 * Récupère et retourne la liste des sous-rangs du joueur ciblé.
	 * 
	 * @param target
	 *            Le joueur ciblé, non null
	 * @return La liste des sous-rangs du joueur ciblé
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static ArrayList<SubRank> getSubRanks(OfflinePlayer target) throws SQLException {
		ArrayList<SubRank> subRanks = new ArrayList<>();

		if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
			subRanks = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getSubRanks();
		} else {
			subRanks = SQLSubRank.getSubRanks(target);
		}
		return subRanks;
	}

	/**
	 * Ajoute le sous-rang spécifié au joueur ciblé.
	 * 
	 * @param sender
	 *            L'émetteur de la requête, peut être null
	 * @param target
	 *            Le joueur ciblé, non null
	 * @param subRank
	 *            Le sous-rang à ajouter au joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static void addSubRank(CommandSender sender, OfflinePlayer target, SubRank subRank) throws SQLException {
		boolean hasAlreadySubRank = hasSubRank(target, subRank);

		if (!hasAlreadySubRank) {
			boolean terms = subRank == SubRank.VIP_PLUS && !hasSubRank(target, SubRank.VIP);

			if (terms) {
				SQLSubRank.addSubRank(target, SubRank.VIP);
			}
			SQLSubRank.addSubRank(target, subRank);
			// Actualisation des sous-rangs du joueur dans la mémoire centrale
			if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getSubRanks().add(subRank);

				if (terms) {
					PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getSubRanks().add(SubRank.VIP);
				}
				PermissionManager.updatePermissions((Player) target);
			}
		}

		if (sender != null) {
			if (!hasAlreadySubRank) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.SUBRANK_AWARDED, subRank.getName(),
						target.getName());
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.SUBRANK_ALREADY_OWNED);
			}
		}
	}

	/**
	 * Retire le sous-rang spécifié au joueur ciblé.
	 * 
	 * @param sender
	 *            L'émetteur de la requête, peut être null
	 * @param target
	 *            Le joueur ciblé, non null
	 * @param subRank
	 *            Le sous-rang à retirer au joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static void removeSubRank(CommandSender sender, OfflinePlayer target, SubRank subRank) throws SQLException {
		boolean hasAlreadySubRank = hasSubRank(target, subRank);

		if (hasAlreadySubRank) {
			boolean terms = subRank == SubRank.VIP && hasSubRank(target, SubRank.VIP_PLUS);

			if (terms) {
				SQLSubRank.removeSubRank(target, SubRank.VIP_PLUS);
			}
			SQLSubRank.removeSubRank(target, subRank);
			// Actualisation des sous-rangs du joueur dans la mémoire centrale
			if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getSubRanks().remove(subRank);

				if (terms) {
					PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getSubRanks()
							.remove(SubRank.VIP_PLUS);
				}
				PermissionManager.updatePermissions((Player) target);
			}

			if (PrefixManager.getPrefixType(target).equalsIgnoreCase(subRank.getPrefix().name())) {
				PrefixManager.setPrefixType(null, target, UtilsPlayerManager.PREFIX_DEFAULT_TYPE);
			}
		}

		if (sender != null) {
			if (hasAlreadySubRank) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.SUBRANK_REMOVED, subRank.getName(),
						target.getName());
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.SUBRANK_NOT_OWNED);
			}
		}
	}

	/**
	 * Retire tous les sous-rangs du joueur ciblé.
	 * 
	 * @param sender
	 *            L'émetteur de la requête, peut être null
	 * @param target
	 *            Le joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static void removeAllSubRank(CommandSender sender, OfflinePlayer target) throws SQLException {
		boolean hasSubRank = hasSubRank(target);

		if (hasSubRank) {
			ArrayList<SubRank> subRanks = getSubRanks(target);
			SQLSubRank.removeAllSubRanks(target);
			// Actualisation des sous-rangs du joueur dans la mémoire centrale
			if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getSubRanks().clear();
				PermissionManager.updatePermissions((Player) target);
			}

			for (SubRank subRank : subRanks) {
				if (PrefixManager.getPrefixType(target).equalsIgnoreCase(subRank.getPrefix().name())) {
					PrefixManager.setPrefixType(null, target, UtilsPlayerManager.PREFIX_DEFAULT_TYPE);
					break;
				}
			}
		}

		if (sender != null) {
			if (hasSubRank) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.SUBRANK_ALL_REMOVED,
						target.getName());
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.SUBRANK_EMPTY_OWNED);
			}
		}
	}

	/**
	 * Vérifie si le joueur possède le sous-rang spécifié.
	 * 
	 * @param target
	 *            Le joueur ciblé, non null
	 * @param subRank
	 *            Le sous-rang à vérifier, non null
	 * @return True si le joueur possède le sous-rang, false sinon
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static boolean hasSubRank(OfflinePlayer target, SubRank subRank) throws SQLException {
		return getSubRanks(target).contains(subRank);
	}

	/**
	 * Vérifie si le joueur possède ou moins un sous-rang.
	 * 
	 * @param target
	 *            Le joueur ciblé, non null
	 * @return True si le joueur ciblé possède ou moins un sous-rang, false sinon
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static boolean hasSubRank(OfflinePlayer target) throws SQLException {
		return !getSubRanks(target).isEmpty();
	}

	/**
	 * Envoie un message à l'émetteur de la requête comportant la liste des
	 * sous-rangs du joueur ciblé.
	 * 
	 * @param sender
	 *            L'émetteur de la requête, non null
	 * @param target
	 *            Le joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static void sendListSubRankMessage(CommandSender sender, OfflinePlayer target) throws SQLException {
		ArrayList<SubRank> subRanks = getSubRanks(target);
		boolean hasSubRank = !subRanks.isEmpty();

		if (hasSubRank) {
			StringJoiner list = new StringJoiner("\n§f - ");

			for (SubRank subRank : subRanks) {
				list.add(subRank.getColoredName());
			}
			UtilsAPI.sendSystemMessage(MessageLevel.INFO, sender, UtilsPlayerManager.SUBRANK_LIST, target.getName(),
					list.toString());
		} else {
			UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.SUBRANK_EMPTY_OWNED);
		}
	}
}
