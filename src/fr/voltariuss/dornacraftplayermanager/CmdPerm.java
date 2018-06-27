package fr.voltariuss.dornacraftplayermanager;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.utils.CommandsUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;
import fr.voltariuss.dornacraftplayermanager.sql.SQLPerm;

public class CmdPerm implements CommandExecutor {

	//Instances
	private final DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
	private final SQLAccount sqlAccount = main.getSQLAccount();
	private final SQLPerm sqlPerm = main.getSqlPerm();
	private CommandsUtils cmdUtils;
	
	//Messages d'aide sur les commandes
	private final String cmdAdd = "§ePour ajouter une permission à un joueur :\n §6/perm add §b<joueur> <permission>";
	private final String cmdRemove = "§ePour retirer une permission à un joueur :\n §6/perm remove §b<joueur> <permission>";
	private final String cmdRemoveAll = "§ePour retirer toutes les permissions d'un joueur :\n §6/perm removeall §b<joueur>";
	private final String cmdListPerm = "§ePour afficher la liste des permissions d'un joueur :\n §6/perm list §b<joueur>";
	
	//Permissions
	private final String permGlobale = "dornacraft.perm";
	private final String permAdd = "dornacraft.perm.add";
	private final String permRemove = "dornacraft.perm.remove";
	private final String permRemoveAll = "dornacraft.perm.removeall";
	private final String permListPerm = "dornacraft.perm.list";
	
	//Tableaux
	private final String[] cmdList = {cmdAdd,cmdRemove,cmdRemoveAll,cmdListPerm};
	private final String[] subCmdList = {"add","remove","removeall","list"};
	private final String[] permList = {permAdd,permRemove,permRemoveAll,permListPerm};

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		cmdUtils = new CommandsUtils(sender, cmdLabel, cmdList);
		
		try {
			if(cmdLabel.equalsIgnoreCase("perm")) {
				if(cmdUtils.hasPermission(permGlobale)) {
					if(args.length == 0) {
						String pluginName = main.getPluginName();
						String authorName = "Voltariuss";
						String version = "1.0";
						String description = "Permet la gestion des permissions des joueurs.";
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
									ArrayList<String> permissions = sqlPerm.getPermissions(target);
									
									if(!permissions.isEmpty()) {
										sqlPerm.removePermissions(target, permissions);
										sender.sendMessage("§aToutes les permissions spécifiques ont été retirées au joueur §b" + target.getName() + "§a.");
									} else {
										sender.sendMessage("§cCe joueur ne possède pas de permissions particulières.");
									}
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}
						} else if(args[0].equalsIgnoreCase("list")) {
							if(cmdUtils.hasPermission(permListPerm)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									ArrayList<String> permissions = new ArrayList<>();	
									permissions = sqlPerm.getPermissions(target);
									
									if(permissions.isEmpty()) {
										sender.sendMessage("§cCe joueur ne possède pas de permissions particulières.");
									} else {
										sender.sendMessage("§6Permissions du joueur §b" + target.getName() + " §6:");
										for(String str : permissions) {
											sender.sendMessage("§6- §7" + str);
										}
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
									String permission = args[2];
									
									if(!sqlPerm.hasPermission(target, permission)) {
										sqlPerm.addPermission(target, permission);									
										sender.sendMessage("§aLa permission §6" + permission + " §aa été ajoutée au joueur §b" + target.getName() + "§a.");
									} else {
										sender.sendMessage(Utils.getErrorPrefix() + "Ce joueur possède déjà cette permission.");
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
									String permission = args[2];
									
									if(sqlPerm.hasPermission(target, permission)) {
										sqlPerm.removePermission(target, permission);
										sender.sendMessage("§aLa permission §6" + permission + " §aa été retirée au joueur §b" + target.getName() + "§a.");	
									} else {
										sender.sendMessage(Utils.getErrorPrefix() + "Ce joueur ne possède pas cette permission.");
									}
								}
							}
						} else if(args[0].equalsIgnoreCase("removeall")) {
							if(cmdUtils.hasPermission(permRemoveAll)) {
								cmdUtils.sendTooManyArguments(cmdRemoveAll);
							}
						} else if(args[0].equalsIgnoreCase("list")) {
							if(cmdUtils.hasPermission(permListPerm)) {
								cmdUtils.sendTooManyArguments(cmdListPerm);							
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
