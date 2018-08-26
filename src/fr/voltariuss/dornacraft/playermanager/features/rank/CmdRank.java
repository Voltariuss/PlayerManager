package fr.voltariuss.dornacraft.playermanager.features.rank;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraft.api.cmds.ComplexCommand;
import fr.voltariuss.dornacraft.api.cmds.CustomCommand;
import fr.voltariuss.dornacraft.api.cmds.SubCommand;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

public class CmdRank extends CustomCommand implements ComplexCommand {
	
	public static final String CMD_LABEL = "rank";
	
	//Arguments
	public static final String ARG_SET = "set";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_PROMOTE = "promote";
	public static final String ARG_DEMOTE = "demote";
	public static final String ARG_INFO = "info";
	
	public CmdRank() {
		super(DornacraftPlayerManager.class);
		super.addSubCommand(new SubCommand(this, ARG_SET, "Définit le rang d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_REMOVE, "Retire le rang d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_PROMOTE, "Promouvois un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_DEMOTE, "Rétrograde un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_INFO, "Affiche le rang d'un joueur.", "<joueur>"));
	}

	@Override
	public void executeSubCommand(CommandSender sender, String[] args) throws Exception {
		OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
		String arg = args[0];
		
		if(player != null) {
			if(arg.equalsIgnoreCase(ARG_SET)) {
				RankManager.openSetRankInventory(sender, player);
			} else if(arg.equalsIgnoreCase(ARG_REMOVE)) {
				RankManager.removeRank(sender, player);
			} else if(arg.equalsIgnoreCase(ARG_PROMOTE)) {
				RankManager.promote(sender, player);
			} else if(arg.equalsIgnoreCase(ARG_DEMOTE)) {
				RankManager.demote(sender, player);
			} else {
				RankManager.sendRankInfoMessage(sender, player.getName(), RankManager.getRank(player));
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
