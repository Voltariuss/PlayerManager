package fr.voltariuss.dornacraft.playermanager.features.level;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.utils.Utils;

public final class LevelManager {

	public static final int MIN_LEVEL = 1;
	public static final int MAX_LEVEL = 80;

	// Error messages
	public static final String INVALIDE_NUMBER_POSITIVE = "Le nombre saisie doit �tre positif.";
	public static final String MUST_BE_IN_INTERVAL = "Le nombre sp�cifi� doit �tre compris entre " + MIN_LEVEL + " et "
			+ MAX_LEVEL + ".";
	public static final String MAX_LEVEL_ALREADY_REACH = "Ce joueur a d�j� atteint le niveau maximum.";
	public static final String MIN_LEVEL_AND_XP_ALREADY_REACH = "Ce joueur est d�j� au niveau le plus bas et ne poss�de pas d'xp.";

	// Messages
	public static final String NEW_CURRENT_PLAYER_LEVEL = "�aLe joueur �b%s �aest d�sormais niveau �6%d�a.";
	public static final String NEW_CURRENT_PLAYER_LEVEL_AND_XP = "�aLe joueur �b%s �aest d�sormais niveau �6%d �aet a �6%d �axp.";
	public static final String NEW_CURRENT_PLAYER_XP = "�aLe joueur �b%s �aa d�sormais �6%d �axp.";
	public static final String AMOUNT_PLAYER_LEVEL_RECEIVED = "�aLe joueur �b%s �aa re�u �e%d niveaux�a.";
	public static final String AMOUNT_PLAYER_LEVEL_LOSE = "�aLe joueur �b%s �aa perdu �e%d niveaux�a.";
	public static final String RESET_PLAYER_LEVEL_SUCCESS = "�aLe niveau du joueur �b%s �aa bien �t� r�initialis�.";

