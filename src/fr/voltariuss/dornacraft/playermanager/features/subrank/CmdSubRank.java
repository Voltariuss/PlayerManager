package fr.voltariuss.dornacraft.playermanager.features.subrank;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraft.api.cmds.ComplexCommand;
import fr.voltariuss.dornacraft.api.cmds.CustomCommand;
import fr.voltariuss.dornacraft.api.cmds.SubCommand;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

public final class CmdSubRank extends CustomCommand implements ComplexCommand {
	
	public static final String CMD_LABEL = "subrank";
	
	public static final String ARG_SET = "set";
	public static final String ARG_CLEAR = "clear";
	public static final String ARG_LIST = "list";
	
	public CmdSubRank() {
		super(DornacraftPlayerManager.class, CMD_LABEL);
		super.addSubCommand(new SubCommand(this, ARG_SET, "Définit les sous-rangs d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_CLEAR, "Retire tous les sous-rangs d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_LIST, "Affiche la liste des sous-rangs d'un joueur.", "<joueur>"));
	}
	
	@Override
	public void executeSubCommand(CommandSender sender, String[] args) throws Exception {
		OfflinePlayer target = AccountManager.getOfflinePlayer(args[1]);
		
		if(target != null) {
			if(args[0].equalsIgnoreCase(ARG_SET)) {
				InventorySubRank.openInventory(sender, target);
			} else if(args[0].equalsIgnoreCase(ARG_CLEAR)) {
				SubRankManager.removeAllSubRank(sender, target);
			} else {
				SubRankManager.sendListSubRankMessage(sender, target);
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