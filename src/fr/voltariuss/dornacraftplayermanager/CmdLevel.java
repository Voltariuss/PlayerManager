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

public class CmdLevel implements CommandExecutor {
	
	//Instances
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
	private SQLAccount sqlAccount = main.getSQLAccount();
	private CommandsUtils cmdUtils;
	
	//Messages d'aide sur les commandes
	private final String cmdAdd = "§ePour ajouter des niveaux à un joueur :\n §6/level add §b<joueur> <nombre>";
	private final String cmdRemove = "§ePour retirer des niveaux à un joueur :\n §6/level remove §b<joueur> <nombre>";
	private final String cmdSet = "§ePour définir le niveau d'un joueur :\n §6/level set §b<joueur> <niveau>";
	private final String cmdReset = "§ePour reset le niveau d'un joueur :\n §6/level reset §b<joueur>";
	private final String cmdInfo = "§ePour voir le niveau d'un joueur :\n §6/level info §b<joueur>";
	
	//Message d'erreur
	private final String invalideNumberPositive = Utils.getErrorPrefix() + "Le nombre saisie doit être positif.";
	private final String mustBeInInterval = Utils.getErrorPrefix() + "Le nombre spécifié doit être compris entre 1 et " + main.getMaxLevel() + ".";
	
	//Permissions
	private final String permGlobal = "dornacraft.level";
	private final String permAdd = "dornacraft.level.add";
	private final String permRemove = "dornacraft.level.remove";
	private final String permSet = "dornacraft.level.set";
	private final String permReset = "dornacraft.level.reset";
	private final String permInfo = "dornacraft.level.info";
	
	//Tableaux
	private final String[] cmdList = {cmdAdd,cmdRemove,cmdSet,cmdReset,cmdInfo};
	private final String[] subCmdList = {"add","remove","set","reset", "info"};
	private final String[] permList = {permAdd,permRemove,permSet,permReset,permInfo};

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		cmdUtils = new CommandsUtils(sender, cmdLabel, cmdList);
		
		try {
			if(cmdLabel.equalsIgnoreCase("level")) {
				if(cmdUtils.hasPermission(permGlobal)) {	
					if(args.length == 0) {
						String pluginName = main.getPluginName();
						String authorName = "Voltariuss";
						String version = "1.0";
						String description = "Permet la gestion du niveau des joueurs.";
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
							sender.sendMessage(cmdUtils.getHelpCommand());
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
						
						if(args[0].equalsIgnoreCase("reset")) {
							if(cmdUtils.hasPermission(permReset)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									sqlAccount.setLevel(target, 1);
									sender.sendMessage("§aLe niveau du joueur §b" + target.getName() + " §aa bien été réinitilisé.");
								} else {
									cmdUtils.sendUnknowPlayer();
								}							
							}
						} else if(args[0].equalsIgnoreCase("info")) {
							if(cmdUtils.hasPermission(permInfo)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
									
									if(playerCacheMap.containsKey(uuid)) {
										PlayerCache targetCache = playerCacheMap.get(uuid);
										int level = targetCache.getLevel();
										sender.sendMessage("§6Niveau du joueur §b" + target.getName() + " §6: §6" + level);
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
						if(args[0].equalsIgnoreCase("add")) {
							if(cmdUtils.hasPermission(permAdd)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									int change = Integer.parseInt(args[2]);
									
									if(change > 0) {
										HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
										
										if(playerCacheMap.containsKey(uuid)) {
											PlayerCache targetCache = playerCacheMap.get(uuid);
											int level = targetCache.getLevel();
											int total = level + change;
											int maxLevel = main.getMaxLevel();
											
											if(total > maxLevel) {
												total = maxLevel;
											}
											sqlAccount.setLevel(target, total);
											sender.sendMessage("§aLe joueur §b" + target.getName() + " §a est désormais niveau §6" + total + "§a.");	
										} else {
											throw new Exception();
										}
									} else {
										sender.sendMessage(invalideNumberPositive);
									}
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(cmdUtils.hasPermission(permRemove)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									int change = Integer.parseInt(args[2]);
									
									if(change > 0) {
										HashMap<UUID,PlayerCache> playerCacheMap = main.getPlayerCacheMap();
										
										if(playerCacheMap.containsKey(uuid)) {
											PlayerCache targetCache = playerCacheMap.get(uuid);
											int level = targetCache.getLevel();
											int total = level - change;
											
											if(total < 1) {
												total = 1;
											}
											sqlAccount.setLevel(target, total);
											sender.sendMessage("§aLe joueur §b" + target.getName() + " §a est désormais niveau §6" + total + "§a.");	
										}
									} else {
										sender.sendMessage(invalideNumberPositive);
									}
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}					
						} else if(args[0].equalsIgnoreCase("set")) {
							UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
							OfflinePlayer target = Utils.getOfflinePlayer(uuid);
							
							if(target != null) {
								int level = Integer.parseInt(args[2]);
								
								if(level > 0 && level <= 80) {
									sqlAccount.setLevel(target, level);
									sender.sendMessage("§aLe joueur §b" + target.getName() + " §a est désormais niveau §6" + level + "§a.");
								} else {
									sender.sendMessage(mustBeInInterval);
								}
							} else {
								cmdUtils.sendUnknowPlayer();
							}
						} else if(args[0].equalsIgnoreCase("reset")) {
							if(cmdUtils.hasPermission(permReset)) {
								cmdUtils.sendTooManyArguments(cmdReset);							
							}
						} else if(args[0].equalsIgnoreCase("info")) {
							if(cmdUtils.hasPermission(permInfo)) {
								cmdUtils.sendTooManyArguments(cmdInfo);							
							}
						} else if(args[0].equalsIgnoreCase("help")) {
							cmdUtils.sendTooManyArguments(cmdUtils.getHelpCommand());
						} else {
							cmdUtils.sendWrongCommand();
						}
					} else {
						for(int i = 0; i < subCmdList.length; i++) {
							if(cmdUtils.hasPermission(permList[i])) {
								cmdUtils.sendTooManyArguments(cmdList[i]);
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
