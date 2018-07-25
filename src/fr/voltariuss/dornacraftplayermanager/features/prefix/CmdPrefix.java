package fr.voltariuss.dornacraftplayermanager.features.prefix;

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

public class CmdPrefix extends CustomCommand implements CommandExecutor {

	private final SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	
	//Arguments
	public static final String ARG_SET = "set";

	public CmdPrefix(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
		this.getSubCommands().add(new SubCommand(ARG_SET, "Modifie le préfixe d'un joueur.", "/prefix set <joueur>", 1));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		super.setSender(sender);
		PrefixManager prefixManager = new PrefixManager(sender);
		
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
							if(args[0].equalsIgnoreCase("set")) {
								if(sender.hasPermission(this.getSubCommand(ARG_SET).getPermission()) || player.getName().equals(sender.getName())) {
									prefixManager.openSetPrefixInventory(player);
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
