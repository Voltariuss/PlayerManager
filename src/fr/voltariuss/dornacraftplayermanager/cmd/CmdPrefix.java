package fr.voltariuss.dornacraftplayermanager.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.Prefix;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;

public class CmdPrefix extends CustomCommand implements CommandExecutor {

	//Instances
	private final SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	
	//Messages d'erreur
	
	//Arguments
	public static final String ARG_SET = "set";
	public static final String ARG_REMOVE = "remove";
	
	//Messages d'aide sur les commandes
	public static final String MSG_SET = "§ePour modifier le préfixe d'un joueur :\n §6/prefix set §b<joueur>";
	public static final String MSG_REMOVE = "§ePour retirer le préfixe d'un joueur :\n §6/prefix remove §b<joueur>";
	
	//Permissions
	public static final String PERM_GLOBAL = "dornacraft.level";
	public static final String PERM_SET = PERM_GLOBAL + "." + ARG_SET;
	public static final String PERM_REMOVE = PERM_GLOBAL + "." + ARG_REMOVE;
	
	//Tableaux
	private final String[] HELP_MESSAGES = {MSG_SET,MSG_REMOVE};
	private final String[] SUB_COMMANDS = {ARG_SET,ARG_REMOVE};
	private final String[] PERMISSIONS = {PERM_SET,PERM_REMOVE};

	public CmdPrefix(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		super.setCommandSender(sender);
		
		if(sender.hasPermission(PERM_GLOBAL)) {
			try {
				if(args.length == 0) {
					this.sendDescriptionCommandMessage();
				} else if(args.length == 1) {
					for(int i = 0; i < this.getSubCommands().length; i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands()[i])) {
							if(sender.hasPermission(this.getPermissions()[i])) {
								sender.sendMessage(this.getHelpMessages()[i]);
							} else {
								this.sendLakePermissionMessage();
							}
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendHelpMessage(this.getHelpMessages());
					} else {
						this.sendWrongCommandMessage();
					}
				} else if(args.length == 2) {
					UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					
					if(player != null) {
						if(args[0].equalsIgnoreCase("set")) {
							if(sender.hasPermission(PERM_SET)) {
								this.openSetPrefixInventory(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(sender.hasPermission(PERM_REMOVE)) {
								this.removePrefix(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("help")) {
							this.sendTooManyArgumentsMessage();
						} else {
							this.sendWrongCommandMessage();
						}
					} else {
						this.sendUnknowPlayerMessage();
					}
				} else {
					for(int i = 0; i < this.getSubCommands().length; i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands()[i])) {
							this.sendTooManyArgumentsMessage();
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendTooManyArgumentsMessage();
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
	
	public void openSetPrefixInventory(OfflinePlayer player) throws Exception {
//		SetRankInventory setRankInventory = new SetPrefixInventory(player, sqlAccount.getRank(player), this);
//		Player p = Bukkit.getPlayer(getCommandSender().getName());
//		setRankInventory.openInventory(p);
	}
	
	public void setPrefix(OfflinePlayer player, Prefix prefix) {
		
	}
	
	public void removePrefix(OfflinePlayer player) {
		
	}
	
	@Override
	public String[] getHelpMessages() {
		return HELP_MESSAGES;
	}

	@Override
	public String[] getPermissions() {
		return PERMISSIONS;
	}

	@Override
	public String[] getSubCommands() {
		return SUB_COMMANDS;
	}
}
