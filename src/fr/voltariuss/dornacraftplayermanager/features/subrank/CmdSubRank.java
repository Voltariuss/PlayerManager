package fr.voltariuss.dornacraftplayermanager.features.subrank;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftapi.utils.ErrorMessage;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.AccountManager;

public class CmdSubRank extends CustomCommand implements CommandExecutor {
	
	public static final String CMD_LABEL = "subrankmanager";
		
	//Arguments
	public static final String ARG_SET = "set";
	public static final String ARG_REMOVEALL = "removeall";
	public static final String ARG_LIST = "list";
	
	public CmdSubRank() {
		super(CMD_LABEL);
		this.getSubCommands().add(new SubCommand(this, ARG_SET, "Définit les sous-rangs d'un joueur.", "/" + CMD_LABEL + " " + ARG_SET + " <joueur>", 1));
		this.getSubCommands().add(new SubCommand(this, ARG_REMOVEALL, "Retire tous les sous-rangs d'un joueur.", "/" + CMD_LABEL + " " + ARG_REMOVEALL + " <joueur>", 2));
		this.getSubCommands().add(new SubCommand(this, ARG_LIST, "Affiche la liste des sous-rangs d'un joueur.", "/" + CMD_LABEL + " " + ARG_LIST + " <joueur>", 3));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		this.treatCommand(sender, args);
		return true;
	}
	
	@Override
	public void executeCommand(String[] args) throws Exception {
		OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
		String arg = args[0];
		
		if(player != null) {
			if(arg.equalsIgnoreCase(ARG_SET)) {
				SubRankManager.openSetSubRankInventory(this.getSender(), player);
			} else if(arg.equalsIgnoreCase(ARG_REMOVEALL)) {
				SubRankManager.removeAllSubRank(this.getSender(), player);
			} else {
				SubRankManager.sendListSubRankMessage(this.getSender(), player);
			}			
		} else {
			Utils.sendErrorMessage(this.getSender(), ErrorMessage.UNKNOW_PLAYER);
		}
	}
}