package fr.voltariuss.playermanager.features.level;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.playermanager.cache.PlayerCacheManager;
import fr.voltariuss.simpledevapi.MessageLevel;
import fr.voltariuss.simpledevapi.UtilsAPI;

public final class LevelManager {

	/**
	 * Récupère le niveau du joueur dans la mémoire centrale s'il est connecté, dans
	 * la base de données sinon.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return Le niveau du joueur ciblé
	 * @throws SQLException Si une erreur avec la base de donn�es est détectée
	 */
	public static int getLevel(OfflinePlayer target) throws SQLException {
		int level = UtilsPlayerManager.LEVEL_MIN;

		if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
			level = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getLevel();
		} else {
			level = SQLLevel.getLevel(target);
		}
		return level;
	}

	/**
	 * Définit le niveau du joueur.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param target Le joueur ciblé, non null
	 * @param level  Le nouveau niveau du joueur
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void setLevel(CommandSender sender, OfflinePlayer target, int level) throws SQLException {
		boolean isInInterval = level >= UtilsPlayerManager.LEVEL_MIN && level <= UtilsPlayerManager.LEVEL_MAX;

		if (isInInterval) {
			SQLLevel.setLevel(target, level);
			SQLLevel.setXp(target, 0);

			if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setLevel(level);
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setXp(0);
			}
		}

		if (sender != null) {
			if (isInInterval) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.LEVEL_SET, target.getName(),
						level);
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.NUMBER_MUST_IN_INTERVAL,
						UtilsPlayerManager.LEVEL_MIN, UtilsPlayerManager.LEVEL_MAX);
			}
		}
	}

	/**
	 * Récupère la quantité d'xp du joueur dans la mémoire centrale s'il est
	 * connecté, dans la base de données sinon.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return La quantité d'xp du joueur ciblé.
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static int getXp(OfflinePlayer target) throws SQLException {
		int xp = 0;

		if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
			xp = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getXp();
		} else {
			xp = SQLLevel.getXp(target);
		}
		return xp;
	}

	/**
	 * Calcule la quantité d'xp totale nécessaire pour pouvoir passer au niveau
	 * suivant par rapport au niveau spécifié.
	 * 
	 * @param level Le niveau correspondant
	 * @return La quantité d'xp totale nécessaire pour monter au niveau supérieur
	 */
	public static int getTotalXp(int level) {
		if (level >= UtilsPlayerManager.LEVEL_MIN && level < UtilsPlayerManager.LEVEL_MAX) {
			double op1 = Math.pow(1.14 + 3 / (level + 1), level + 1);
			double op2 = Math.pow(level / 65, level + 1);
			double op3 = 300 * Math.pow(level, 1.95) * Math.pow(0.99, level);
			return (int) (op1 + op2 + op3 + 1798);
		} else {
			return 0;
		}
	}

	/**
	 * Définit la quantité d'xp du joueur. Si le total d'xp est atteint, le niveau
	 * est augmenté.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param target Le joueur ciblé, non null
	 * @param xp     La nouvelle quantité d'xp du joueur
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void setXp(CommandSender sender, OfflinePlayer target, int xp) throws SQLException {
		int level = getLevel(target);
		boolean isInInterval = xp >= 0 && xp < getTotalXp(level);

		if (isInInterval) {
			SQLLevel.setXp(target, xp);

			if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setXp(xp);
			}
		}

		if (sender != null) {
			if (isInInterval) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.LEVEL_XP_SET,
						target.getName(), xp);
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.NUMBER_MUST_IN_INTERVAL, 0,
						getTotalXp(level));
			}
		}
	}

	/**
	 * Ajoute le nombre de niveaux spécifiés au joueur ciblé.
	 * 
	 * @param sender  L'émetteur de la requête, peut être null
	 * @param target  Le joueur ciblé, non null
	 * @param nbLevel Le nombre de niveaux à ajouter
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void addLevel(CommandSender sender, OfflinePlayer target, int nbLevel) throws SQLException {
		int level = getLevel(target);
		int newLevel = level;
		boolean isPositiveLevel = nbLevel > 0;
		boolean isMaxLevel = level == UtilsPlayerManager.LEVEL_MAX;

		if (isPositiveLevel && !isMaxLevel) {
			if (newLevel + nbLevel > UtilsPlayerManager.LEVEL_MAX) {
				nbLevel = UtilsPlayerManager.LEVEL_MAX - level;
			}
			newLevel += nbLevel;
			setLevel(null, target, newLevel);
		}

		if (sender != null) {
			if (isPositiveLevel && !isMaxLevel) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.LEVEL_RECEIVED,
						target.getName(), nbLevel);
				UtilsAPI.sendSystemMessage(MessageLevel.INFO, sender, UtilsPlayerManager.LEVEL_UPDATED,
						target.getName(), newLevel);
			} else if (isMaxLevel) {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.LEVEL_MAX_ALREADY_REACH);
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.NUMBER_MUST_BE_STRICTLY_POSITIVE);
			}
		}
	}

	/**
	 * Ajoute la quantité d'xp spécifiée au joueur ciblé.
	 * 
	 * @param sender  L'émetteur de la requête, peut être null
	 * @param target  Le joueur ciblé, non null
	 * @param xpToAdd La quantité d'xp à ajouter
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void addXp(CommandSender sender, OfflinePlayer target, int xpToAdd) throws SQLException {
		int level = getLevel(target);
		boolean isAlterable = xpToAdd > 0 && level < UtilsPlayerManager.LEVEL_MAX;

		if (isAlterable) {
			xpToAdd += getXp(target);
			int totalXp = getTotalXp(level);

			while (level != UtilsPlayerManager.LEVEL_MAX && xpToAdd >= totalXp) {
				xpToAdd -= totalXp;
				level++;
				totalXp = getTotalXp(level);
			}

			if (level == UtilsPlayerManager.LEVEL_MAX) {
				xpToAdd = 0;
			}
			setLevel(null, target, level);
			setXp(null, target, xpToAdd);
		}

		if (sender != null) {
			if (isAlterable) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.LEVEL_XP_RECEIVED,
						target.getName(), xpToAdd);
				UtilsAPI.sendSystemMessage(MessageLevel.INFO, sender, UtilsPlayerManager.LEVEL_AND_XP_UPDATED,
						target.getName(), level, xpToAdd);
			} else if (xpToAdd <= 0) {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.NUMBER_MUST_BE_STRICTLY_POSITIVE);
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.LEVEL_MAX_ALREADY_REACH);
			}
		}
	}

	/**
	 * Retire le nombre de niveaux spécifiés au joueur ciblé.
	 * 
	 * @param sender  L'émetteur de la requête, peut être null
	 * @param target  La joueur ciblé, non null
	 * @param nbLevel Le nombre de niveaux à retirer
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void removeLevel(CommandSender sender, OfflinePlayer target, int nbLevel) throws SQLException {
		int level = getLevel(target);
		int newLevel = level;
		boolean isPositiveLevel = nbLevel > 0 && level > UtilsPlayerManager.LEVEL_MIN;

		if (isPositiveLevel) {
			if (newLevel - nbLevel < UtilsPlayerManager.LEVEL_MIN) {
				nbLevel = level - UtilsPlayerManager.LEVEL_MIN;
			}
			newLevel -= nbLevel;
			setLevel(null, target, newLevel);
		}

		if (sender != null) {
			if (isPositiveLevel) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.LEVEL_LOST,
						target.getName(), nbLevel);
				UtilsAPI.sendSystemMessage(MessageLevel.INFO, sender, UtilsPlayerManager.LEVEL_UPDATED,
						target.getName(), newLevel);
			} else if (level == UtilsPlayerManager.LEVEL_MIN) {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.LEVEL_ALREADY_MIN);
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.NUMBER_MUST_BE_STRICTLY_POSITIVE);
			}
		}
	}

	/**
	 * Retire la quantité d'xp spécifiée au joueur ciblé.
	 * 
	 * @param sender  L'émetteur de la requête, peut être null
	 * @param target  La joueur ciblé, non null
	 * @param xpToRmv La quantité d'xp à retirer
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void removeXp(CommandSender sender, OfflinePlayer target, int xpToRmv) throws SQLException {
		int level = getLevel(target);
		int currentXp = getXp(target);
		boolean isAlterable = xpToRmv > 0 && (level != UtilsPlayerManager.LEVEL_MIN || currentXp > 0);

		if (isAlterable) {
			while (level != UtilsPlayerManager.LEVEL_MIN && currentXp < xpToRmv) {
				xpToRmv -= currentXp + 1;
				level--;
				currentXp = getTotalXp(level) - 1;
			}

			if (currentXp - xpToRmv < 0) {
				currentXp = 0;
			} else {
				currentXp -= xpToRmv;
			}
			setLevel(null, target, level);
			setXp(null, target, currentXp);
		}

		if (sender != null) {
			if (isAlterable) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.LEVEL_XP_LOST,
						target.getName(), xpToRmv);
				UtilsAPI.sendSystemMessage(MessageLevel.INFO, sender, UtilsPlayerManager.LEVEL_AND_XP_UPDATED,
						target.getName(), level, currentXp);
			} else if (xpToRmv <= 0) {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.NUMBER_MUST_BE_STRICTLY_POSITIVE);
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.LEVEL_ALREADY_MIN_WITHOUT_XP);
			}
		}
	}

	/**
	 * Réinitialise l'xp et le niveau du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void resetXpAndLevel(CommandSender sender, OfflinePlayer target) throws SQLException {
		int level = getLevel(target);
		int xp = getXp(target);
		boolean isMinLevelWithoutXp = level == UtilsPlayerManager.LEVEL_MIN && xp == 0;

		if (!isMinLevelWithoutXp) {
			if (level > UtilsPlayerManager.LEVEL_MIN) {
				SQLLevel.setLevel(target, UtilsPlayerManager.LEVEL_MIN);
			}

			if (xp > 0) {
				SQLLevel.setXp(target, xp);
			}

			if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setLevel(UtilsPlayerManager.LEVEL_MIN);
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setXp(xp);
			}
		}

		if (sender != null) {
			if (!isMinLevelWithoutXp) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.LEVEL_RESET_XP_AND_LEVEL,
						target.getName());
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsPlayerManager.LEVEL_ALREADY_MIN_WITHOUT_XP);
			}
		}
	}

	/**
	 * Envoie un message à l'émetteur de la requête comportant le niveau du joueur
	 * ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void sendInfo(CommandSender sender, OfflinePlayer target) throws SQLException {
		int level = getLevel(target);
		int xp = getXp(target);
		int totalXp = getTotalXp(level);
		int perc = xp == 0 ? 0 : Math.round(xp * 100 / (float) totalXp);

		if (sender.getName().equals(target.getName())) {
			UtilsAPI.sendSystemMessage(MessageLevel.NORMAL, sender, UtilsPlayerManager.LEVEL_INFO_HIMSELF, level, xp,
					totalXp, perc);
		} else {
			UtilsAPI.sendSystemMessage(MessageLevel.NORMAL, sender, UtilsPlayerManager.LEVEL_INFO, target.getName(),
					level, xp, totalXp, perc);
		}
	}
}
