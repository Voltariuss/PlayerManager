package fr.voltariuss.dornacraft.playermanager.features.level;

import java.util.Arrays;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraft.api.cmds.CommandArgument;
import fr.voltariuss.dornacraft.api.cmds.CommandArgumentType;
import fr.voltariuss.dornacraft.api.cmds.CommandNode;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommand;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommandExecutor;
import fr.voltariuss.dornacraft.api.msgs.DornacraftAPIMessage;
import fr.voltariuss.dornacraft.api.msgs.MessageLevel;
import fr.voltariuss.dornacraft.api.msgs.MessageUtils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;

public final class CmdAdminLevel extends DornacraftCommand {

	public static final String CMD_LABEL = "adminlevel";

	public static final String ARG_ADD = "add";
	public static final String ARG_ADDXP = "addxp";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_REMOVEXP = "removexp";
	public static final String ARG_SET = "set";
	public static final String ARG_RESET = "reset";
	public static final String ARG_INFO = "info";
	
	public static final String DESC_ADD = "Ajoute des niveaux à un joueur";
	public static final String DESC_ADDXP = "Ajoute de l'xp à un joueur";
	public static final String DESC_REMOVE = "Retire des niveaux à un joueur";
	public static final String DESC_REMOVEXP = "Retire de l'xp à un joueur";
	public static final String DESC_SET = "Définit le niveau d'un joueur";
	public static final String DESC_RESET = "Réinitialise le niveau d'un joueur";
	public static final String DESC_INFO = "Affiche le niveau d'un joueur";

	public CmdAdminLevel() {
		super(CMD_LABEL);
		DornacraftCommandExecutor dce = new DornacraftCommandExecutor() {
			
			@Override
			public void execute(CommandSender sender, Command cmd, String label, String[] args) throws Exception {
				OfflinePlayer target = AccountManager.getOfflinePlayer(args[1]);

				if (target != null) {
					if (args[0].equalsIgnoreCase(ARG_ADD)) {
						LevelManager.addLevel(sender, target, Integer.parseInt(args[2]));
					} else if (args[0].equalsIgnoreCase(ARG_ADDXP)) {
						LevelManager.addXp(sender, target, Integer.parseInt(args[2]));
					} else if (args[0].equalsIgnoreCase(ARG_REMOVE)) {
						LevelManager.removeLevel(sender, target, Integer.parseInt(args[2]));
					} else if (args[0].equalsIgnoreCase(ARG_REMOVEXP)) {
						LevelManager.removeXp(sender, target, Integer.parseInt(args[2]));
					} else if (args[0].equalsIgnoreCase(ARG_SET)) {
						LevelManager.setLevel(sender, target, Integer.parseInt(args[2]));
					} else if (args[0].equalsIgnoreCase(ARG_RESET)) {
						LevelManager.resetLevel(sender, target);
					} else {
						LevelManager.sendInfo(sender, target);
					}
				} else {
					MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender, DornacraftAPIMessage.PLAYER_UNKNOW);
				}
			}
		};
		// /levelmanager add <joueur> <quantité>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_ADD), DESC_ADD),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), true), DESC_ADD),
				new CommandNode(new CommandArgument(CommandArgumentType.NUMBER.getCustomArgType("quantité"), true), DESC_ADD, dce, null)
			));
		// /levelmanager addxp <joueur> <quantité>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_ADDXP), DESC_ADDXP),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), true), DESC_ADDXP),
				new CommandNode(new CommandArgument(CommandArgumentType.NUMBER.getCustomArgType("quantité"), true), DESC_ADDXP, dce, null)
			));
		// /levelmanager remove <joueur> <quantité>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_REMOVE), DESC_REMOVE),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), true), DESC_REMOVE),
				new CommandNode(new CommandArgument(CommandArgumentType.NUMBER.getCustomArgType("quantité"), true), DESC_REMOVE, dce, null)
			));
		// /levelmanager removexp <joueur> <quantité>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_REMOVEXP), DESC_REMOVEXP),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), true), DESC_REMOVEXP),
				new CommandNode(new CommandArgument(CommandArgumentType.NUMBER.getCustomArgType("quantité"), true), DESC_REMOVEXP, dce, null)
			));
		// /levelmanager set <joueur> <quantité>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_SET), DESC_SET),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), true), DESC_SET),
				new CommandNode(new CommandArgument(CommandArgumentType.NUMBER.getCustomArgType("quantité"), true), DESC_SET, dce, null)
			));
		// /levelmanager reset <joueur>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_RESET), DESC_RESET),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), true), DESC_RESET, dce, null)
			));
		// /levelmanager info <joueur>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_INFO), DESC_INFO),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), true), DESC_INFO, dce, null)
			));
	}
}
