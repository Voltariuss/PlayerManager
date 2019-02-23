package fr.voltariuss.dornacraft.playermanager.features.level;

import java.util.Arrays;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.cmds.CommandArgument;
import fr.voltariuss.dornacraft.api.cmds.CommandArgumentType;
import fr.voltariuss.dornacraft.api.cmds.CommandNode;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommand;
import fr.voltariuss.dornacraft.api.cmds.DornacraftCommandExecutor;
import fr.voltariuss.dornacraft.api.utils.MessageLevel;
import fr.voltariuss.dornacraft.api.utils.MessageUtils;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;

public class CmdLevel extends DornacraftCommand {
	
	public static final String CMD_LABEL = "level";
	
	public static final String DESC_CMD = "Consulte le niveau du joueur spécifié";
	
	public CmdLevel() {
		super(CMD_LABEL);
		DornacraftCommandExecutor dce = new DornacraftCommandExecutor() {
			
			@Override
			public void execute(CommandSender sender, Command cmd, String label, String[] args) throws Exception {
				boolean targetAvailable = false;
				OfflinePlayer target = null;
				
				if(args.length == 0) {
					if(sender instanceof Player) {
						target = (Player) sender;
						targetAvailable = true;
					} else {
						Utils.sendSystemMessage(MessageLevel.ERROR, sender, MessageUtils.CONSOLE_NOT_ALLOWED);
					}				
				} else {
					target = AccountManager.getOfflinePlayer(args[0]);
					
					if(target == null) {
						Utils.sendSystemMessage(MessageLevel.ERROR, sender, MessageUtils.PLAYER_UNKNOW);
					} else {
						targetAvailable = true;
					}
				}
				
				if(targetAvailable) {
					LevelManager.sendInfo(sender, (Player) sender);				
				}
			}
		};
		// /level
		getCmdTreeExecutor().getRoot().setExecutor(dce);
		// /level [joueur]
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getCustomArgType("joueur"), false), DESC_CMD, dce, null)
			));
	}
}
