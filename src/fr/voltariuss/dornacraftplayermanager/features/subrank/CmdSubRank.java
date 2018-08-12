package fr.voltariuss.dornacraftplayermanager.features.subrank;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftapi.utils.ErrorMessage;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.AccountManager;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;

public class CmdSubRank extends CustomCommand implements CommandExecutor {
		
	//Arguments
	public static final String ARG_SET = "set";
	public static final String ARG_REMOVEALL = "removeall";
	public static final String ARG_LIST = "list";
	
	public CmdSubRank(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
		this.getSubCommands().add(new SubCommand(ARG_SET, "D�fini les sous-rangs d'un joueur.", "/subrank set <joueur>", 1));
		this.getSubCommands().add(new SubCommand(ARG_REMOVEALL, "Retire tous les sous-rangs d'un joueur.", "/subrank removeall <joueur>", 2));
		this.getSubCommands().add(new SubCommand(ARG_LIST, "Affiche la liste des sous-rangs d'un joueur.", "/subrank list <joueur>", 3));
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
							if(args[0].equalsIgnoreCase("set")) {
								if(sender.hasPermission(this.getSubCommand(ARG_SET).getPermission())) {
									if(sender instanceof Player) {
										SubRankManager.openSetSubRankInventory((Player) sender, player);
									} else {
										Utils.sendErrorMessage(sender, ErrorMessage.MUST_BE_A_PLAYER);
									}
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase("removeall")) {
								if(sender.hasPermission(this.getSubCommand(ARG_REMOVEALL).getPermission())) {
									SubRankManager.removeAllSubRank(sender, player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase("list")) {
								if(sender.hasPermission(this.getSubCommand(ARG_LIST).getPermission())) {
									SubRankManager.sendListSubRankMessage(sender, player);
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
				this.sendExceptionMessage();
			}	
		} else {
			this.sendLakePermissionMessage();
		}
		return true;
	}
}