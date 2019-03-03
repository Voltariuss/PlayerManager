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
	 * R�cup�re et retourne la liste des sous-rangs du joueur cibl�.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return La liste des sous-rangs du joueur cibl�
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
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
	 * Ajoute le sous-rang sp�cifi� au joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param subRank
	 *            Le sous-rang � ajouter au joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void addSubRank(CommandSender sender, OfflinePlayer target, SubRank subRank) throws SQLException {
		boolean hasAlreadySubRank = hasSubRank(target, subRank);

		if (!hasAlreadySubRank) {
			boolean terms = subRank == SubRank.VIP_PLUS && !hasSubRank(target, SubRank.VIP);

			if (terms) {
				SQLSubRank.addSubRank(target, SubRank.VIP);
			}
			SQLSubRank.addSubRank(target, subRank);
			// Actualisation des sous-rangs du joueur dans la m�moire centrale
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
	 * Retire le sous-rang sp�cifi� au joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param subRank
	 *            Le sous-rang � retirer au joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void removeSubRank(CommandSender sender, OfflinePlayer target, SubRank subRank) throws SQLException {
		boolean hasAlreadySubRank = hasSubRank(target, subRank);

		if (hasAlreadySubRank) {
			boolean terms = subRank == SubRank.VIP && hasSubRank(target, SubRank.VIP_PLUS);

			if (terms) {
				SQLSubRank.removeSubRank(target, SubRank.VIP_PLUS);
			}
			SQLSubRank.removeSubRank(target, subRank);
			// Actualisation des sous-rangs du joueur dans la m�moire centrale
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
	 * Retire tous les sous-rangs du joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void removeAllSubRank(CommandSender sender, OfflinePlayer target) throws SQLException {
		boolean hasSubRank = hasSubRank(target);

		if (hasSubRank) {
			ArrayList<SubRank> subRanks = getSubRanks(target);
			SQLSubRank.removeAllSubRanks(target);
			// Actualisation des sous-rangs du joueur dans la m�moire centrale
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
	 * V�rifie si le joueur poss�de le sous-rang sp�cifi�.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param subRank
	 *            Le sous-rang � v�rifier, non null
	 * @return True si le joueur poss�de le sous-rang, false sinon
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static boolean hasSubRank(OfflinePlayer target, SubRank subRank) throws SQLException {
		return getSubRanks(target).contains(subRank);
	}

	/**
	 * V�rifie si le joueur poss�de ou moins un sous-rang.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return True si le joueur cibl� poss�de ou moins un sous-rang, false sinon
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static boolean hasSubRank(OfflinePlayer target) throws SQLException {
		return !getSubRanks(target).isEmpty();
	}

	/**
	 * Envoie un message � l'�metteur de la requ�te comportant la liste des
	 * sous-rangs du joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, non null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void sendListSubRankMessage(CommandSender sender, OfflinePlayer target) throws SQLException {
		ArrayList<SubRank> subRanks = getSubRanks(target);
		boolean hasSubRank = !subRanks.isEmpty();

		if (hasSubRank) {
			StringJoiner list = new StringJoiner("\n�f - ");

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
