package fr.voltariuss.dornacraftplayermanager.cmd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;
import fr.voltariuss.dornacraftplayermanager.sql.SQLPerm;

public class CmdPerm extends CustomCommand implements CommandExecutor {
	
	//Instances
	private final SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	private final SQLPerm sqlPerm = DornacraftPlayerManager.getInstance().getSqlPerm();
	
	//Messages d'erreur
	public static final String NO_HAS_PERMISSIONS = "§cCe joueur ne possède pas de permissions particulières.";
	public static final String ALREADY_HAS_PERMISSION = "Ce joueur possède déjà cette permission.";
	public static final String NO_HAS_SPECIFIED_PERMISSION = "Ce joueur ne possède pas cette permission.";
	
	//Arguments
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_REMOVEALL = "removeall";
	public static final String ARG_LIST = "list";
	
	//Messages d'aide sur les commandes
	public static final String MSG_ADD = "§ePour ajouter une permission à un joueur :\n §6/perm add §b<joueur> <permission>";
	public static final String MSG_REMOVE = "§ePour retirer une permission à un joueur :\n §6/perm remove §b<joueur> <permission>";
	public static final String MSG_REMOVEALL = "§ePour retirer toutes les permissions d'un joueur :\n §6/perm removeall §b<joueur>";
	public static final String MSG_LIST = "§ePour afficher la liste des permissions d'un joueur :\n §6/perm list §b<joueur>";
	
	//Permissions
	public static final String PERM_GLOBAL = "dornacraft.perm";
	public static final String PERM_ADD = PERM_GLOBAL + "." + ARG_ADD;
	public static final String PERM_REMOVE = PERM_GLOBAL + "." + ARG_REMOVE;
	public static final String PERM_REMOVEALL = PERM_GLOBAL + "." + ARG_REMOVEALL;
	public static final String PERM_LIST = PERM_GLOBAL + "." + ARG_LIST;
	
	//Tableaux
	private final String[] HELP_MESSAGES = {MSG_ADD,MSG_REMOVE,MSG_REMOVEALL,MSG_LIST};
	private final String[] SUB_COMMANDS = {ARG_ADD,ARG_REMOVE,ARG_REMOVEALL,ARG_LIST};
	private final String[] PERMISSIONS = {PERM_ADD,PERM_REMOVE,PERM_REMOVEALL,PERM_LIST};

	public CmdPerm(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		super.setCommandSender(sender);
		
		if(sender.hasPermission(PERM_GLOBAL)) {
			try {
				if(args.length == 0) {
					this.sendDescriptionCommandMessage();
				} else if(args.length == 1) {
					for(int i = 0; i < this.getSubCommands().length; i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands()[i])) {
							if(sender.hasPermission(this.getPermissions()[i])) {
								sender.sendMessage(this.getHelpMessages()[i]);
							} else {
								this.sendLakePermissionMessage();
							}
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendHelpMessage(this.getHelpMessages());
					} else {
						this.sendWrongCommandMessage();
					}
				} else if(args.length == 2) {
					UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					
					if(player != null) {
						if(args[0].equalsIgnoreCase("add")) {
							if(sender.hasPermission(PERM_ADD)) {
								this.sendNotEnoughArgumentsMessage();
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(sender.hasPermission(PERM_REMOVE)) {
								this.sendNotEnoughArgumentsMessage();
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("removeall")) {
							if(sender.hasPermission(PERM_REMOVEALL)) {
								this.removeAllPerm(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("list")) {
							if(sender.hasPermission(PERM_LIST)) {
								this.sendListPerm(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("help")) {
							this.sendTooManyArgumentsMessage();
						} else {
							this.sendWrongCommandMessage();
						}
					} else {
						this.sendUnknowPlayerMessage();
					}
				} else if(args.length == 3) {
					UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					
					if(args[0].equalsIgnoreCase("add")) {
						if(sender.hasPermission(PERM_ADD)) {
							this.addPerm(player, args[2]);
						} else {
							this.sendLakePermissionMessage();
						}
					} else if(args[0].equalsIgnoreCase("remove")) {
						if(sender.hasPermission(PERM_REMOVE)) {
							this.removePerm(player, args[2]);
						} else {
							this.sendLakePermissionMessage();
						}
					} else {
						for(int i = 2; i < this.getSubCommands().length; i++) {
							if(args[0].equalsIgnoreCase(this.getSubCommands()[i])) {
								this.sendTooManyArgumentsMessage();
								return true;
							}
						}
						
						if(args[0].equalsIgnoreCase("help")) {
							this.sendTooManyArgumentsMessage();
						} else {
							this.sendWrongCommandMessage();
						}
					}
				} else {
					for(int i = 0; i < this.getSubCommands().length; i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands()[i])) {
							this.sendTooManyArgumentsMessage();
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendTooManyArgumentsMessage();
					} else {
						this.sendWrongCommandMessage();
					}
				}	
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(Utils.getExceptionMessage());
			}	
		} else {
			this.sendLakePermissionMessage();
		}
		return true;
	}
	
	public void addPerm(OfflinePlayer player, String permission) throws Exception {
		if(!sqlPerm.hasPermission(player, permission)) {
			sqlPerm.addPermission(player, permission);
			this.sendMessage("§aLa permission §6" + permission + " §aa été ajoutée au joueur §b" + player.getName() + "§a.");
		} else {
			this.sendErrorMessage(ALREADY_HAS_PERMISSION);
		}
	}
	
	public void removePerm(OfflinePlayer player, String permission) throws Exception {
		if(sqlPerm.hasPermission(player, permission)) {
			sqlPerm.removePermission(player, permission);
			this.sendMessage("§aLa permission §6" + permission + " §aa été retirée au joueur §b" + player.getName() + "§a.");
		} else {
			this.sendErrorMessage(NO_HAS_SPECIFIED_PERMISSION);
		}
	}
	
	public void removeAllPerm(OfflinePlayer player) throws Exception {
		if(sqlPerm.hasPermission(player)) {
			sqlPerm.removeAllPermissions(player);
			this.sendMessage("§aToutes les permissions spécifiques ont été retirées au joueur §b" + player.getName() + "§a.");
		} else {
			this.sendErrorMessage(NO_HAS_PERMISSIONS);
		}
	}
	
	public void sendListPerm(OfflinePlayer player) throws Exception {
		ArrayList<String> permissions = sqlPerm.getPermissions(player);
		
		if(!permissions.isEmpty()) {
			String listPermissions = "";
			Iterator<String> iterator = permissions.iterator();
			
			while(iterator.hasNext()) {
				String permission = "§f" + iterator.next();
				listPermissions = listPermissions + permission;
				
				if(iterator.hasNext()) {
					listPermissions = listPermissions + "§e, ";
				}
			}
			this.sendMessage("§6Permissions du joueur §b" + player.getName() + " §6: " + listPermissions);
		} else {
			this.sendErrorMessage(NO_HAS_PERMISSIONS);
		}
	}
	
	@Override
	public String[] getHelpMessages() {
		return HELP_MESSAGES;
	}

	@Override
	public String[] getPermissions() {
		return PERMISSIONS;
	}

	@Override
	public String[] getSubCommands() {
		return SUB_COMMANDS;
	}
}
