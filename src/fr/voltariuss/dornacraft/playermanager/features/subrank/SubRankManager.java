package fr.voltariuss.dornacraft.playermanager.features.subrank;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.msgs.MessageLevel;
import fr.voltariuss.dornacraft.api.msgs.MessageUtils;
import fr.voltariuss.dornacraft.playermanager.features.permission.PermissionManager;
import fr.voltariuss.dornacraft.playermanager.features.prefix.Prefix;
import fr.voltariuss.dornacraft.playermanager.features.prefix.PrefixManager;

/**
 * Classe de gestion des sous-rangs des joueurs
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class SubRankManager {

	public static final String UNKNOW_SUBRANK = "Le sous-rang spécifié est incorrect.";
	public static final String HAS_ALREADY_SUBRANK = "Ce joueur possède déjà le sous-rang spécifié.";
	public static final String DONT_HAS_SPECIFIED_SUBRANK = "Ce joueur ne possède pas le sous-rang spécifié.";
	public static final String DONT_HAS_SUBRANK = "Ce joueur ne possède pas de sous-rang.";

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
				sender.sendMessage("§aLe sous-rang §6" + subRank.getName() + " §aa bien été attribué au joueur §b"
						+ target.getName() + "§a.");
			} else {
				MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender, HAS_ALREADY_SUBRANK);
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
				PrefixManager.setPrefixType(null, target, Prefix.getDefault());
			}
		}

		if (sender != null) {
			if (hasAlreadySubRank) {
				sender.sendMessage("§aLe sous-rang §6" + subRank.getName() + " §aa bien été retiré au joueur §b"
						+ target.getName() + "§a.");
			} else {
				MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender, DONT_HAS_SPECIFIED_SUBRANK);
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
					PrefixManager.setPrefixType(null, target, Prefix.getDefault());
					break;
				}
			}
		}

		if (sender != null) {
			if (hasSubRank) {
				sender.sendMessage("§aTous les sous-rangs ont été retirés au joueur §b" + target.getName() + "§a.");
			} else {
				MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender, DONT_HAS_SUBRANK);
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
			String strSubRanks = "";

			for (SubRank subRank : subRanks) {
				strSubRanks += "\n§f - " + subRank.getColoredName();
			}
			sender.sendMessage("§6Liste des sous-rangs du joueur §b" + target.getName() + " §6: " + strSubRanks);
		} else {
			MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender, DONT_HAS_SUBRANK);
		}
	}
}
