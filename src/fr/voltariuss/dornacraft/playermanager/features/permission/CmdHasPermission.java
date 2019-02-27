package fr.voltariuss.dornacraft.playermanager.features.permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.cmds.CommandArgument;
import fr.voltariuss.dornacraft.api.cmds.CommandArgumentType;
import fr.voltariuss.dornacraft.api.cmds.CommandNode;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommand;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommandExecutor;
import fr.voltariuss.dornacraft.api.msgs.DornacraftAPIMessage;
import fr.voltariuss.dornacraft.api.msgs.MessageLevel;
import fr.voltariuss.dornacraft.api.msgs.MessageUtils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;

/**
 * Classe de gestion de la commande /haspermission
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public class CmdHasPermission extends DornacraftCommand {

	public static final String CMD_LABEL = "haspermission";

	public static final String DESC_CMD = "Détermine si le joueur possède la permission spécifiée ou non";

	/**
	 * Constructeur de la commande /haspermission
	 */
	public CmdHasPermission() {
		super(CMD_LABEL);
		// /haspermission <permission> [player]
		getCmdTreeExecutor().addSubCommand(
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("permission"), true),
						DESC_CMD, new DornacraftCommandExecutor() {

							@Override
							public void execute(CommandSender sender, Command cmd, String label, String[] args)
									throws Exception {
								if (((Player) sender).hasPermission(args[0])) {
									sender.sendMessage("§a§lOui");
								} else {
									sender.sendMessage("§c§lNon");
								}
							}
						}, null),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("player"), false),
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
									MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender,
											DornacraftAPIMessage.PLAYER_UNKNOW);
								}
							}
						}, null));
	}
}
