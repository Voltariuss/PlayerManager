package fr.voltariuss.dornacraft.playermanager.features.prefix;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.voltariuss.dornacraft.api.cmds.CommandArgument;
import fr.voltariuss.dornacraft.api.cmds.CommandArgumentType;
import fr.voltariuss.dornacraft.api.cmds.CommandNode;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommand;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommandExecutor;
import fr.voltariuss.dornacraft.api.msgs.DornacraftAPIMessage;
import fr.voltariuss.dornacraft.api.msgs.MessageLevel;
import fr.voltariuss.dornacraft.api.msgs.MessageUtils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

/**
 * Classe de gestion de la commande /prefix
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class CmdPrefix extends DornacraftCommand {

	public static final String CMD_LABEL = "prefix";

	public static final String DESC_CMD = "Ouvre l'inventaire de gestion de son préfixe";

	/**
	 * Constructeur de la commande /prefix
	 */
	public CmdPrefix() {
		super(CMD_LABEL);
		// /prefix
		getCmdTreeExecutor().getRoot().setExecutor(new DornacraftCommandExecutor() {

			@Override
			public void execute(CommandSender sender, Command cmd, String label, String[] args) throws Exception {
				if (sender instanceof Player) {
					InventoryPrefix.openInventory((Player) sender, (Player) sender);
				} else {
					MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender,
							DornacraftAPIMessage.CONSOLE_NOT_ALLOWED);
				}
			}
		});
		// /prefix [joueur]
		getCmdTreeExecutor().addSubCommand(
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), false),
						DESC_CMD, new DornacraftCommandExecutor() {

							@Override
							public void execute(CommandSender sender, Command cmd, String label, String[] args)
									throws Exception {
								OfflinePlayer target = AccountManager.getOfflinePlayer(args[0]);

								if (target != null) {
									InventoryPrefix.openInventory((Player) sender, target);
								} else {
									MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender,
											DornacraftAPIMessage.PLAYER_UNKNOW);
								}
							}
						},
						getCmdTreeExecutor().getRoot().getPermission(
								JavaPlugin.getPlugin(DornacraftPlayerManager.class).getCommand(CMD_LABEL))
								+ ".others"));
	}
}
