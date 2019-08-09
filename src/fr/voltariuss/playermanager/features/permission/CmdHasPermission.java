package fr.voltariuss.playermanager.features.permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.voltariuss.playermanager.AccountManager;
import fr.voltariuss.simpledevapi.MessageLevel;
import fr.voltariuss.simpledevapi.UtilsAPI;
import fr.voltariuss.simpledevapi.cmds.CommandArgument;
import fr.voltariuss.simpledevapi.cmds.CommandArgumentType;
import fr.voltariuss.simpledevapi.cmds.CommandNode;
import fr.voltariuss.simpledevapi.cmds.DornacraftCommand;
import fr.voltariuss.simpledevapi.cmds.DornacraftCommandExecutor;

public class CmdHasPermission extends DornacraftCommand {

	public static final String CMD_LABEL = "haspermission";

	public static final String DESC_CMD = "Détermine si le joueur possède la permission spécifiée ou non";

	/**
	 * Constructeur de la commande /haspermission
	 */
	public CmdHasPermission() {
		super(CMD_LABEL);
		// /haspermission <permission> [joueur]
		getCmdTreeExecutor().addSubCommand(
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("permission"), true),
						DESC_CMD, new DornacraftCommandExecutor() {

							@Override
							public void execute(CommandSender sender, Command cmd, String label, String[] args)
									throws Exception {
								if (sender.hasPermission(args[0])) {
									sender.sendMessage("§a§lOui");
								} else {
									sender.sendMessage("§c§lNon");
								}
							}
						}, null),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), false),
						DESC_CMD, new DornacraftCommandExecutor() {

							@Override
							public void execute(CommandSender sender, Command cmd, String label, String[] args)
									throws Exception {
								Player target = AccountManager.getOfflinePlayer(args[1]).getPlayer();

								if (target != null) {
									if (((Player) target).hasPermission(args[0])) {
										sender.sendMessage("§a§lOui");
									} else {
										sender.sendMessage("§c§lNon");
									}
								} else {
									UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.PLAYER_UNKNOW);
								}
							}
						}, null));
	}
}
