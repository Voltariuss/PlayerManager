package fr.voltariuss.dornacraftplayermanager.features.level;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftapi.utils.ErrorMessage;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.AccountManager;

public class CmdLevel extends CustomCommand implements CommandExecutor {
	
	public static final String CMD_LABEL = "levelmanager";
	
	//Arguments
	public static final String ARG_ADD = "add";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_SET = "set";
	public static final String ARG_RESET = "reset";
	public static final String ARG_INFO = "info";

	public CmdLevel() {
		super(CMD_LABEL);
		this.getSubCommands().add(new SubCommand(this, ARG_ADD, "Ajoute des niveaux à un joueur.", "/" + CMD_LABEL + " " + ARG_ADD + " <joueur> <nombre>", 1));
		this.getSubCommands().add(new SubCommand(this, ARG_REMOVE, "Retire des niveaux à un joueur.", "/" + CMD_LABEL + " " + ARG_REMOVE + " <joueur> <nombre>", 2));
		this.getSubCommands().add(new SubCommand(this, ARG_SET, "Définit le niveau d'un joueur.", "/" + CMD_LABEL + " " + ARG_SET + " <joueur> <nombre>", 3));
		this.getSubCommands().add(new SubCommand(this, ARG_RESET, "Réinitialise le niveau d'un joueur.", "/" + CMD_LABEL + " " + ARG_RESET + " <joueur>", 4));
		this.getSubCommands().add(new SubCommand(this, ARG_INFO, "Affiche le niveau d'un joueur.", "/" + CMD_LABEL + " " + ARG_INFO + " <joueur>", 5));
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
			if(arg.equalsIgnoreCase(ARG_ADD)) {
				LevelManager.addLevel(this.getSender(), player, Integer.parseInt(args[2]));
			} else if(arg.equalsIgnoreCase(ARG_REMOVE)) {
				LevelManager.removeLevel(this.getSender(), player, Integer.parseInt(args[2]));
			} else if(arg.equalsIgnoreCase(ARG_SET)) {
				LevelManager.setLevel(this.getSender(), player, Integer.parseInt(args[2]));
			} else if(arg.equalsIgnoreCase(ARG_RESET)) {
				LevelManager.resetLevel(this.getSender(), player);
			} else {
				LevelManager.sendInfoLevel(this.getSender(), player);
			}
		} else {
			Utils.sendErrorMessage(this.getSender(), ErrorMessage.UNKNOW_PLAYER);
		}
	}
	
	
}
