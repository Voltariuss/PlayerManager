package fr.voltariuss.dornacraft.playermanager.features.rank;

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

public final class CmdRank extends DornacraftCommand {
	
	public static final String CMD_LABEL = "rank";
	
	public static final String ARG_SET = "set";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_PROMOTE = "promote";
	public static final String ARG_DEMOTE = "demote";
	public static final String ARG_INFO = "info";
	
	public static final String DESC_SET = "Définit le rang d'un joueur";
	public static final String DESC_REMOVE = "Retire le rang d'un joueur";
	public static final String DESC_PROMOTE = "Promouvois un joueur";
	public static final String DESC_DEMOTE = "Rétrograde un joueur";
	public static final String DESC_INFO = "Affiche le rang d'un joueur";
	
	public CmdRank() {
		super(CMD_LABEL);
		DornacraftCommandExecutor dce = new DornacraftCommandExecutor() {
			
			@Override
			public void execute(CommandSender sender, Command cmd, String[] args) throws Exception {
				OfflinePlayer target = AccountManager.getOfflinePlayer(args[1]);
				
				if(target != null) {
					if(args[0].equalsIgnoreCase(ARG_SET)) {
						if(sender instanceof Player) {
							InventoryRank.openInventory((Player) sender, target);
						} else {
							Utils.sendSystemMessage(MessageLevel.ERROR, sender, MessageUtils.CONSOLE_NOT_ALLOWED);
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
					Utils.sendSystemMessage(MessageLevel.ERROR, sender, MessageUtils.PLAYER_UNKNOW);
				}
			}
		};
		// /rank set <joueur>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_SET), DESC_SET),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_SET, dce, null)
			));
		// /rank remove <joueur>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_REMOVE), DESC_REMOVE),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_REMOVE, dce, null)
			));
		// /rank promote <joueur>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_PROMOTE), DESC_PROMOTE),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_PROMOTE, dce, null)
			));
		// /rank demote <joueur>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_DEMOTE), DESC_DEMOTE),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_DEMOTE, dce, null)
			));
		// /rank info <joueur>
		getCmdTreeExecutor().addCommand(Arrays.asList(
				new CommandNode(new CommandArgument(ARG_INFO), DESC_INFO),
				new CommandNode(new CommandArgument(CommandArgumentType.STRING.getType()), DESC_INFO, dce, null)
			));
	}
}
