package fr.voltariuss.dornacraft.playermanager.features.rank;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.cmds.ComplexCommand;
import fr.voltariuss.dornacraft.api.cmds.CustomCommand;
import fr.voltariuss.dornacraft.api.cmds.SubCommand;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

public final class CmdRank extends CustomCommand implements ComplexCommand {
	
	public static final String CMD_LABEL = "rank";
	
	public static final String ARG_SET = "set";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_PROMOTE = "promote";
	public static final String ARG_DEMOTE = "demote";
	public static final String ARG_INFO = "info";
	
	public CmdRank() {
		super(DornacraftPlayerManager.class, CMD_LABEL);
		super.addSubCommand(new SubCommand(this, ARG_SET, "Définit le rang d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_REMOVE, "Retire le rang d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_PROMOTE, "Promouvois un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_DEMOTE, "Rétrograde un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_INFO, "Affiche le rang d'un joueur.", "<joueur>"));
	}

	@Override
	public void executeSubCommand(CommandSender sender, String[] args) throws Exception {
		OfflinePlayer target = AccountManager.getOfflinePlayer(args[1]);
				
		if(target != null) {
			if(args[0].equalsIgnoreCase(ARG_SET)) {
				if(sender instanceof Player) {
					InventoryRank.openInventory((Player) sender, target);
				} else {
					Utils.sendErrorMessage(sender, ErrorMessage.PLAYER_ONLINE_ONLY);
				}
			} else if(args[0].equalsIgnoreCase(ARG_REMOVE)) {
				RankManager.removeRank(sender, target);
			} else if(args[0].equalsIgnoreCase(ARG_PROMOTE)) {
				RankManager.promote(sender, target);
			} else if(args[0].equalsIgnoreCase(ARG_DEMOTE)) {
				RankManager.demote(sender, target);
			} else {
				RankManager.sendRankInfoMessage(sender, target);
			}
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.PLAYER_UNKNOW);
		}
	}

	@Override
	public void executeMainCommand(CommandSender sender, String[] args) throws Exception {
		super.sendHelpCommandMessage();
	}
}
