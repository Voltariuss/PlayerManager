package fr.voltariuss.dornacraftplayermanager.features.rank;

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

public class CmdRank extends CustomCommand implements CommandExecutor {
	
	//Arguments
	public static final String ARG_SET = "set";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_PROMOTE = "promote";
	public static final String ARG_DEMOTE = "demote";
	public static final String ARG_INFO = "info";

	/**
	 * Constructor of the command /rank.
	 * 
	 * @param sender The command sender.
	 * @param cmdLabel The label of the command.
	 * @param plugin The plugin who generate this command.
	 */
	public CmdRank(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
		this.getSubCommands().add(new SubCommand(ARG_SET, "Défini le rang d'un joueur.", "/rank set <joueur>", 1));
		this.getSubCommands().add(new SubCommand(ARG_REMOVE, "Retire le rang d'un joueur.", "/rank remove <joueur>", 2));
		this.getSubCommands().add(new SubCommand(ARG_PROMOTE, "Promouvois un joueur.", "/rank promote <joueur>", 3));
		this.getSubCommands().add(new SubCommand(ARG_DEMOTE, "Rétrograde un joueur.", "/rank demote <joueur>", 4));
		this.getSubCommands().add(new SubCommand(ARG_INFO, "Affiche le rang d'un joueur.", "/rank info <joueur>", 5));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		this.setSender(sender);
		
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
							if(args[0].equalsIgnoreCase(ARG_SET)) {
								if(sender.hasPermission(this.getSubCommand(ARG_SET).getPermission())) {
									if(sender instanceof Player) {
										RankManager.openSetRankInventory((Player) sender, player);
									} else {
										Utils.sendErrorMessage(sender, ErrorMessage.MUST_BE_A_PLAYER);
									}
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase(ARG_REMOVE)) {
								if(sender.hasPermission(this.getSubCommand(ARG_REMOVE).getPermission())) {
									RankManager.removeRank(sender, player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase(ARG_PROMOTE)) {
								if(sender.hasPermission(this.getSubCommand(ARG_PROMOTE).getPermission())) {
									RankManager.promote(sender, player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase(ARG_DEMOTE)) {
								if(sender.hasPermission(this.getSubCommand(ARG_DEMOTE).getPermission())) {
									RankManager.demote(sender, player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase(ARG_INFO)) {
								if(sender.hasPermission(this.getSubCommand(ARG_INFO).getPermission())) {
									RankManager.sendRankInfoMessage(sender, player.getName(), RankManager.getRank(player));
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
