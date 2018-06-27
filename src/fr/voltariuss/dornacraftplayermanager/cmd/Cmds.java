package fr.voltariuss.dornacraftplayermanager.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;

import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;

public class Cmds implements CommandExecutor {
	
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
	private SQLAccount sqlAccount = main.getSQLAccount();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		try {
			if(cmdLabel.equalsIgnoreCase("rank")) {
				CmdRank cmdRank = new CmdRank(sender, cmdLabel, main);
				
				if(args.length == 0) {
					cmdRank.sendDescriptionCommandMessage();
				} else if(args.length == 1) {
					for(int i = 0; i < cmdRank.getSubCommands().length; i++) {
						if(args[0].equalsIgnoreCase(cmdRank.getSubCommands()[i])) {
							if(sender.hasPermission(cmdRank.getPermissions()[i])) {
								sender.sendMessage(cmdRank.getHelpMessages()[i]);
							} else {
								cmdRank.sendLakePermissionMessage();
							}
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						cmdRank.sendHelpMessage(cmdRank.getHelpMessages());
					} else {
						cmdRank.sendWrongCommandMessage();
					}
				} else if(args.length == 2) {
					UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					
					if(player != null) {
						if(args[0].equalsIgnoreCase("set")) {
							if(sender.hasPermission(CmdRank.PERM_RANK_SET)) {
								Inventory inventory = createInventory();
							} else {
								cmdRank.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(sender.hasPermission(CmdRank.PERM_RANK_REMOVE)) {
								cmdRank.removeRank(player);
							} else {
								cmdRank.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("promote")) {
							if(sender.hasPermission(CmdRank.PERM_RANK_PROMOTE)) {
								cmdRank.promote(player);
							} else {
								cmdRank.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("demote")) {
							if(sender.hasPermission(CmdRank.PERM_RANK_DEMOTE)) {
								cmdRank.demote(player);
							} else {
								cmdRank.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("info")) {
							if(sender.hasPermission(CmdRank.PERM_RANK_INFO)) {
								cmdRank.info(player);
							} else {
								cmdRank.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("help")) {
							cmdRank.sendTooManyArgumentsMessage();
						} else {
							cmdRank.sendWrongCommandMessage();
						}
					} else {
						cmdRank.sendUnknowPlayerMessage();
					}
				} else {
					for(int i = 0; i < cmdRank.getSubCommands().length; i++) {
						if(args[0].equalsIgnoreCase(cmdRank.getSubCommands()[i])) {
							cmdRank.sendTooManyArgumentsMessage();
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						cmdRank.sendTooManyArgumentsMessage();
					} else {
						cmdRank.sendWrongCommandMessage();
					}
				}
			}
			
			if(cmdLabel.equalsIgnoreCase("subrank")) {
				
			}
			
			if(cmdLabel.equalsIgnoreCase("perm")) {
				
			}
			
			if(cmdLabel.equalsIgnoreCase("level")) {
				
			}
			
			if(cmdLabel.equalsIgnoreCase("prefix")) {
				
			}
		} catch(Exception e) {
			e.printStackTrace();
			sender.sendMessage(Utils.getExceptionMessage());
		}
		return true;
	}
}
