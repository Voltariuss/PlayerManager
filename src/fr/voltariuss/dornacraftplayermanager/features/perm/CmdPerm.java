package fr.voltariuss.dornacraftplayermanager.features.perm;

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

public class CmdPerm extends CustomCommand implements CommandExecutor {
	
	private final SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	
	//Arguments
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_REMOVEALL = "removeall";
	public static final String ARG_LIST = "list";

	public CmdPerm(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
		this.getSubCommands().add(new SubCommand(ARG_ADD, "Ajoute une permission à un joueur.", "/perm add <joueur> <permission>", 1));
		this.getSubCommands().add(new SubCommand(ARG_REMOVE, "Retire une permission à un joueur.", "/perm remove <joueur> <permission>", 2));
		this.getSubCommands().add(new SubCommand(ARG_REMOVEALL, "Retire toutes les permissions d'un joueur.", "/perm removeall <joueur>", 3));
		this.getSubCommands().add(new SubCommand(ARG_LIST, "Affiche la liste des permissions d'un joueur.", "/perm list <joueur>", 4));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		super.setSender(sender);
		PermManager permManager = new PermManager(sender);
		
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
					if(args[0].equalsIgnoreCase("help")) {
						this.sendTooManyArgumentsMessage(args[0]);
					} else {
						UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
						OfflinePlayer player = uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
						
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
									permManager.removeAllPerm(player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase("list")) {
								if(sender.hasPermission(this.getSubCommand(ARG_LIST).getPermission())) {
									permManager.sendListPerm(player);
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
					} else {
						UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
						OfflinePlayer player = uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
						
						if(args[0].equalsIgnoreCase("add")) {
							if(sender.hasPermission(this.getSubCommand(ARG_ADD).getPermission())) {
								permManager.addPerm(player, args[2]);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(sender.hasPermission(this.getSubCommand(ARG_REMOVE).getPermission())) {
								permManager.removePerm(player, args[2]);
							} else {
								this.sendLakePermissionMessage();
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
