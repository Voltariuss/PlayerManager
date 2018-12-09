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

public final class CmdLevelManager extends CustomCommand implements ComplexCommand {

	public static final String CMD_LABEL = "levelmanager";

	public static final String ARG_ADD = "add";
	public static final String ARG_ADDXP = "addxp";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_REMOVEXP = "removexp";
	public static final String ARG_SET = "set";
	public static final String ARG_RESET = "reset";
	public static final String ARG_INFO = "info";

	public CmdLevelManager() {
		super(DornacraftPlayerManager.class, CMD_LABEL);
		super.addSubCommand(new SubCommand(this, ARG_ADD, "Ajoute des niveaux à un joueur.", "<joueur> <nombre>"));
		super.addSubCommand(new SubCommand(this, ARG_ADDXP, "Ajoute de l'xp à un joueur.", "<joueur> <nombre>"));
		super.addSubCommand(new SubCommand(this, ARG_REMOVE, "Retire des niveaux à un joueur.", "<joueur> <nombre>"));
		super.addSubCommand(new SubCommand(this, ARG_REMOVEXP, "Retire de l'xp à un joueur.", "<joueur> <nombre>"));
		super.addSubCommand(new SubCommand(this, ARG_SET, "Définit le niveau d'un joueur.", "<joueur> <nombre>"));
		super.addSubCommand(new SubCommand(this, ARG_RESET, "Réinitialise le niveau d'un joueur.", "<joueur>"));
		super.addSubCommand(new SubCommand(this, ARG_INFO, "Affiche le niveau d'un joueur.", "<joueur>"));
	}

	@Override
	public void executeSubCommand(CommandSender sender, String[] args) throws Exception {
		OfflinePlayer target = AccountManager.getOfflinePlayer(args[1]);

		if (target != null) {
			if (args[0].equalsIgnoreCase(ARG_ADD)) {
				LevelManager.addLevel(sender, target, Integer.parseInt(args[2]));
			} else if (args[0].equalsIgnoreCase(ARG_ADDXP)) {
				LevelManager.addXp(sender, target, Integer.parseInt(args[2]));
			} else if (args[0].equalsIgnoreCase(ARG_REMOVE)) {
				LevelManager.removeLevel(sender, target, Integer.parseInt(args[2]));
			} else if (args[0].equalsIgnoreCase(ARG_REMOVEXP)) {
				LevelManager.removeXp(sender, target, Integer.parseInt(args[2]));
			} else if (args[0].equalsIgnoreCase(ARG_SET)) {
				LevelManager.setLevel(sender, target, Integer.parseInt(args[2]));
			} else if (args[0].equalsIgnoreCase(ARG_RESET)) {
				LevelManager.resetLevel(sender, target);
			} else {
				LevelManager.sendInfo(sender, target);
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
