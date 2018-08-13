package fr.voltariuss.dornacraftplayermanager.features.permission;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftapi.utils.ErrorMessage;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.AccountManager;

public class CmdPermission extends CustomCommand implements CommandExecutor {
		
	public static final String CMD_LABEL = "permissionmanager";
	
	//Arguments
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_REMOVEALL = "removeall";
	public static final String ARG_LIST = "list";

	public CmdPermission() {
		super(CMD_LABEL);
		this.getSubCommands().add(new SubCommand(this, ARG_ADD, "Ajoute une permission à un joueur.", "/" + CMD_LABEL + " " + ARG_ADD + " <joueur> <permission>", 1));
		this.getSubCommands().add(new SubCommand(this, ARG_REMOVE, "Retire une permission à un joueur.", "/" + CMD_LABEL + " " + ARG_REMOVE + " <joueur> <permission>", 2));
		this.getSubCommands().add(new SubCommand(this, ARG_REMOVEALL, "Retire toutes les permissions d'un joueur.", "/" + CMD_LABEL + " " + ARG_REMOVEALL + " <joueur>", 3));
		this.getSubCommands().add(new SubCommand(this, ARG_LIST, "Affiche la liste des permissions d'un joueur.", "/" + CMD_LABEL + " " + ARG_LIST + " <joueur>", 4));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		this.treatCommand(sender, args);
		return true;
	}

	@Override
	public void executeCommand(String[] args) throws Exception {
		OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
		String arg = args[0];
		
		if(player != null) {
			if(arg.equalsIgnoreCase(ARG_ADD)) {
				PermissionManager.addPermission(this.getSender(), player, args[2]);
			} else if(arg.equalsIgnoreCase(ARG_REMOVE)) {
				PermissionManager.removePermission(this.getSender(), player, args[2]);
			} else if(arg.equalsIgnoreCase(ARG_REMOVEALL)) {
				PermissionManager.removeAllPermissions(this.getSender(), player);
			} else {
				PermissionManager.sendListPermissions(this.getSender(), player);
			}	
		} else {
			Utils.sendErrorMessage(this.getSender(), ErrorMessage.UNKNOW_PLAYER);
		}
	}
}
