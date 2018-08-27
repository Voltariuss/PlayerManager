package fr.voltariuss.dornacraft.playermanager.features.level;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.utils.Utils;

public final class LevelManager {
	
	public static final int MIN_LEVEL = 1;
	public static final int MAX_LEVEL = 80;
	
	public static final String INVALIDE_NUMBER_POSITIVE = "Le nombre saisie doit �tre positif.";
	public static final String MUST_BE_IN_INTERVAL = "Le nombre sp�cifi� doit �tre compris entre " + MIN_LEVEL + " et " + MAX_LEVEL + ".";
		
	/**
	 * R�cup�re le niveau du joueur dans la m�moire centrale si il est connect�,
	 * dans la base de donn�es sinon.
	 * 
	 * @param target Le joueur cibl�, non null
	 * @return Le niveau du joueur cibl�.
	 * @throws SQLException
	 */
	public static int getLevel(OfflinePlayer target) throws SQLException {
		int level = MIN_LEVEL;
		
		if(target.isOnline()) {
			level = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getLevel();
		} else {
			level = SQLLevel.getLevel(target);
		}
		return level;
	}
	
	/**
	 * D�finit le niveau du joueur.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @param level Le nouveau niveau du joueur
	 * @throws SQLException
	 */
	public static void setLevel(CommandSender sender, OfflinePlayer target, int level) throws SQLException {
		boolean isInInterval = level >= MIN_LEVEL && level <= MAX_LEVEL;
		
		if(isInInterval) {
			SQLLevel.setLevel(target, level);
			//Actualise le niveau du joueur dans la m�moire centrale si il est connect�
			if(target.isOnline()) {
				PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setLevel(level);
			}
		}
		
		if(sender != null) {
			if(isInInterval) {
				sendNewCurrentPlayerLevelMessage(sender, target);
			} else {
				Utils.sendErrorMessage(sender, MUST_BE_IN_INTERVAL);
			}
		}
	}

	/**
	 * Ajoute le nombre de niveaux sp�cifi�s au joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @param nbLevel Le nombre de niveaux � ajouter
	 * @throws SQLException
	 */
	public static void addLevel(CommandSender sender, OfflinePlayer target, int nbLevel) throws SQLException {
		int playerLevel = getLevel(target);
		int newLevel = playerLevel;
		boolean isPositiveLevel = nbLevel > 0;
		
		if(isPositiveLevel) {
			if(newLevel + nbLevel > MAX_LEVEL) {
				nbLevel = MAX_LEVEL - playerLevel;
			}
			newLevel += nbLevel;
			setLevel(null, target, newLevel);
		}
		
		if(sender != null) {
			if(isPositiveLevel) {
				sendPlayerReceiveLevelMessage(sender, target, nbLevel);
			} else {
				Utils.sendErrorMessage(sender, INVALIDE_NUMBER_POSITIVE);
			}
		}
	}
	
	/**
	 * Retire le nombre de niveaux sp�cifi�s au joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target La joueur cibl�, non null
	 * @param nbLevel Le nombre de niveaux � retirer
	 * @throws SQLException
	 */
	public static void removeLevel(CommandSender sender, OfflinePlayer target, int nbLevel) throws SQLException {
		int playerLevel = getLevel(target);
		int newLevel = playerLevel;
		boolean isPositiveLevel = nbLevel > 0;
		
		if(isPositiveLevel) {
			if(newLevel - nbLevel < MIN_LEVEL) {
				nbLevel = playerLevel - MIN_LEVEL;
			}
			newLevel -= nbLevel;
			setLevel(null, target, newLevel);
		}
		
		if(sender != null) {
			if(isPositiveLevel) {
				sendPlayerLoseLevelMessage(sender, target, nbLevel);
			} else {
				Utils.sendErrorMessage(sender, INVALIDE_NUMBER_POSITIVE);
			}
		}
	}
	
	/**
	 * R�initialise le niveau du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void resetLevel(CommandSender sender, OfflinePlayer target) throws SQLException {
		setLevel(null, target, 1);
		sendResetPlayerLevelMessage(sender, target);
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te comportant le niveau du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void sendInfoLevel(CommandSender sender, OfflinePlayer target) throws SQLException {
		int level = getLevel(target);
		sender.sendMessage("�6Niveau du joueur �b" + target.getName() + " �6: �e" + Integer.toString(level));
	}
	
	/**
	 * Envoie un message � l'�metteur de le requ�te annon�ant le nouveau niveau du joueur cibl�.
	 * 
	 * @param sender L'�metteur de le requ�te, non null
	 * @param target Le joueur cibl�, non null
	 * @param newLevel Le nouveau niveau du joueur cibl�
	 * @throws SQLException 
	 */
	private static void sendNewCurrentPlayerLevelMessage(CommandSender sender, OfflinePlayer target) throws SQLException {
		sender.sendMessage("�aLe joueur �b" + target.getName() + " �aest d�sormais niveau �6" + Integer.toString(getLevel(target)) + "�a.");
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te sp�cifiant le nombre de niveaux re�us par le joueur cibl�.
	 * 
	 * @param sender L'�metteur de la reuq�te, non null
	 * @param target Le joueur cibl�, non null
	 * @param nbLevel Le nombre de niveaux re�us par le joueur cibl�
	 * @param level Le nouveau niveau du joueur cibl�
	 * @throws SQLException 
	 */
	private static void sendPlayerReceiveLevelMessage(CommandSender sender, OfflinePlayer target, int nbLevel) throws SQLException {
		sender.sendMessage("�aLe joueur �b" + target.getName() + " �aa re�u �e" + Integer.toString(nbLevel) + " niveaux�a.");
		sendNewCurrentPlayerLevelMessage(sender, target);
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te sp�cifiant le nombre de niveaux perdus par le joueur cibl�.
	 * 
	 * @param sender L'�metteur de le requ�te, non null
	 * @param target Le joueur cibl�, non null
	 * @param nbLevel Le nombre de niveaux perdus par le joueur cibl�
	 * @param newLevel Le nouveau niveau du joueur cibl�
	 * @throws SQLException 
	 */
	private static void sendPlayerLoseLevelMessage(CommandSender sender, OfflinePlayer target, int nbLevel) throws SQLException {
		sender.sendMessage("�aLe joueur �b" + target.getName() + " �aa perdu �e" + Integer.toString(nbLevel) + " niveaux�a.");
		sendNewCurrentPlayerLevelMessage(sender, target);
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te annon�ant le succ�s de la r�initialisation du niveau du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param target Le joueur cibl�, non null
	 * @throws SQLException 
	 */
	private static void sendResetPlayerLevelMessage(CommandSender sender, OfflinePlayer target) throws SQLException {
		sender.sendMessage("�aLe niveau du joueur �b" + target.getName() + " �aa bien �t� r�initilis�.");
		sendNewCurrentPlayerLevelMessage(sender, target);
	}
}
