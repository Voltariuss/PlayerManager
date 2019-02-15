package fr.voltariuss.dornacraft.playermanager.features.permission;

import java.util.Arrays;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraft.api.cmds.CommandArgument;
import fr.voltariuss.dornacraft.api.cmds.CommandArgumentType;
import fr.voltariuss.dornacraft.api.cmds.CommandNode;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommand;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommandExecutor;
import fr.voltariuss.dornacraft.api.utils.MessageLevel;
import fr.voltariuss.dornacraft.api.utils.MessageUtils;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;

public final class CmdPermission extends DornacraftCommand {
		
	public static final String CMD_LABEL = "permission";
	
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_CLEAR = "clear";
	public static final String ARG_LIST = "list";
	
	public static final String DESC_ADD = "Ajoute une permission � un joueur";
	public static final String DESC_REMOVE = "Retire une permission � un joueur";
	public static final String DESC_CLEAR = "Retire toutes les permissions d'un joueur";
	public static final String DESC_LIST = "Affiche la liste des permissions d'un joueur";
	
	public CmdPermission() {
		super(CMD_LABEL);
		DornacraftCommandExecutor dce = new DornacraftCommandExecutor() {
			
			@Override
			public void execute(CommandSender sender, Command cmd, String[] args) throws Exception {
				OfflinePlayer target = AccountManager.getOfflinePlayer(args[1]);
				
				if (target != null) {
					if (args[0].equalsIgnoreCase(ARG_ADD)) {
						PermissionManager.addPermission(sender, target, args[2]);
					} else if (args[0].equalsIgnoreCase(ARG_REMOVE)) {
						PermissionManager.removePermission(sender, target, args[2]);
					} else if (args[0].equalsIgnoreCase(ARG_CLEAR)) {
						PermissionManager.removeAllPermissions(sender, target);
					} else {
						PermissionManager.sendListPermissions(sender, target);
					}
				} else {
					Utils.sendSystemMessage(MessageLevel.ERROR, sender, MessageUtils.PLAYER_UNKNOW);
				}
			}
		};
		// /permission add <player> <permission>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_ADD), DESC_ADD),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_ADD),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_ADD, dce, null)
			));
		// /permission remove <player> <permission>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_REMOVE), DESC_REMOVE),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_REMOVE),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_REMOVE, dce, null)
			));
		// /permission clear <player>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_CLEAR), DESC_CLEAR),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_CLEAR, dce, null)
			));
		// /permission list <player>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_LIST), DESC_LIST),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_LIST, dce, null)
			));
	}
}
