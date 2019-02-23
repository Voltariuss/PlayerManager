package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.util.Arrays;

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
import fr.voltariuss.dornacraft.api.utils.MessageLevel;
import fr.voltariuss.dornacraft.api.utils.MessageUtils;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

public final class CmdPrefix extends DornacraftCommand {
	
	public static final String CMD_LABEL = "prefix";
	
	public static final String DESC_CMD = "Ouvre l'inventaire de gestion de son préfixe";
	
	public CmdPrefix() { 
		super(CMD_LABEL);
		// /prefix
		getCmdTreeExecutor().getRoot().setExecutor(new DornacraftCommandExecutor() {
					
					@Override
					public void execute(CommandSender sender, Command cmd, String label, String[] args) throws Exception {
						if(sender instanceof Player) {
							InventoryPrefix.openInventory((Player) sender, (Player) sender);										
						} else {
							Utils.sendSystemMessage(MessageLevel.ERROR, sender, MessageUtils.CONSOLE_NOT_ALLOWED);
						}
					}
				});
		// /prefix [joueur]
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), false), DESC_CMD, new DornacraftCommandExecutor() {
					
					@Override
					public void execute(CommandSender sender, Command cmd, String label, String[] args) throws Exception {
						OfflinePlayer target = AccountManager.getOfflinePlayer(args[0]);
						
						if(target != null) {
							InventoryPrefix.openInventory((Player) sender, target);
						} else {
							Utils.sendSystemMessage(MessageLevel.ERROR, sender, MessageUtils.PLAYER_UNKNOW);
						}
					}
				}, getCmdTreeExecutor().getRoot().getPermission(JavaPlugin.getPlugin(DornacraftPlayerManager.class).getCommand(CMD_LABEL)) + ".others")
			));
	}
}
