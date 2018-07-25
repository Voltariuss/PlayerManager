package fr.voltariuss.dornacraftplayermanager.cmd;

import java.util.ArrayList;
import java.util.Iterator;
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
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;
import fr.voltariuss.dornacraftplayermanager.sql.SQLPerm;

public class CmdPerm extends CustomCommand implements CommandExecutor {
	
	//Instances
	private final SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	private final SQLPerm sqlPerm = DornacraftPlayerManager.getInstance().getSqlPerm();
	
	//Messages d'erreur
	public static final String NO_HAS_PERMISSIONS = "�cCe joueur ne poss�de pas de permissions particuli�res.";
	public static final String ALREADY_HAS_PERMISSION = "Ce joueur poss�de d�j� cette permission.";
	public static final String NO_HAS_SPECIFIED_PERMISSION = "Ce joueur ne poss�de pas cette permission.";
	
	//Arguments
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_REMOVEALL = "removeall";
	public static final String ARG_LIST = "list";

	public CmdPerm(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
		this.getSubCommands().add(new SubCommand(ARG_ADD, "Pour ajouter une permission � un joueur :\n �6/perm add �b<joueur> <permission>", 1));
		this.getSubCommands().add(new SubCommand(ARG_REMOVE, "Pour retirer une permission � un joueur :\n �6/perm remove �b<joueur> <permission>", 2));
		this.getSubCommands().add(new SubCommand(ARG_REMOVEALL, "Pour retirer toutes les permissions d'un joueur :\n �6/perm removeall �b<joueur>", 3));
		this.getSubCommands().add(new SubCommand(ARG_LIST, "Pour afficher la liste des permissions d'un joueur :\n �6/perm list �b<joueur>", 4));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		super.setSender(sender);
		
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
					UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					
					if(player != null) {
						if(args[0].equalsIgnoreCase("add")) {
							if(sender.hasPermission(this.getSubCommand(ARG_ADD).getPermission())) {
								this.sendNotEnoughArgumentsMessage();
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(sender.hasPermission(this.getSubCommand(ARG_REMOVE).getPermission())) {
								this.sendNotEnoughArgumentsMessage();
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("removeall")) {
							if(sender.hasPermission(this.getSubCommand(ARG_REMOVEALL).getPermission())) {
								this.removeAllPerm(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("list")) {
							if(sender.hasPermission(this.getSubCommand(ARG_LIST).getPermission())) {
								this.sendListPerm(player);
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
						if(sender.hasPermission(this.getSubCommand(ARG_ADD).getPermission())) {
							this.addPerm(player, args[2]);
						} else {
							this.sendLakePermissionMessage();
						}
					} else if(args[0].equalsIgnoreCase("remove")) {
						if(sender.hasPermission(this.getSubCommand(ARG_REMOVE).getPermission())) {
							this.removePerm(player, args[2]);
						} else {
							this.sendLakePermissionMessage();
						}
					} else {
						for(int i = 2; i < this.getSubCommands().size(); i++) {
							if(args[0].equalsIgnoreCase(this.getSubCommands().get(i).getArg())) {
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
					for(int i = 0; i < this.getSubCommands().size(); i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands().get(i).getArg())) {
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
	
	public void addPerm(OfflinePlayer player, String permission) throws Exception {
		if(!sqlPerm.hasPermission(player, permission)) {
			sqlPerm.addPermission(player, permission);
			this.sendMessage("�aLa permission �6" + permission + " �aa �t� ajout�e au joueur �b" + player.getName() + "�a.");
		} else {
			this.sendErrorMessage(ALREADY_HAS_PERMISSION);
		}
	}
	
	public void removePerm(OfflinePlayer player, String permission) throws Exception {
		if(sqlPerm.hasPermission(player, permission)) {
			sqlPerm.removePermission(player, permission);
			this.sendMessage("�aLa permission �6" + permission + " �aa �t� retir�e au joueur �b" + player.getName() + "�a.");
		} else {
			this.sendErrorMessage(NO_HAS_SPECIFIED_PERMISSION);
		}
	}
	
	public void removeAllPerm(OfflinePlayer player) throws Exception {
		if(sqlPerm.hasPermission(player)) {
			sqlPerm.removeAllPermissions(player);
			this.sendMessage("�aToutes les permissions sp�cifiques ont �t� retir�es au joueur �b" + player.getName() + "�a.");
		} else {
			this.sendErrorMessage(NO_HAS_PERMISSIONS);
		}
	}
	
	public void sendListPerm(OfflinePlayer player) throws Exception {
		ArrayList<String> permissions = sqlPerm.getPermissions(player);
		
		if(!permissions.isEmpty()) {
			String listPermissions = "";
			Iterator<String> iterator = permissions.iterator();
			
			while(iterator.hasNext()) {
				String permission = "�f" + iterator.next();
				listPermissions = listPermissions + permission;
				
				if(iterator.hasNext()) {
					listPermissions = listPermissions + "�e, ";
				}
			}
			this.sendMessage("�6Permissions du joueur �b" + player.getName() + " �6: " + listPermissions);
		} else {
			this.sendErrorMessage(NO_HAS_PERMISSIONS);
		}
	}
}