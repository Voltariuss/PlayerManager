package fr.voltariuss.dornacraft.playermanager.features.prefix;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.cmds.CustomCommand;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

public final class CmdPrefix extends CustomCommand {
	
	public static final String CMD_LABEL = "prefix";
	
	public CmdPrefix() { super(DornacraftPlayerManager.class, CMD_LABEL); }

	@Override
	public void executeMainCommand(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0 || args.length == 1) {
			if(sender instanceof Player) {
				if(args.length == 0) {
					InventoryPrefix.openInventory((Player) sender, (Player) sender);										
				} else if(sender.hasPermission(super.getPermission() + ".others")) {
					OfflinePlayer target = AccountManager.getOfflinePlayer(args[0]);
					
					if(target != null) {
						InventoryPrefix.openInventory((Player) sender, target);
					} else {
						Utils.sendErrorMessage(sender, ErrorMessage.PLAYER_UNKNOW);
					}
				} else {
					Utils.sendErrorMessage(sender, ErrorMessage.PERMISSION_MISSING);
				}
			} else {
				Utils.sendErrorMessage(sender, ErrorMessage.PLAYER_ONLINE_ONLY);
			}
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.COMMAND_TOO_MANY_ARGUMENTS);
		}
	}
}
