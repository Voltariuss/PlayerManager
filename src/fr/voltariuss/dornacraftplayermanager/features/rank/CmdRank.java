package fr.voltariuss.dornacraftplayermanager.features.rank;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftapi.utils.ErrorMessage;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.AccountManager;

public class CmdRank extends CustomCommand implements CommandExecutor {
	
	public static final String CMD_LABEL = "rankmanager";
	
	//Arguments
	public static final String ARG_SET = "set";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_PROMOTE = "promote";
	public static final String ARG_DEMOTE = "demote";
	public static final String ARG_INFO = "info";

	/**
	 * Constructor of the command /rank.
	 * 
	 * @param sender The command sender.
	 * @param cmdLabel The label of the command.
	 * @param plugin The plugin who generate this command.
	 */
	public CmdRank() {
		super(CMD_LABEL);
		this.getSubCommands().add(new SubCommand(this, ARG_SET, "Définit le rang d'un joueur.", "/" + CMD_LABEL + " " + ARG_SET + " <joueur>", 1));
		this.getSubCommands().add(new SubCommand(this, ARG_REMOVE, "Retire le rang d'un joueur.", "/" + CMD_LABEL + " " + ARG_REMOVE + " <joueur>", 2));
		this.getSubCommands().add(new SubCommand(this, ARG_PROMOTE, "Promouvois un joueur.", "/" + CMD_LABEL + " " + ARG_PROMOTE + " <joueur>", 3));
		this.getSubCommands().add(new SubCommand(this, ARG_DEMOTE, "Rétrograde un joueur.", "/" + CMD_LABEL + " " + ARG_DEMOTE + " <joueur>", 4));
		this.getSubCommands().add(new SubCommand(this, ARG_INFO, "Affiche le rang d'un joueur.", "/" + CMD_LABEL + " " + ARG_INFO + " <joueur>", 5));
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
				RankManager.openSetRankInventory(this.getSender(), player);
			} else if(arg.equalsIgnoreCase(ARG_REMOVE)) {
				RankManager.removeRank(this.getSender(), player);
			} else if(arg.equalsIgnoreCase(ARG_PROMOTE)) {
				RankManager.promote(this.getSender(), player);
			} else if(arg.equalsIgnoreCase(ARG_DEMOTE)) {
				RankManager.demote(this.getSender(), player);
			} else {
				RankManager.sendRankInfoMessage(this.getSender(), player.getName(), RankManager.getRank(player));
			}
		} else {
			Utils.sendErrorMessage(this.getSender(), ErrorMessage.UNKNOW_PLAYER);
		}
	}
}
