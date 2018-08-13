package fr.voltariuss.dornacraftplayermanager.features.prefix;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftapi.utils.ErrorMessage;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.AccountManager;

public class CmdPrefix extends CustomCommand implements CommandExecutor {
	
	public static final String CMD_LABEL = "prefixmanager";
	
	//Arguments
	public static final String ARG_SET = "set";

	public CmdPrefix() {
		super(CMD_LABEL);
		this.getSubCommands().add(new SubCommand(this, ARG_SET, "Modifie le préfixe d'un joueur.", "/" + CMD_LABEL + " " + ARG_SET + " <joueur>", 1));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		this.treatCommand(sender, args);
		return true;
	}
	
	@Override
	public void executeCommand(String[] args) throws Exception {
		OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
		
		if(player != null) {
			PrefixManager.openSetPrefixInventory(this.getSender(), player);
		} else {
			Utils.sendErrorMessage(this.getSender(), ErrorMessage.UNKNOW_PLAYER);
		}
	}
}
