package fr.voltariuss.dornacraftplayermanager.features.level;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.dornacraft.cache.DornacraftCache;
import fr.voltariuss.dornacraftapi.utils.Utils;

public class LevelManager {
	
	public static final int MIN_LEVEL = 1;
	public static final int MAX_LEVEL = 80;
	
	//Messages d'erreur
	public static final String INVALIDE_NUMBER_POSITIVE = "Le nombre saisie doit être positif.";
	public static final String MUST_BE_IN_INTERVAL = "Le nombre spécifié doit être compris entre " + MIN_LEVEL + " et " + MAX_LEVEL + ".";
	
	//Autres messages
	public static final String CURRENT_PLAYER_LEVEL = "§6Niveau du joueur §b% §6: §e%";
	public static final String NEW_CURRENT_PLAYER_LEVEL = "§aLe joueur §b% §aest désormais niveau §6%§a.";
	public static final String PLAYER_RECEIVED_LEVEL = "§aLe joueur §b% §aa reçu §e% niveaux§a.";
	public static final String PLAYER_LOSE_LEVEL = "§aLe joueur §b% §aa perdu §e% niveaux§a.";
	public static final String RESET_PLAYER_LEVEL = "§aLe niveau du joueur §b% §aa bien été réinitilisé.";
	
	/**
	 * Récupère le niveau du joueur dans la mémoire centrale si il est connecté,
	 * dans la base de données sinon.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return Le niveau du joueur ciblé.
	 * @throws SQLException
	 */
	public static int getLevel(OfflinePlayer player) throws SQLException {
		int level = MIN_LEVEL;
		
		if(player.isOnline()) {
			level = DornacraftCache.getPlayerCacheMap().get(player.getUniqueId()).getLevel();
		} else {
			level = SQLLevel.getLevel(player);
		}
		return level;
	}
	
	/**
	 * Définit le niveau du joueur.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param player Le joueur ciblé, non null
	 * @param level Le nouveau niveau du joueur
	 * @throws SQLException
	 */
	public static void setLevel(CommandSender sender, OfflinePlayer player, int level) throws SQLException {
		boolean isInInterval = level >= MIN_LEVEL && level <= MAX_LEVEL;
		
		if(isInInterval) {
			SQLLevel.setLevel(player, level);
			//Actualise le niveau du joueur dans la mémoire centrale si il est connecté
			if(player.isOnline()) {
				DornacraftCache.getPlayerCacheMap().get(player.getUniqueId()).setLevel(level);
			}
		} 
		
		if(sender != null) {
			if(isInInterval) {
				sendNewCurrentPlayerLevelMessage(sender, player.getName(), level);
			} else {
				Utils.sendErrorMessage(sender, MUST_BE_IN_INTERVAL);				
			}
		}
	}

	/**
	 * Ajoute le nombre de niveaux spécifiés au joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param player Le joueur ciblé
	 * @param nbLevel Le nombre de niveaux à ajouter
	 * @throws SQLException
	 */
	public static void addLevel(CommandSender sender, OfflinePlayer player, int nbLevel) throws SQLException {
		int playerLevel = getLevel(player);
		int newLevel = playerLevel;
		boolean isPositiveLevel = nbLevel > 0;
		
		if(isPositiveLevel) {
			if(newLevel + nbLevel > MAX_LEVEL) {
				nbLevel = MAX_LEVEL - playerLevel;
			}
			newLevel += nbLevel;
			setLevel(null, player, newLevel);
		}
		
		if(sender != null) {
			if(isPositiveLevel) {
				sendPlayerReceiveLevelMessage(sender, player.getName(), nbLevel, newLevel);				
			} else {
				Utils.sendErrorMessage(sender, INVALIDE_NUMBER_POSITIVE);
			}
		}
	}
	
