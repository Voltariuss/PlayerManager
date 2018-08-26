package fr.voltariuss.dornacraft.playermanager.features.level;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraft.api.cmds.ComplexCommand;
import fr.voltariuss.dornacraft.api.cmds.CustomCommand;
import fr.voltariuss.dornacraft.api.cmds.SubCommand;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.DornacraftPlayerManager;

public class CmdLevel extends CustomCommand implements ComplexCommand {
	
	public static final String CMD_LABEL = "levelmanager";
	
	//Arguments
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_SET = "set";
	public static final String ARG_RESET = "reset";
	public static final String ARG_INFO = "info";

	public CmdLevel() {
		super(DornacraftPlayerManager.class);
		super.addSubCommand(new SubCommand(this, ARG_ADD, "Ajoute des niveaux à un joueur.", "<joueur> <nombre>"));
		super.addSubCommand(new SubCommand(this, ARG_REMOVE, "Retire des niveaux à un joueur.", "<joueur> <nombre>"));
		super.addSubCommand(new SubCommand(this, ARG_SET, "Définit le niveau d'un joueur.", "<joueur> <nombre>"));
		super.addSubCommand(new SubCommand(this, ARG_RESET, "Réinitialise le niveau d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_INFO, "Affiche le niveau d'un joueur.", "<joueur>"));
	}

	@Override
	public void executeSubCommand(CommandSender sender, String[] args) throws Exception {
		OfflinePlayer player = AccountManager.getOfflinePlayer(args[1]);
		String arg = args[0];
		
		if(player != null) {
			if(arg.equalsIgnoreCase(ARG_ADD)) {
				LevelManager.addLevel(sender, player, Integer.parseInt(args[2]));
			} else if(arg.equalsIgnoreCase(ARG_REMOVE)) {
				LevelManager.removeLevel(sender, player, Integer.parseInt(args[2]));
			} else if(arg.equalsIgnoreCase(ARG_SET)) {
				LevelManager.setLevel(sender, player, Integer.parseInt(args[2]));
			} else if(arg.equalsIgnoreCase(ARG_RESET)) {
				LevelManager.resetLevel(sender, player);
			} else {
				LevelManager.sendInfoLevel(sender, player);
			}
		} else {
			Utils.sendErrorMessage(this.getSender(), ErrorMessage.UNKNOW_PLAYER);
		}
	}

	@Override
	public void executeMainCommand(CommandSender sender, String[] args) throws Exception {
		super.sendHelpCommandMessage();
	}
}
