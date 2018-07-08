package fr.voltariuss.dornacraftplayermanager.cmd;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.SubRank;
import fr.voltariuss.dornacraftplayermanager.inventories.SetSubRankInventory;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;
import fr.voltariuss.dornacraftplayermanager.sql.SQLSubRank;

public class CmdSubRank extends CustomCommand implements CommandExecutor {
	
	//Instances
	private SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	private SQLSubRank sqlSubRank = DornacraftPlayerManager.getInstance().getSQLSubRank(); 
	
	//Messages d'erreur
	public static final String UNKNOW_SUBRANK = Utils.getErrorPrefix() + "Le sous-rang spécifié est incorrect.";
	public static final String HAS_SUBRANK = Utils.getErrorPrefix() + "Ce joueur possède déjà le sous-rang spécifié.";
	public static final String DONT_HAS_SPECIFIED_SUBRANK = Utils.getErrorPrefix() + "Ce joueur ne possède pas le sous-rang spécifié.";
	public static final String DONT_HAS_SUBRANK = Utils.getErrorPrefix() + "Ce joueur ne possède pas de sous-rang.";
	
	//Arguments
	public static final String ARG_SUBRANK_SET = "set";
	public static final String ARG_SUBRANK_REMOVEALL = "removeall";
	public static final String ARG_SUBRANK_LIST = "list";
	
	//Messages d'aide sur les commandes
	public static final String MSG_SUBRANK_SET = "§ePour définir les sous-rangs d'un joueur:\n §6/subrank set §b<joueur>";
	public static final String MSG_SUBRANK_REMOVEALL = "§ePour retirer tous les sous-rangs d'un joueur:\n §6/subrank removeall §b<joueur>";
	public static final String MSG_SUBRANK_LIST = "§ePour voir la liste des sous-rangs d'un joueur:\n §6/subrank list §b<joueur>";
	
	//Permissions
	public static final String PERM_SUBRANK_GLOBAL = "dornacraft.subrank";
	public static final String PERM_SUBRANK_SET = PERM_SUBRANK_GLOBAL + "." + ARG_SUBRANK_SET;
	public static final String PERM_SUBRANK_REMOVEALL = PERM_SUBRANK_GLOBAL + "." + ARG_SUBRANK_REMOVEALL;
	public static final String PERM_SUBRANK_LIST = PERM_SUBRANK_GLOBAL + "." + ARG_SUBRANK_LIST;
	
	//Tableaux
	private final String[] HELP_MESSAGES = {MSG_SUBRANK_SET,MSG_SUBRANK_REMOVEALL,MSG_SUBRANK_LIST};
	private final String[] SUB_COMMANDS = {ARG_SUBRANK_SET,ARG_SUBRANK_REMOVEALL,ARG_SUBRANK_LIST};
	private final String[] PERMISSIONS = {PERM_SUBRANK_SET,PERM_SUBRANK_REMOVEALL,PERM_SUBRANK_LIST};
	
	public CmdSubRank(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		super.setCommandSender(sender);
		
		if(sender.hasPermission(PERM_SUBRANK_GLOBAL)) {
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
							if(sender.hasPermission(PERM_SUBRANK_SET)) {
								this.openSetSubRankInventory(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("removeall")) {
							if(sender.hasPermission(PERM_SUBRANK_REMOVEALL)) {
								this.removeAllSubRank(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("list")) {
							if(sender.hasPermission(PERM_SUBRANK_LIST)) {
								this.sendListSubRank(player);
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
	
	public void openSetSubRankInventory(OfflinePlayer player) throws Exception {
		SetSubRankInventory setRankInventory = new SetSubRankInventory(player, sqlSubRank.getSubRanks(player), this);
		Player p = Bukkit.getPlayer(getCommandSender().getName());
		setRankInventory.openInventory(p);
	}
	
	public void addSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		if(!sqlSubRank.hasSubRank(player, subRank)) {
			sqlSubRank.addSubRank(player, subRank);
			this.sendMessage("§aLe sous-rang §6" + subRank.getName() + " §aa bien été attribué au joueur §b" + player.getName() + "§a.");
		} else {
			this.sendErrorMessage(HAS_SUBRANK);
		}
	}
	
	public void removeSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		if(sqlSubRank.hasSubRank(player, subRank)) {
			sqlSubRank.removeSubRank(player, subRank);
			this.sendMessage("§aLe sous-rang §6" + subRank.getName() + " §aa bien été retiré au joueur §b" + player.getName() + "§a.");
		} else {
			this.sendErrorMessage(DONT_HAS_SPECIFIED_SUBRANK);
		}
	}
	
	public void removeAllSubRank(OfflinePlayer player) throws Exception {
		if(sqlSubRank.hasSubRank(player)) {
			sqlSubRank.removeAllSubRanks(player);
			this.sendMessage("§aTous les sous-rangs ont été retirés au joueur §b" + player.getName() + "§a.");
		} else {
			this.sendErrorMessage(DONT_HAS_SUBRANK);
		}
	}
	
	public void sendListSubRank(OfflinePlayer player) throws Exception {
		ArrayList<SubRank> subRanks = sqlSubRank.getSubRanks(player);
		
		if(sqlSubRank.hasSubRank(player)) {
			this.sendMessage("§6Liste des sous-rangs du joueur §b" + player.getName() + " §6:");
			
			while(!subRanks.isEmpty()) {
				SubRank subRank = subRanks.get(subRanks.size() - 1);
				this.sendMessage("§f - " + subRank.getSubRankColor() + subRank.getName());		
				subRanks.remove(subRanks.size() - 1);
			}		
		} else {
			this.sendErrorMessage(DONT_HAS_SUBRANK);
		}
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