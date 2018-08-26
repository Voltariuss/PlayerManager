package fr.voltariuss.dornacraft.playermanager.features.subrank;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraft.api.cmds.ComplexCommand;
import fr.voltariuss.dornacraft.api.cmds.CustomCommand;
import fr.voltariuss.dornacraft.api.cmds.SubCommand;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

public class CmdSubRank extends CustomCommand implements CommandExecutor, ComplexCommand {
	
	public static final String CMD_LABEL = "subrank";
	
	//Arguments
	public static final String ARG_SET = "set";
	public static final String ARG_CLEAR = "clear";
	public static final String ARG_LIST = "list";
	
	public CmdSubRank() {
		super(DornacraftPlayerManager.class);
		super.addSubCommand(new SubCommand(this, ARG_SET, "Définit les sous-rangs d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_CLEAR, "Retire tous les sous-rangs d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_LIST, "Affiche la liste des sous-rangs d'un joueur.", "<joueur>"));
	}
	
	@Override
	public void executeSubCommand(CommandSender sender, String[] args) throws Exception {
		OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
		String arg = args[0];
		
		if(player != null) {
			if(arg.equalsIgnoreCase(ARG_SET)) {
				SubRankManager.openSetSubRankInventory(sender, player);
			} else if(arg.equalsIgnoreCase(ARG_CLEAR)) {
				SubRankManager.removeAllSubRank(sender, player);
			} else {
				SubRankManager.sendListSubRankMessage(sender, player);
			}			
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.UNKNOW_PLAYER);
		}
	}

	@Override
	public void executeMainCommand(CommandSender sender, String[] args) throws Exception {
		super.sendHelpCommandMessage();
	}
}