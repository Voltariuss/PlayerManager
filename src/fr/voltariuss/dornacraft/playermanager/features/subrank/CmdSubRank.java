package fr.voltariuss.dornacraft.playermanager.features.subrank;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraft.api.MessageLevel;
import fr.voltariuss.dornacraft.api.UtilsAPI;
import fr.voltariuss.dornacraft.api.cmds.CommandArgument;
import fr.voltariuss.dornacraft.api.cmds.CommandArgumentType;
import fr.voltariuss.dornacraft.api.cmds.CommandNode;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommand;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommandExecutor;
import fr.voltariuss.dornacraft.playermanager.AccountManager;

/**
 * Classe de gestion de la commande /subrank
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class CmdSubRank extends DornacraftCommand {

	public static final String CMD_LABEL = "subrank";

	public static final String ARG_SET = "set";
	public static final String ARG_CLEAR = "clear";
	public static final String ARG_LIST = "list";

	public static final String DESC_SET = "D�finit les sous-rangs d'un joueur";
	public static final String DESC_CLEAR = "Retire tous les sous-rangs d'un joueur";
	public static final String DESC_LIST = "Affiche la liste des sous-rangs d'un joueur";

	/**
	 * Constructeur de la commande /subrank
	 */
	public CmdSubRank() {
		super(CMD_LABEL);
		DornacraftCommandExecutor dce = new DornacraftCommandExecutor() {

			@Override
			public void execute(CommandSender sender, Command cmd, String label, String[] args) throws Exception {
				OfflinePlayer target = AccountManager.getOfflinePlayer(args[1]);

				if (target != null) {
					if (args[0].equalsIgnoreCase(ARG_SET)) {
						InventorySubRank.openInventory(sender, target);
					} else if (args[0].equalsIgnoreCase(ARG_CLEAR)) {
						SubRankManager.removeAllSubRank(sender, target);
					} else {
						SubRankManager.sendListSubRankMessage(sender, target);
					}
				} else {
					UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.PLAYER_UNKNOW);
				}
			}
		};
		// /subrank set <joueur>
		getCmdTreeExecutor().addSubCommand(new CommandNode(new CommandArgument(ARG_SET), DESC_SET), new CommandNode(
				new CommandArgument(CommandArgumentType.STRING.getCustomArgType(UtilsAPI.COMMAND_PLAYER_INPUT_NAME), true), DESC_SET, dce, null));
		// /subrank clear <joueur>
		getCmdTreeExecutor().addSubCommand(new CommandNode(new CommandArgument(ARG_CLEAR), DESC_CLEAR),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType(UtilsAPI.COMMAND_PLAYER_INPUT_NAME), true),
						DESC_CLEAR, dce, null));
		// /subrank list <joueur>
		getCmdTreeExecutor().addSubCommand(new CommandNode(new CommandArgument(ARG_LIST), DESC_LIST),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType(UtilsAPI.COMMAND_PLAYER_INPUT_NAME), true),
						DESC_LIST, dce, null));
	}
}