package fr.voltariuss.dornacraftplayermanager.features.level;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.SQLAccount;

public class CmdLevel extends CustomCommand implements CommandExecutor {

	private final SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	
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
		this.getSubCommands().add(new SubCommand(ARG_RESET, "Réinitilise le niveau d'un joueur.", "/level reset <joueur>", 4));
		this.getSubCommands().add(new SubCommand(ARG_INFO, "Affiche le niveau d'un joueur.", "/level info <joueur>", 5));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		super.setSender(sender);
		LevelManager levelManager = new LevelManager(sender);
		
		if(sender.hasPermission(this.getPrimaryPermission())) {
			try {
				if(args.length == 0) {
					this.sendDescriptionCommandMessage();
				} else if(args.length == 1) {
					for(int i = 0; i < this.getSubCommands().size(); i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands().get(i).getArg())) {
							if(sender.hasPermission(this.getSubCommands().get(i).getPermission())) {
								sender.sendMessage(this.getSubCommands().get(i).getHelpMessage());
							} else {
								this.sendLakePermissionMessage();
							}
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendHelpMessage();
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
						UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
						OfflinePlayer player = uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
						
						if(player != null) {
							if(args[0].equalsIgnoreCase("reset")) {
								if(sender.hasPermission(this.getSubCommand(ARG_RESET).getPermission())) {
									levelManager.resetLevel(player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase("info")) {
								if(sender.hasPermission(this.getSubCommand(ARG_INFO).getPermission())) {
									levelManager.sendInfoLevel(player);
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
						UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
						OfflinePlayer player = uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
						
						if(args[0].equalsIgnoreCase("add")) {
							if(sender.hasPermission(this.getSubCommand(ARG_ADD).getPermission())) {
								levelManager.addLevel(player, Integer.parseInt(args[2]));
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(sender.hasPermission(this.getSubCommand(ARG_REMOVE).getPermission())) {
								levelManager.removeLevel(player, Integer.parseInt(args[2]));
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("set")) {
							if(sender.hasPermission(this.getSubCommand(ARG_SET).getPermission())) {
								levelManager.setLevel(player, Integer.parseInt(args[2]));
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
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(Utils.getExceptionMessage());
			}	
		} else {
			this.sendLakePermissionMessage();
		}
		return true;
	}
}
