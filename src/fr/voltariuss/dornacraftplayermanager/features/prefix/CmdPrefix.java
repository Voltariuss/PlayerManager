package fr.voltariuss.dornacraftplayermanager.features.prefix;

import java.sql.SQLException;

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

public class CmdPrefix extends CustomCommand implements CommandExecutor {
	
	//Arguments
	public static final String ARG_SET = "set";

	public CmdPrefix(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
		this.getSubCommands().add(new SubCommand(ARG_SET, "Modifie le préfixe d'un joueur.", "/prefix set <joueur>", 1));
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
								if(sender.hasPermission(this.getSubCommand(ARG_SET).getPermission()) || player.getName().equals(sender.getName())) {
									if(sender instanceof Player) {
										PrefixManager.openSetPrefixInventory((Player) sender, player);										
									} else {
										Utils.sendErrorMessage(sender, ErrorMessage.MUST_BE_A_PLAYER);
									}
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
