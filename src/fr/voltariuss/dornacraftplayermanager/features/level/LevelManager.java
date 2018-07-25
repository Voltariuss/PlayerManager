package fr.voltariuss.dornacraftplayermanager.features.level;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.FeatureManager;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.SQLAccount;

public class LevelManager extends FeatureManager {
	
	private SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	
	//Messages d'erreur
	public static final String PLAYER_LEVEL_ALREADY_MAX = "Le joueur possède déjà le niveau maximum.";
	public static final String PLAYER_LEVEL_ALREADY_MIN = "Le joueur possède déjà le niveau le plus bas.";
	public static final String INVALIDE_NUMBER_POSITIVE = "Le nombre saisie doit être positif.";
	public static final String MUST_BE_IN_INTERVAL = "Le nombre spécifié doit être compris entre 1 et " + DornacraftPlayerManager.getInstance().getMaxLevel() + ".";
	
	//Autres messages
	public static final String CURRENT_PLAYER_LEVEL = "§aLe joueur §b% §aest désormais niveau §6%§a.";
	public static final String PLAYER_RECEIVED_LEVEL = "§aLe joueur §b% §aa reçu §e% niveaux§a.";
	public static final String PLAYER_LOSE_LEVEL = "§aLe joueur §b% §aa perdu §e% niveaux§a.";
	public static final String RESET_PLAYER_LEVEL = "§aLe niveau du joueur §b% §aa bien été réinitilisé.";
	
	public LevelManager(CommandSender sender) {
		super(sender);
	}

	public void addLevel(OfflinePlayer player, int nbLevel) throws Exception {
		int playerLevel = sqlAccount.getLevel(player);
		
		if(playerLevel < 80) {
			if(nbLevel > 0) {
				int newLevel = playerLevel + nbLevel;
				
				if(newLevel > 80 ) {
					nbLevel = 80 - playerLevel;
					newLevel = playerLevel + nbLevel;
				}
				
				sqlAccount.setLevel(player, newLevel);
				this.sendPlayerReceiveLevelMessage(player.getName(), nbLevel, newLevel);
			} else {
				this.sendErrorMessage(INVALIDE_NUMBER_POSITIVE);
			}
		} else {
			this.sendErrorMessage(PLAYER_LEVEL_ALREADY_MAX);
		}
	}
	
	public void removeLevel(OfflinePlayer player, int nbLevel) throws Exception {
		int playerLevel = sqlAccount.getLevel(player);
		
		if(playerLevel > 1) {
			if(nbLevel > 0) {
				int newLevel = playerLevel - nbLevel;

				if(newLevel > 80 ) {
					nbLevel = 80 - playerLevel;
					newLevel = playerLevel + nbLevel;
				}
				
				sqlAccount.setLevel(player, newLevel);
				this.sendPlayerLoseLevelMessage(player.getName(), nbLevel, newLevel);
			} else {
				this.sendErrorMessage(INVALIDE_NUMBER_POSITIVE);
			}
		} else {
			this.sendErrorMessage(PLAYER_LEVEL_ALREADY_MIN);
		}
	}
	
	public void setLevel(OfflinePlayer player, int newLevel) throws Exception {
		if(newLevel <= 80 && newLevel >= 1) {
			sqlAccount.setLevel(player, newLevel);
			this.sendCurrentPlayerLevelMessage(player.getName(), newLevel);
		} else {
			this.sendErrorMessage(MUST_BE_IN_INTERVAL);
		}
	}
	
	public void resetLevel(OfflinePlayer player) throws Exception {
		sqlAccount.setLevel(player, 1);
		this.sendResetPlayerLevelMessage(player.getName());
	}
	
	public void sendInfoLevel(OfflinePlayer player) throws Exception {
		int level = sqlAccount.getLevel(player);
		this.sendMessage("§6Niveau du joueur §b" + player.getName() + " §6: §e" + level);
	}
	
	public void sendCurrentPlayerLevelMessage(String playerName, int level) {
		this.sendMessage(CURRENT_PLAYER_LEVEL.replaceFirst("%", playerName).replaceFirst("%", Integer.toString(level)));
	}
	
	public void sendPlayerReceiveLevelMessage(String playerName, int nbLevel, int level) {
		this.sendMessage(PLAYER_RECEIVED_LEVEL.replaceFirst("%", playerName).replaceFirst("%", Integer.toString(nbLevel)));
		this.sendCurrentPlayerLevelMessage(playerName, level);
	}
	
	public void sendPlayerLoseLevelMessage(String playerName, int nbLevel, int level) {
		this.sendMessage(PLAYER_LOSE_LEVEL.replaceFirst("%", playerName).replaceFirst("%", Integer.toString(nbLevel)));
		this.sendCurrentPlayerLevelMessage(playerName, level);
	}
	
	public void sendResetPlayerLevelMessage(String playerName) {
		this.sendMessage(RESET_PLAYER_LEVEL.replaceFirst("%", playerName));
		this.sendCurrentPlayerLevelMessage(playerName, 1);
	}
}
