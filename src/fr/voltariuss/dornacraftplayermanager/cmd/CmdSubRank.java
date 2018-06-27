package fr.voltariuss.dornacraftplayermanager.cmd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.utils.CommandsUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.SubRank;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;
import fr.voltariuss.dornacraftplayermanager.sql.SQLSubRank;

public class CmdSubRank implements CommandExecutor {
	
	//Instances
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
	private SQLAccount sqlAccount = main.getSQLAccount();
	private SQLSubRank sqlSubRank = main.getSQLSubRank(); 
	private CommandsUtils cmdUtils;
	
	//Messages d'aide sur les commandes
	private final String cmdAdd = "§ePour définir le sous-rang d'un joueur:\n §6/subrank add §b<joueur> <grade>";
	private final String cmdRemove = "§ePour retirer un sous-rang à un joueur:\n §6/subrank remove §b<joueur> <sous-rang>";
	private final String cmdRemoveAll = "§ePour retirer tous les sous-rangs d'un joueur:\n §6/subrank removeall §b<joueur>";
	private final String cmdListSubRank = "§ePour voir la liste des sous-rangs d'un joueur:\n §6/subrank list §b<joueur>";
	
	//Permissions
	private final String permGlobal = "dornacraft.subrrank";
	private final String permAdd = "dornacraft.subrrank.add";
	private final String permRemove = "dornacraft.subrrank.remove";
	private final String permRemoveAll = "dornacraft.subrrank.removeall";
	private final String permListSubRank = "dornacraft.subrrank.list";
	
	//Messages d'erreur
	private final String unknowSubRank = Utils.getErrorPrefix() + "Le sous-rang spécifié est incorrect.";
	private final String hasSubRank = Utils.getErrorPrefix() + "Ce joueur possède déjà le sous-rang spécifié.";
	private final String dontHasSpecificSubRank = Utils.getErrorPrefix() + "Ce joueur ne possède pas le sous-rang spécifié.";
	private final String dontHasSubRank = Utils.getErrorPrefix() + "Ce joueur ne possède pas de sous-rang.";
	
	//Tableaux
	private final String[] cmdList = {cmdAdd,cmdRemove,cmdRemoveAll,cmdListSubRank};
	private final String[] subCmdList = {"add","remove","removeall","list"};
	private final String[] permList = {permAdd,permRemove,permRemoveAll,permListSubRank};

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		cmdUtils = new CommandsUtils(sender, cmdLabel, cmdList);
		
		try {
			if(cmdLabel.equalsIgnoreCase("subrank")) {
				if(cmdUtils.hasPermission(permGlobal)) {
					if(args.length == 0) {
						String pluginName = main.getPluginName();
						String authorName = "Voltariuss";
						String version = "1.0";
						String description = "Permet la gestion des sous-rangs des joueurs.";
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
						for(int i = 0; i < subCmdList.length - 2; i++) {
							if(args[0].equalsIgnoreCase(subCmdList[i])) {
								if(cmdUtils.hasPermission(permList[i])) {
									sender.sendMessage(cmdList[i]);
								}
								return true;
							}
						}
						
						if(args[0].equalsIgnoreCase("removeall")) {
							if(cmdUtils.hasPermission(permRemoveAll)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									sqlSubRank.removeAllSubRanks(target);
									sender.sendMessage("§aTous les sous-rangs ont été retirés au joueur §b" + target.getName() + "§a.");
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}
						} else if(args[0].equalsIgnoreCase("list")) {
							if(cmdUtils.hasPermission(permListSubRank)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
									
								if(target != null) {
									ArrayList<SubRank> subRankList = new ArrayList<>();
									subRankList = sqlSubRank.getSubRanks(target);
									
									if(!subRankList.isEmpty()) {
										sender.sendMessage("§6Liste des sous-rangs du joueur §b" + target.getName() + " §6:");
										Iterator<SubRank> iterator = subRankList.iterator();
										
										while(iterator.hasNext()) {
											SubRank sr = iterator.next();
											sender.sendMessage("§6- " + sr.getSubRankColor() + sr.getName());
										}
									} else {
										sender.sendMessage(dontHasSubRank);
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
									SubRank subRank = SubRank.fromString(args[2]);
									
									if(subRank != null) {
										if(sqlSubRank.hasSubRank(target, subRank)) {
											sender.sendMessage(hasSubRank);
										} else {
											sqlSubRank.addSubRank(target, subRank);
											sender.sendMessage("§aLe sous-rang §6" + subRank.getName() + " §aa bien été attribué au joueur §b" + target.getName() + "§a.");
										}		
									} else {
										sender.sendMessage(unknowSubRank);
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
									SubRank subRank = SubRank.fromString(args[2]);
									
									if(subRank != null) {
										if(!sqlSubRank.hasSubRank(target, subRank)) {
											sender.sendMessage(dontHasSpecificSubRank);
										} else {
											sqlSubRank.removeSubRank(target, subRank);
											sender.sendMessage("§aLe sous-rang §6" + subRank.getName() + " §aa bien été retiré au joueur §b" + target.getName() + "§a.");
										}		
									} else {
										sender.sendMessage(unknowSubRank);
									}
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}
						} else if(args[0].equalsIgnoreCase("removeall")) {
							if(cmdUtils.hasPermission(permRemoveAll)) {
								cmdUtils.sendTooManyArguments(cmdRemoveAll);
							}
						} else if(args[0].equalsIgnoreCase("list")) {
							if(cmdUtils.hasPermission(permListSubRank)) {
								cmdUtils.sendTooManyArguments(cmdListSubRank);														
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
