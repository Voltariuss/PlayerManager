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
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;

public class CmdLevel extends CustomCommand implements CommandExecutor {

	//Instances
	private final SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	
	//Messages d'erreur
	public static final String PLAYER_LEVEL_ALREADY_MAX = "Le joueur possède déjà le niveau maximum.";
	public static final String PLAYER_LEVEL_ALREADY_MIN = "Le joueur possède déjà le niveau le plus bas.";
	public static final String INVALIDE_NUMBER_POSITIVE = "Le nombre saisie doit être positif.";
	public static final String MUST_BE_IN_INTERVAL = "Le nombre spécifié doit être compris entre 1 et " + DornacraftPlayerManager.getInstance().getMaxLevel() + ".";
	
	//Arguments
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_SET = "set";
	public static final String ARG_RESET = "reset";
	public static final String ARG_INFO = "info";
	
	//Messages d'aide sur les commandes
	public static final String MSG_ADD = "§ePour ajouter des niveaux à un joueur :\n §6/level add §b<joueur> <nombre>";
	public static final String MSG_REMOVE = "§ePour retirer des niveaux à un joueur :\n §6/level remove §b<joueur> <nombre>";
	public static final String MSG_SET = "§ePour définir le niveau d'un joueur :\n §6/level set §b<joueur> <nombre>";
	public static final String MSG_RESET = "§ePour reset le niveau d'un joueur :\n §6/level reset §b<joueur>";
	public static final String MSG_INFO = "§ePour afficher le niveau d'un joueur :\n §6/level info §b<joueur>";
	
	//Permissions
	public static final String PERM_GLOBAL = "dornacraft.level";
	public static final String PERM_ADD = PERM_GLOBAL + "." + ARG_ADD;
	public static final String PERM_REMOVE = PERM_GLOBAL + "." + ARG_REMOVE;
	public static final String PERM_SET = PERM_GLOBAL + "." + ARG_SET;
	public static final String PERM_RESET = PERM_GLOBAL + "." + ARG_RESET;
	public static final String PERM_INFO = PERM_GLOBAL + "." + ARG_INFO;
	
	//Tableaux
	private final String[] HELP_MESSAGES = {MSG_ADD,MSG_REMOVE,MSG_SET,MSG_RESET,MSG_INFO};
	private final String[] SUB_COMMANDS = {ARG_ADD,ARG_REMOVE,ARG_SET,PERM_RESET,PERM_INFO};
	private final String[] PERMISSIONS = {PERM_ADD,PERM_REMOVE,PERM_SET,PERM_RESET,PERM_INFO};

	public CmdLevel(String cmdLabel) {
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
						if(args[0].equalsIgnoreCase("add")) {
							if(sender.hasPermission(PERM_ADD)) {
								this.sendNotEnoughArgumentsMessage();
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(sender.hasPermission(PERM_REMOVE)) {
								this.sendNotEnoughArgumentsMessage();
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("set")) {
							if(sender.hasPermission(PERM_SET)) {
								this.sendNotEnoughArgumentsMessage();
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("reset")) {
							if(sender.hasPermission(PERM_RESET)) {
								this.resetLevel(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("info")) {
							if(sender.hasPermission(PERM_INFO)) {
								this.sendInfoLevel(player);
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
				} else if(args.length == 3) {
					UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					
					if(args[0].equalsIgnoreCase("add")) {
						if(sender.hasPermission(PERM_ADD)) {
							this.addLevel(player, Integer.parseInt(args[2]));
						} else {
							this.sendLakePermissionMessage();
						}
					} else if(args[0].equalsIgnoreCase("remove")) {
						if(sender.hasPermission(PERM_REMOVE)) {
							this.removeLevel(player, Integer.parseInt(args[2]));
						} else {
							this.sendLakePermissionMessage();
						}
					} else if(args[0].equalsIgnoreCase("set")) {
						if(sender.hasPermission(PERM_REMOVE)) {
							this.setLevel(player, Integer.parseInt(args[2]));
						} else {
							this.sendLakePermissionMessage();
						}
					} else {
						for(int i = 3; i < this.getSubCommands().length; i++) {
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
	
	public void addLevel(OfflinePlayer player, int nbNiveaux) throws Exception {
		int playerLevel = sqlAccount.getLevel(player);
		
		if(playerLevel < 80) {
			if(nbNiveaux > 0) {
				int newLevel = playerLevel + nbNiveaux;
				
				if(newLevel > 80 ) {
					nbNiveaux = 80 - playerLevel;
					newLevel = playerLevel + nbNiveaux;
				}
				
				sqlAccount.setLevel(player, newLevel);
				this.sendMessage("§aLe joueur §b" + player.getName() + " §aa reçu §e" + nbNiveaux + " niveaux§a.");
				this.sendMessage("§aLe joueur §b" + player.getName() + " §a est désormais niveau §6" + newLevel + "§a.");
			} else {
				this.sendErrorMessage(INVALIDE_NUMBER_POSITIVE);
			}
		} else {
			this.sendErrorMessage(PLAYER_LEVEL_ALREADY_MAX);
		}
	}
	
	public void removeLevel(OfflinePlayer player, int nbNiveaux ) throws Exception {
		int playerLevel = sqlAccount.getLevel(player);
		
		if(playerLevel > 1) {
			if(nbNiveaux > 0) {
				int newLevel = playerLevel - nbNiveaux;

				if(newLevel > 80 ) {
					nbNiveaux = 80 - playerLevel;
					newLevel = playerLevel + nbNiveaux;
				}
				
				sqlAccount.setLevel(player, newLevel);
				this.sendMessage("§aLe joueur §b" + player.getName() + " §aa perdu §e" + nbNiveaux + " niveaux§a.");
				this.sendMessage("§aLe joueur §b" + player.getName() + " §a est désormais niveau §6" + newLevel + "§a.");
			} else {
				this.sendErrorMessage(INVALIDE_NUMBER_POSITIVE);
			}
		} else {
			this.sendErrorMessage(PLAYER_LEVEL_ALREADY_MIN);
		}
	}
	
	public void setLevel(OfflinePlayer player, int newLevel) throws Exception {
		if(newLevel <= 80 && newLevel >= 1) {
			sqlAccount.setLevel(player, newLevel);
			this.sendMessage("§aLe joueur §b" + player.getName() + " §a est désormais niveau §6" + newLevel + "§a.");
		} else {
			this.sendErrorMessage(MUST_BE_IN_INTERVAL);
		}
	}
	
	public void resetLevel(OfflinePlayer player) throws Exception {
		sqlAccount.setLevel(player, 1);
		this.sendMessage("§aLe niveau du joueur §b" + player.getName() + " §aa bien été réinitilisé.");
		this.sendMessage("§aLe joueur §b" + player.getName() + " §a est désormais niveau §6" + 1 + "§a.");
	}
	
	public void sendInfoLevel(OfflinePlayer player) throws Exception {
		int level = sqlAccount.getLevel(player);
		this.sendMessage("§6Niveau du joueur §b" + player.getName() + " §6: §e" + level);
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
