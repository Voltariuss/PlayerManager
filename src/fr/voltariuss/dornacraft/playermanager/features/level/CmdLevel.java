package fr.voltariuss.dornacraft.playermanager.features.level;

import org.bukkit.OfflinePlayer;
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
 * Classe de gestion de la commande /level
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
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
						MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender,
								DornacraftAPIMessage.CONSOLE_NOT_ALLOWED);
					}
				} else {
					target = AccountManager.getOfflinePlayer(args[0]);

					if (target == null) {
						MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender, DornacraftAPIMessage.PLAYER_UNKNOW);
					} else {
						targetAvailable = true;
					}
				}

				if (targetAvailable) {
					LevelManager.sendInfo(sender, (Player) sender);
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
