package fr.voltariuss.dornacraft.playermanager.features.permission;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraft.api.cmds.ComplexCommand;
import fr.voltariuss.dornacraft.api.cmds.CustomCommand;
import fr.voltariuss.dornacraft.api.cmds.SubCommand;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

public class CmdPermission extends CustomCommand implements CommandExecutor, ComplexCommand {
		
	public static final String CMD_LABEL = "permission";
	
	//Arguments
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_CLEAR = "clear";
	public static final String ARG_LIST = "list";

	public CmdPermission() {
		super(DornacraftPlayerManager.class);
		super.addSubCommand(new SubCommand(this, ARG_ADD, "Ajoute une permission à un joueur.", "<joueur> <permission>"));
		super.addSubCommand(new SubCommand(this, ARG_REMOVE, "Retire une permission à un joueur.", "<joueur> <permission>"));
		super.addSubCommand(new SubCommand(this, ARG_CLEAR, "Retire toutes les permissions d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_LIST, "Affiche la liste des permissions d'un joueur.", "<joueur>"));
	}

	@Override
	public void executeSubCommand(CommandSender sender, String[] args) throws Exception {
		OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
		String arg = args[0];
		
		if(player != null) {
			if(arg.equalsIgnoreCase(ARG_ADD)) {
				PermissionManager.addPermission(sender, player, args[2]);
			} else if(arg.equalsIgnoreCase(ARG_REMOVE)) {
				PermissionManager.removePermission(sender, player, args[2]);
			} else if(arg.equalsIgnoreCase(ARG_CLEAR)) {
				PermissionManager.removeAllPermissions(sender, player);
			} else {
				PermissionManager.sendListPermissions(sender, player);
			}
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.UNKNOW_PLAYER);
		}
	}

	@Override
	public void executeMainCommand(CommandSender sender, String[] args) throws Exception {
		super.sendHelpCommandMessage();
	}
}
