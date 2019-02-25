package fr.voltariuss.dornacraft.playermanager.features.permission;

import java.util.Arrays;

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

public class CmdHasPermission extends DornacraftCommand {
	
	public static final String CMD_LABEL = "haspermission";
	
	public static final String DESC_CMD = "Détermine si le joueur possède la permission spécifiée ou non";

	public CmdHasPermission() {
		super(CMD_LABEL);
		// /haspermission <permission> [player]
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("permission"), true), DESC_CMD, new DornacraftCommandExecutor() {
					
					@Override
					public void execute(CommandSender sender, Command cmd, String label, String[] args) throws Exception {
						if (((Player) sender).hasPermission(args[0])) {
							sender.sendMessage("§a§lOui");
						} else {
							sender.sendMessage("§c§lNon");
						}
					}
				}, null),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("player"), false), DESC_CMD, new DornacraftCommandExecutor() {
					
					@Override
					public void execute(CommandSender sender, Command cmd, String label, String[] args) throws Exception {
						Player target = AccountManager.getOfflinePlayer(args[1]).getPlayer();
						
						if (target != null) {
							if (((Player) target).hasPermission(args[0])) {
								sender.sendMessage("§a§lOui");
							} else {
								sender.sendMessage("§c§lNon");
							}
						} else {
							MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender, DornacraftAPIMessage.PLAYER_UNKNOW);
						}
					}
				}, null)
			));
	}
}
