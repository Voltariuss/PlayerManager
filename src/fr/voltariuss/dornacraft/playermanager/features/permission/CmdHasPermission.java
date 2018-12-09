package fr.voltariuss.dornacraft.playermanager.features.permission;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.cmds.CustomCommand;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

public class CmdHasPermission extends CustomCommand {
	
	public static final String CMD_LABEL = "haspermission";

	public CmdHasPermission() {
		super(DornacraftPlayerManager.class, CMD_LABEL);
	}

	@Override
	public void executeMainCommand(CommandSender sender, String[] args) throws Exception {
		if (args.length == 2) {
			OfflinePlayer target = AccountManager.getOfflinePlayer(args[0]);
			
			if (target != null && target.isOnline()) {
				if (((Player) target).hasPermission(args[1])) {
					sender.sendMessage("§a§lOui");
				} else {
					sender.sendMessage("§c§lNon");
				}				
			} else {
				Utils.sendErrorMessage(sender, ErrorMessage.PLAYER_UNKNOW);
			}
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.COMMAND_NOT_ENOUGH_ARGUMENTS);
		}
	}
}
