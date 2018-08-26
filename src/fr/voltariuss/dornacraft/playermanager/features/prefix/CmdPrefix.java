package fr.voltariuss.dornacraft.playermanager.features.prefix;

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

public class CmdPrefix extends CustomCommand implements CommandExecutor, ComplexCommand {
	
	public static final String CMD_LABEL = "prefixmanager";
	
	//Arguments
	public static final String ARG_SET = "set";

	public CmdPrefix() {
		super(DornacraftPlayerManager.class);
		super.addSubCommand(new SubCommand(this, ARG_SET, "Modifie le préfixe d'un joueur.", "<joueur>"));
	}

	@Override
	public void executeSubCommand(CommandSender sender, String[] args) throws Exception {
		OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
		
		if(player != null) {
			PrefixManager.openSetPrefixInventory(sender, player);
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.UNKNOW_PLAYER);
		}
	}

	@Override
	public void executeMainCommand(CommandSender sender, String[] args) throws Exception {
		super.sendHelpCommandMessage();
	}
}
