package fr.voltariuss.dornacraft.playermanager.features.level;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.cmds.CustomCommand;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

public class CmdLevel extends CustomCommand {
	
	public static final String CMD_LABEL = "level";
	
	public CmdLevel() { super(DornacraftPlayerManager.class); }

	@Override
	public void executeMainCommand(CommandSender sender, String[] args) throws Exception {
		if(args.length == 0 || args.length == 1) {
			boolean targetAvailable = false;
			OfflinePlayer target = null;
			
			if(args.length == 0) {
				if(sender instanceof Player) {
					target = (Player) sender;
					targetAvailable = true;
				} else {
					Utils.sendErrorMessage(sender, ErrorMessage.NOT_FOR_CONSOLE);
				}				
			} else {
				target = AccountManager.getOfflinePlayer(args[1]);
				
				if(target == null) {
					Utils.sendErrorMessage(sender, ErrorMessage.UNKNOW_PLAYER);
				} else {
					targetAvailable = true;
				}
			}
			
			if(targetAvailable) {
				LevelManager.sendInfoLevel(sender, (Player) sender);				
			}
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.TOO_MANY_ARGUMENTS);
		}
	}
}
