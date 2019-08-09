package fr.voltariuss.playermanager.features.level;

import org.bukkit.OfflinePlayer;
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

public class CmdLevel extends DornacraftCommand {

	public static final String CMD_LABEL = "level";

	public static final String DESC_CMD = "Consulte le niveau du joueur spécifié";

	/**
	 * Constructeur de la commande /level
	 */
	public CmdLevel() {
		super(CMD_LABEL);
		DornacraftCommandExecutor dce = new DornacraftCommandExecutor() {

			@Override
			public void execute(CommandSender sender, Command cmd, String label, String[] args) throws Exception {
				boolean targetAvailable = false;
				OfflinePlayer target = null;

				if (args.length == 0) {
					if (sender instanceof Player) {
						target = (Player) sender;
						targetAvailable = true;
					} else {
						UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.CONSOLE_NOT_ALLOWED);
					}
				} else {
					target = AccountManager.getOfflinePlayer(args[0]);

					if (target == null) {
						UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.PLAYER_UNKNOW);
					} else {
						targetAvailable = true;
					}
				}

				if (targetAvailable) {
					LevelManager.sendInfo(sender, target);
				}
			}
		};
		// /level
		getCmdTreeExecutor().getRoot().setExecutor(dce);
		// /level [joueur]
		getCmdTreeExecutor().addSubCommand(
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), false),
						DESC_CMD, dce, null));
	}
}