	/**
	 * Retire le nombre de niveaux spécifiés au joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param player La joueur ciblé, non null
	 * @param nbLevel Le nombre de niveaux à retirer
	 * @throws SQLException
	 */
	public static void removeLevel(CommandSender sender, OfflinePlayer player, int nbLevel) throws SQLException {
		int playerLevel = getLevel(player);
		int newLevel = playerLevel;
		boolean isPositiveLevel = nbLevel > 0;
		
		if(isPositiveLevel) {
			if(newLevel - nbLevel < MIN_LEVEL) {
				nbLevel = playerLevel - MIN_LEVEL;
			}
			newLevel -= nbLevel;
			setLevel(null, player, newLevel);
		}
		
		if(sender != null) {
			if(isPositiveLevel) {
				sendPlayerLoseLevelMessage(sender, player.getName(), nbLevel, newLevel);
			} else {
				Utils.sendErrorMessage(sender, INVALIDE_NUMBER_POSITIVE);
			}
		}
	}
	
	/**
	 * Réinitialise le niveau du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param player Le joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void resetLevel(CommandSender sender, OfflinePlayer player) throws SQLException {
		setLevel(null, player, 1);
		sendResetPlayerLevelMessage(sender, player.getName());
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête comportant le niveau du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param player Le joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void sendInfoLevel(CommandSender sender, OfflinePlayer player) throws SQLException {
		int level = getLevel(player);
		sender.sendMessage(CURRENT_PLAYER_LEVEL.replaceFirst("%", player.getName()).replaceFirst("%", Integer.toString(level)));
	}
	
	/**
	 * Envoie un message à l'émetteur de le requête annonçant le nouveau niveau du joueur ciblé.
	 * 
	 * @param sender L'émetteur de le requête, non null
	 * @param playerName Le nom du joueur ciblé, non null
	 * @param newLevel Le nouveau niveau du joueur ciblé
	 */
	public static void sendNewCurrentPlayerLevelMessage(CommandSender sender, String playerName, int newLevel) {
		sender.sendMessage(NEW_CURRENT_PLAYER_LEVEL.replaceFirst("%", playerName).replaceFirst("%", Integer.toString(newLevel)));
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête spécifiant le nombre de niveaux reçus par le joueur ciblé.
	 * 
	 * @param sender L'émetteur de la reuqête, non null
	 * @param playerName Le nom du joueur ciblé, non null
	 * @param nbLevel Le nombre de niveaux reçus par le joueur ciblé
	 * @param level Le nouveau niveau du joueur ciblé
	 */
	public static void sendPlayerReceiveLevelMessage(CommandSender sender, String playerName, int nbLevel, int newLevel) {
		sender.sendMessage(PLAYER_RECEIVED_LEVEL.replaceFirst("%", playerName).replaceFirst("%", Integer.toString(nbLevel)));
		sendNewCurrentPlayerLevelMessage(sender, playerName, newLevel);
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête spécifiant le nombre de niveaux perdus par le joueur ciblé.
	 * 
	 * @param sender L'émetteur de le requête, non null
	 * @param playerName Le nom du joueur ciblé, non null
	 * @param nbLevel Le nombre de niveaux perdus par le joueur ciblé
	 * @param newLevel Le nouveau niveau du joueur ciblé
	 */
	public static void sendPlayerLoseLevelMessage(CommandSender sender, String playerName, int nbLevel, int newLevel) {
		sender.sendMessage(PLAYER_LOSE_LEVEL.replaceFirst("%", playerName).replaceFirst("%", Integer.toString(nbLevel)));
		sendNewCurrentPlayerLevelMessage(sender, playerName, newLevel);
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête annonçant le succès de la réinitialisation du niveau du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param playerName Le nom du joueur ciblé, non null
	 */
	public static void sendResetPlayerLevelMessage(CommandSender sender, String playerName) {
		sender.sendMessage(RESET_PLAYER_LEVEL.replaceFirst("%", playerName));
		sendNewCurrentPlayerLevelMessage(sender, playerName, 1);
	}
}
