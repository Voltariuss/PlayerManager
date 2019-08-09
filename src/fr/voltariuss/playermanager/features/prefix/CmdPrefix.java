package fr.voltariuss.playermanager.features.prefix;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.voltariuss.playermanager.AccountManager;
import fr.voltariuss.playermanager.PlayerManager;
import fr.voltariuss.simpledevapi.MessageLevel;
import fr.voltariuss.simpledevapi.UtilsAPI;
import fr.voltariuss.simpledevapi.cmds.CommandArgument;
import fr.voltariuss.simpledevapi.cmds.CommandArgumentType;
import fr.voltariuss.simpledevapi.cmds.CommandNode;
import fr.voltariuss.simpledevapi.cmds.DornacraftCommand;
import fr.voltariuss.simpledevapi.cmds.DornacraftCommandExecutor;

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
					UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.CONSOLE_NOT_ALLOWED);
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
									UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.PLAYER_UNKNOW);
								}
							}
						}, getCmdTreeExecutor().getRoot().getPermission(
								JavaPlugin.getPlugin(PlayerManager.class).getCommand(CMD_LABEL)) + ".others"));
	}
}
