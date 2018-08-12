package fr.voltariuss.dornacraftplayermanager.features.level;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftplayermanager.AccountManager;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;

public class CmdLevel extends CustomCommand implements CommandExecutor {
	
	//Arguments
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_SET = "set";
	public static final String ARG_RESET = "reset";
	public static final String ARG_INFO = "info";

	public CmdLevel(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
		this.getSubCommands().add(new SubCommand(ARG_ADD, "Ajoute des niveaux à un joueur.", "/level add <joueur> <nombre>", 1));
		this.getSubCommands().add(new SubCommand(ARG_REMOVE, "Retire des niveaux à un joueur.", "/level remove <joueur> <nombre>", 2));
		this.getSubCommands().add(new SubCommand(ARG_SET, "Défini le niveau d'un joueur.", "/level set <joueur> <nombre>", 3));
		this.getSubCommands().add(new SubCommand(ARG_RESET, "Réinitialise le niveau d'un joueur.", "/level reset <joueur>", 4));
		this.getSubCommands().add(new SubCommand(ARG_INFO, "Affiche le niveau d'un joueur.", "/level info <joueur>", 5));
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
					} else if(args[0].equalsIgnoreCase("set")) {
						if(sender.hasPermission(this.getSubCommand(ARG_SET).getPermission())) {
							this.sendNotEnoughArgumentsMessage(args[0]);
						} else {
							this.sendLakePermissionMessage();
						}
					} else if(args[0].equalsIgnoreCase("help")) {
						this.sendTooManyArgumentsMessage(args[0]);
					} else {
						OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
						
						if(player != null) {
							if(args[0].equalsIgnoreCase("reset")) {
								if(sender.hasPermission(this.getSubCommand(ARG_RESET).getPermission())) {
									LevelManager.resetLevel(sender, player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase("info")) {
								if(sender.hasPermission(this.getSubCommand(ARG_INFO).getPermission())) {
									LevelManager.sendInfoLevel(sender, player);
								} else {
									this.sendLakePermissionMessage();
								}
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
					} else {
						OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
						
						if(args[0].equalsIgnoreCase("add")) {
							if(sender.hasPermission(this.getSubCommand(ARG_ADD).getPermission())) {
								LevelManager.addLevel(sender, player, Integer.parseInt(args[2]));
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(sender.hasPermission(this.getSubCommand(ARG_REMOVE).getPermission())) {
								LevelManager.removeLevel(sender, player, Integer.parseInt(args[2]));
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("set")) {
							if(sender.hasPermission(this.getSubCommand(ARG_SET).getPermission())) {
								LevelManager.setLevel(sender, player, Integer.parseInt(args[2]));
							} else {
								this.sendLakePermissionMessage();
							}
						} else {
							for(int i = 3; i < this.getSubCommands().size(); i++) {
								if(args[0].equalsIgnoreCase(this.getSubCommands().get(i).getArg())) {
									this.sendTooManyArgumentsMessage(args[0]);
									return true;
								}
							}
							this.sendWrongCommandMessage();
						}
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
