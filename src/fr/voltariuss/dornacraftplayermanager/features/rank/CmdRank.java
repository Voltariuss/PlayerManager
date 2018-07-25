package fr.voltariuss.dornacraftplayermanager.features.rank;

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
		RankManager rankManager = new RankManager(sender);
		
		if(sender.hasPermission(this.getPrimaryPermission())) {
			try {
				if(args.length == 0) {
					this.sendDescriptionCommandMessage();
				} else if(args.length == 1) {
					for(int i = 0; i < this.getSubCommands().size(); i++) {
						SubCommand subCommand = this.getSubCommands().get(i);
						if(args[0].equalsIgnoreCase(subCommand.getArg())) {
							if(sender.hasPermission(subCommand.getPermission())) {
								this.sendMessage(subCommand.getHelpMessage());
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
						UUID uuid = DornacraftPlayerManager.getInstance().getSQLAccount().getUUIDOfPlayer(args[1]);
						OfflinePlayer player = uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
						
						if(player != null) {
							if(args[0].equalsIgnoreCase(ARG_SET)) {
								if(sender.hasPermission(this.getSubCommand(ARG_SET).getPermission())) {
									rankManager.openSetRankInventory(player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase(ARG_REMOVE)) {
								if(sender.hasPermission(this.getSubCommand(ARG_REMOVE).getPermission())) {
									rankManager.removeRank(player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase(ARG_PROMOTE)) {
								if(sender.hasPermission(this.getSubCommand(ARG_PROMOTE).getPermission())) {
									rankManager.promote(player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase(ARG_DEMOTE)) {
								if(sender.hasPermission(this.getSubCommand(ARG_DEMOTE).getPermission())) {
									rankManager.demote(player);
								} else {
									this.sendLakePermissionMessage();
								}
							} else if(args[0].equalsIgnoreCase(ARG_INFO)) {
								if(sender.hasPermission(this.getSubCommand(ARG_INFO).getPermission())) {
									rankManager.info(player);
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
				sender.sendMessage(Utils.getExceptionMessage());
			}	
		} else {
			this.sendLakePermissionMessage();
		}
		return true;
	}
}