	/**
	 * R�cup�re le niveau du joueur dans la m�moire centrale si il est connect�,
	 * dans la base de donn�es sinon.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return Le niveau du joueur cibl�.
	 * @throws SQLException
	 */
	public static int getLevel(OfflinePlayer target) throws SQLException {
		int level = MIN_LEVEL;

		if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
			level = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getLevel();
		} else {
			level = SQLLevel.getLevel(target);
		}
		return level;
	}

	/**
	 * D�finit le niveau du joueur.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param level
	 *            Le nouveau niveau du joueur
	 * @throws SQLException
	 */
	public static void setLevel(CommandSender sender, OfflinePlayer target, int level) throws SQLException {
		boolean isInInterval = level >= MIN_LEVEL && level <= MAX_LEVEL;

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
				sender.sendMessage(String.format(NEW_CURRENT_PLAYER_LEVEL, target.getName(), level, 0));
			} else {
				Utils.sendErrorMessage(sender, MUST_BE_IN_INTERVAL);
			}
		}
	}

	/**
	 * R�cup�re la quantit� d'xp du joueur dans la m�moire centrale si il est
	 * connect�, dans la base de donn�es sinon.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return La quantit� d'xp du joueur cibl�.
	 * @throws SQLException
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
	 * Calcule la quantit� d'xp totale n�cessaire pour pouvoir passer au niveau
	 * suivant par rapport au niveau sp�cifi�.
	 * 
	 * @param level
	 *            Le niveau correspondant
	 * @return La quantit� d'xp totale n�cessaire pour monter au niveau
	 *         sup�rieur
	 * @throws SQLException
	 */
	public static int getTotalXp(int level) {
		if (level >= MIN_LEVEL && level < MAX_LEVEL) {
			double op1 = Math.pow(1.14 + 3 / (level + 1), level + 1);
			double op2 = Math.pow(level / 65, level + 1);
			double op3 = 300 * Math.pow(level, 1.95) * Math.pow(0.99, level);
			return (int) (op1 + op2 + op3 + 1798);
		} else {
			return 0;
		}
	}

	/**
	 * D�finit la quantit� d'xp du joueur. Si le total d'xp est atteint, le
	 * niveau est augment�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param xp
	 *            La nouvelle quantit� d'xp du joueur
	 * @throws SQLException
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
				sender.sendMessage(String.format(NEW_CURRENT_PLAYER_LEVEL, target.getName(), level));
			} else {
				Utils.sendErrorMessage(sender, MUST_BE_IN_INTERVAL);
			}
		}
	}

	/**
	 * Ajoute le nombre de niveaux sp�cifi�s au joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param nbLevel
	 *            Le nombre de niveaux � ajouter
	 * @throws SQLException
	 */
	public static void addLevel(CommandSender sender, OfflinePlayer target, int nbLevel) throws SQLException {
		int playerLevel = getLevel(target);
		int newLevel = playerLevel;
		boolean isPositiveLevel = nbLevel > 0;

		if (isPositiveLevel) {
			if (newLevel + nbLevel > MAX_LEVEL) {
				nbLevel = MAX_LEVEL - playerLevel;
			}
			newLevel += nbLevel;
			setLevel(null, target, newLevel);
		}

		if (sender != null) {
			if (isPositiveLevel) {
				sender.sendMessage(String.format(AMOUNT_PLAYER_LEVEL_RECEIVED, target.getName(), nbLevel));
				sender.sendMessage(String.format(NEW_CURRENT_PLAYER_LEVEL, target.getName(), newLevel));
			} else {
				Utils.sendErrorMessage(sender, INVALIDE_NUMBER_POSITIVE);
			}
		}
	}

	/**
	 * Ajoute la quantit� d'xp sp�cifi�e au joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param xp
	 *            La quantit� d'xp � ajouter
	 * @throws SQLException
	 */
	public static void addXp(CommandSender sender, OfflinePlayer target, int xpToAdd) throws SQLException {
		int level = getLevel(target);
		boolean isAlterable = xpToAdd > 0 && level != MAX_LEVEL;

		if (isAlterable) {
			xpToAdd += getXp(target);
			int totalXp = getTotalXp(level);

			while (level != MAX_LEVEL && xpToAdd >= totalXp) {
				xpToAdd -= totalXp;
				level++;
				totalXp = getTotalXp(level);
			}

			if (level == MAX_LEVEL) {
				xpToAdd = 0;
			}
			setLevel(null, target, level);
			setXp(null, target, xpToAdd);
		}

		if (sender != null) {
			if (isAlterable) {
				sender.sendMessage(String.format(NEW_CURRENT_PLAYER_LEVEL_AND_XP, target.getName(), level, xpToAdd));
			} else if (xpToAdd <= 0) {
				Utils.sendErrorMessage(sender, INVALIDE_NUMBER_POSITIVE);
			} else {
				Utils.sendErrorMessage(sender, MAX_LEVEL_ALREADY_REACH);
			}
		}
	}

	/**
	 * Retire le nombre de niveaux sp�cifi�s au joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            La joueur cibl�, non null
	 * @param nbLevel
	 *            Le nombre de niveaux � retirer
	 * @throws SQLException
	 */
	public static void removeLevel(CommandSender sender, OfflinePlayer target, int nbLevel) throws SQLException {
		int playerLevel = getLevel(target);
		int newLevel = playerLevel;
		boolean isPositiveLevel = nbLevel > 0;

		if (isPositiveLevel) {
			if (newLevel - nbLevel < MIN_LEVEL) {
				nbLevel = playerLevel - MIN_LEVEL;
			}
			newLevel -= nbLevel;
			setLevel(null, target, newLevel);
		}

		if (sender != null) {
			if (isPositiveLevel) {
				sender.sendMessage(String.format(AMOUNT_PLAYER_LEVEL_LOSE, target.getName(), nbLevel));
				sender.sendMessage(String.format(NEW_CURRENT_PLAYER_LEVEL, target.getName(), newLevel));
			} else {
				Utils.sendErrorMessage(sender, INVALIDE_NUMBER_POSITIVE);
			}
		}
	}

	/**
	 * Retire la quantit� d'xp sp�cifi�e au joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            La joueur cibl�, non null
	 * @param xp
	 *            La quantit� d'xp � retirer
	 * @throws SQLException
	 */
	public static void removeXp(CommandSender sender, OfflinePlayer target, int xpToRmv) throws SQLException {
		int level = getLevel(target);
		int currentXp = getXp(target);
		boolean isAlterable = xpToRmv > 0 && (level != MIN_LEVEL || currentXp > 0);

		if (isAlterable) {
			while (level != MIN_LEVEL && currentXp < xpToRmv) {
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
				sender.sendMessage(String.format(NEW_CURRENT_PLAYER_LEVEL_AND_XP, target.getName(), level, currentXp));
			} else if (xpToRmv <= 0) {
				Utils.sendErrorMessage(sender, INVALIDE_NUMBER_POSITIVE);
			} else {
				Utils.sendErrorMessage(sender, MIN_LEVEL_AND_XP_ALREADY_REACH);
			}
		}
	}

	/**
	 * R�initialise le niveau du joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void resetLevel(CommandSender sender, OfflinePlayer target) throws SQLException {
		setLevel(sender, target, 1);
	}

	/**
	 * Envoie un message � l'�metteur de la requ�te comportant le niveau du
	 * joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, non null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void sendInfo(CommandSender sender, OfflinePlayer target) throws SQLException {
		int level = getLevel(target);
		int xp = getXp(target);
		int totalXp = getTotalXp(level);
		int perc = xp == 0 ? 0 : Math.round(xp * 100 / (float) totalXp);

		if (sender.getName().equals(target.getName())) {
			sender.sendMessage(String.format("�6Votre niveau �6: �e%d\n�6Quantit� d'xp : �e%d�7/�e%d �8(�7%d%%�8)", level,
					xp, totalXp, perc));
		} else {
			sender.sendMessage(String.format("�6Niveau du joueur �b%s �6: �e%d\n�6Quantit� d'xp : �e%d�7/�e%d �8(�7%d%�8)",
					target.getName(), level, xp, totalXp, perc));
		}
	}
}
