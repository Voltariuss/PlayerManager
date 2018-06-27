package fr.voltariuss.dornacraftplayermanager;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.utils.CommandsUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.playercache.PlayerCache;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;

public class CmdPrefix implements CommandExecutor {
	
	//Instances
		private final DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
		private final SQLAccount sqlAccount = main.getSQLAccount();
		private CommandsUtils cmdUtils;
		
		//Messages d'aide sur les commandes
		private final String cmdSet = "§ePour modifier le préfixe d'un joueur :\n §6/prefix set §b<joueur> <prefixe>";
		private final String cmdRemove = "§ePour retirer le préfixe d'un joueur :\n §6/prefix remove §b<joueur>";
		
		//Permissions
		private final String permGlobale = "dornacraft.prefix";
		private final String permSet = "dornacraft.prefix.set";
		private final String permRemove = "dornacraft.prefix.remove";
		
		//Tableaux
		private final String[] cmdList = {cmdSet,cmdRemove};
		private final String[] subCmdList = {"set","remove"};
		private final String[] permList = {permSet,permRemove};

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		cmdUtils = new CommandsUtils(sender, cmdLabel, cmdList);
		
		try {
			if(cmdLabel.equalsIgnoreCase("prefix")) {
				if(cmdUtils.hasPermission(permGlobale)) {
					if(args.length == 0) {
						String pluginName = main.getPluginName();
						String authorName = "Voltariuss";
						String version = "1.0";
						String description = "Permet la gestion du préfixe des joueurs.";
						cmdUtils.sendDescriptionCommand(pluginName, authorName, version, description);
					} else if(args.length == 1) {
						for(int i = 0; i < subCmdList.length; i++) {
							if(args[0].equalsIgnoreCase(subCmdList[i])) {
								if(cmdUtils.hasPermission(permList[i])) {
									sender.sendMessage(cmdList[i]);
								}
								return true;
							}
						}
						
						if(args[0].equalsIgnoreCase("help")) {
							cmdUtils.sendHelpCommand();
						} else {
							cmdUtils.sendWrongCommand();							
						}
					} else if(args.length == 2) {
						for(int i = 0; i < subCmdList.length - 1; i++) {
							if(args[0].equalsIgnoreCase(subCmdList[i])) {
								if(cmdUtils.hasPermission(permList[i])) {
									sender.sendMessage(cmdList[i]);
								}
								return true;
							}
						}
							
						if(args[0].equalsIgnoreCase("remove")) {
							if(cmdUtils.hasPermission(permRemove)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
									
									if(playerCacheMap.containsKey(uuid)) {
										PlayerCache playerCache = playerCacheMap.get(uuid);
										
										if(playerCache.getPrefixType().equals("Default")) {
											sender.sendMessage(Utils.getErrorPrefix() + "Ce joueur utilise actuellement le préfixe par defaut.");
										} else {
											sqlAccount.setPrefixType(target, "Default");
											sender.sendMessage("§aPréfixe par défaut accordé au joueur");
										}	
									} else {
										throw new Exception();
									}
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}
						} else if(args[0].equalsIgnoreCase("help")) {
							cmdUtils.sendTooManyArguments(cmdUtils.getHelpCommand());
						} else {
							cmdUtils.sendWrongCommand();
						}
					} else if(args.length == 3) {
						if(args[0].equalsIgnoreCase("set")) {
							if(cmdUtils.hasPermission(permSet)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									String prefixType = args[2];
									HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
									
									if(playerCacheMap.containsKey(uuid)) {
										PlayerCache playerCache = playerCacheMap.get(uuid);
										
										if(playerCache.getPrefixType().equalsIgnoreCase(prefixType)) {
											sender.sendMessage(Utils.getErrorPrefix() + "Ce joueur utilise déjà ce préfixe.");
										} else {
											sqlAccount.setPrefixType(target, prefixType);
											sender.sendMessage("Préfixe du joueur modifié.");
										}
									} else {
										throw new Exception();
									}
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}
						} else if(args[0].equalsIgnoreCase("help")) {
							cmdUtils.sendTooManyArguments(cmdUtils.getHelpCommand());
						} else {
							cmdUtils.sendWrongCommand();
						}
					} else {
						for(int i = 0; i < subCmdList.length; i++) {
							if(args[0].equalsIgnoreCase(subCmdList[i])) {
								if(cmdUtils.hasPermission(permList[i])) {
									cmdUtils.sendTooManyArguments(cmdList[i]);
								}
								return true;
							}
						}
						
						if(args[0].equalsIgnoreCase("help")) {
							cmdUtils.sendTooManyArguments(cmdUtils.getHelpCommand());
						} else {
							cmdUtils.sendWrongCommand();
						}
					}
	 			}
			}
		} catch(Exception e) {
			e.printStackTrace();
			cmdUtils.sendExceptionMessage();
		}
		return true;
	}

}
