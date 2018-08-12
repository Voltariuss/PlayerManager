package fr.voltariuss.dornacraftplayermanager.features.permission;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftplayermanager.AccountManager;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;

public class CmdPermission extends CustomCommand implements CommandExecutor {
		
	//Arguments
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_REMOVEALL = "removeall";
	public static final String ARG_LIST = "list";

	public CmdPermission(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
		this.getSubCommands().add(new SubCommand(ARG_ADD, "Ajoute une permission à un joueur.", "/perm add <joueur> <permission>", 1));
		this.getSubCommands().add(new SubCommand(ARG_REMOVE, "Retire une permission à un joueur.", "/perm remove <joueur> <permission>", 2));
		this.getSubCommands().add(new SubCommand(ARG_REMOVEALL, "Retire toutes les permissions d'un joueur.", "/perm removeall <joueur>", 3));
		this.getSubCommands().add(new SubCommand(ARG_LIST, "Affiche la liste des permissions d'un joueur.", "/perm list <joueur>", 4));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		super.setSender(sender);
		
		if(sender.hasPermission(this.getPrimaryPermission())) {
			try {
				if(args.length == 0) {
					this.sendHelpCommandMessage();
				} else if(args.length == 1) {
					for(int i = 0; i < this.getSubCommands().size(); i++) {
						SubCommand subCommand = this.getSubCommands().get(i);
						
						if(args[0].equalsIgnoreCase(subCommand.getArg())) {
							if(sender.hasPermission(subCommand.getPermission())) {
								sender.sendMessage(subCommand.getHelpMessage());
							} else {
								this.sendLakePermissionMessage();
							}
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendHelpCommandMessage();
					} else {
						this.sendWrongCommandMessage();
					}
				} else if(args.length == 2) {
					if(args[0].equalsIgnoreCase("help")) {
						this.sendTooManyArgumentsMessage(args[0]);
					} else {
						OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
						
						if(player != null) {
							if(args[0].equalsIgnoreCase("add")) {
								if(sender.hasPermission(this.getSubCommand(ARG_ADD).getPermission())) {
									this.sendNotEnoughArgumentsMessage(args[0]);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase("remove")) {
								if(sender.hasPermission(this.getSubCommand(ARG_REMOVE).getPermission())) {
									this.sendNotEnoughArgumentsMessage(args[0]);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase("removeall")) {
								if(sender.hasPermission(this.getSubCommand(ARG_REMOVEALL).getPermission())) {
									PermissionManager.removeAllPermissions(sender, player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase("list")) {
								if(sender.hasPermission(this.getSubCommand(ARG_LIST).getPermission())) {
									PermissionManager.sendListPermissions(sender, player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase("help")) {
								this.sendTooManyArgumentsMessage(args[0]);
							} else {
								this.sendWrongCommandMessage();
							}
						} else {
							this.sendUnknowPlayerMessage();
						}
					}
				} else if(args.length == 3) {
					if(args[0].equalsIgnoreCase("help")) {
						this.sendTooManyArgumentsMessage(args[0]);
					} else if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
						OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
						
						if(player != null) {
							if(args[0].equalsIgnoreCase("add")) {
								if(sender.hasPermission(this.getSubCommand(ARG_ADD).getPermission())) {
									PermissionManager.addPermission(sender, player, args[2]);
								} else {
									this.sendLakePermissionMessage();
								}
							} else {
								if(sender.hasPermission(this.getSubCommand(ARG_REMOVE).getPermission())) {
									PermissionManager.removePermission(sender, player, args[2]);
								} else {
									this.sendLakePermissionMessage();
								}
							}
						} else {
							this.sendUnknowPlayerMessage();
						}
					} else {
						for(int i = 2; i < this.getSubCommands().size(); i++) {
							if(args[0].equalsIgnoreCase(this.getSubCommands().get(i).getArg())) {
								this.sendTooManyArgumentsMessage(args[0]);
								return true;
							}
						}
						this.sendWrongCommandMessage();
					}
				} else {
					for(int i = 0; i < this.getSubCommands().size(); i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands().get(i).getArg())) {
							this.sendTooManyArgumentsMessage(args[0]);
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendTooManyArgumentsMessage(args[0]);
					} else {
						this.sendWrongCommandMessage();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				this.sendExceptionMessage();
			}	
		} else {
			this.sendLakePermissionMessage();
		}
		return true;
	}
}
