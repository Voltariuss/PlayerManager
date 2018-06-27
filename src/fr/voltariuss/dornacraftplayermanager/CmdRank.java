package fr.voltariuss.dornacraftplayermanager;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.voltariuss.dornacraftapi.utils.CommandsUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;

public class CmdRank implements CommandExecutor {
	
	//Instances
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
	private SQLAccount sqlAccount = main.getSQLAccount();
	private CommandsUtils cmdUtils;
	
	//Messages d'aide sur les commandes
	private final String cmdSet = "§ePour définir le rang d'un joueur:\n §6/rank set §b<joueur> <rang>";
	private final String cmdRemove = "§ePour retirer le rang d'un joueur:\n §6/rank remove §b<joueur>";
	private final String cmdPromote = "§ePour promouvoir un joueur:\n §6/rank promote §b<joueur>";
	private final String cmdDemote = "§ePour rétrograder un joueur:\n §6/rank demote §b<joueur>";
	private final String cmdInfo = "§ePour voir le rang d'un joueur:\n §6/rank info §b<joueur>";
	
	//Message d'erreur
	private final String changeRankImpossible = Utils.getErrorPrefix() + "Impossible de modifier le rank de ce joueur.";
	
	//Permissions
	private final String permGlobal = "dornacraft.rank";
	private final String permSet = "dornacraft.rank.set";
	private final String permRemove = "dornacraft.rank.remove";
	private final String permPromote = "dornacraft.rank.promote";
	private final String permDemote = "dornacraft.rank.demote";
	private final String permInfo = "dornacraft.rank.info";
	
	//Messages d'erreur
	private final String unknowRank = Utils.getErrorPrefix() + "Le rang spécifié est incorrect.";
	private final String maxPromote = Utils.getErrorPrefix() + "Le joueur possède dèjà le rang le plus élevé.";
	private final String defaultRank = Utils.getErrorPrefix() + "Le joueur possède déjà le rang le plus bas.";
	
	//Tableaux
	private final String[] cmdList = {cmdSet,cmdRemove,cmdPromote,cmdDemote,cmdInfo};
	private final String[] subCmdList = {"set","remove","promote","demote","info"};
	private final String[] permList = {permSet,permRemove,permPromote,permDemote,permInfo};

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		cmdUtils = new CommandsUtils(sender, cmdLabel, cmdList);
		
		try {
			if(cmdLabel.equalsIgnoreCase("rank")) {
				if(cmdUtils.hasPermission(permGlobal)) {
					if(args.length == 0) {
						String pluginName = main.getPluginName();
						String authorName = "Voltariuss";
						String version = "1.0";
						String description = "Permet la gestion du rang des joueurs.";
						cmdUtils.sendDescriptionCommand(pluginName, authorName, version, description);
					} else if(args.length == 1) {
						for(int i = 0; i < subCmdList.length; i++) {
							if(args[0].equalsIgnoreCase(subCmdList[i])) {
								if(cmdUtils.hasPermission(permList[i])) {
									sender.sendMessage(cmdList[i]);
								}
								return true;
							}
						}
						
						if(args[0].equalsIgnoreCase("help")) {
							cmdUtils.sendHelpCommand();
						} else {
							cmdUtils.sendWrongCommand();						
						}
					} else if(args.length == 2) {
						if(args[0].equalsIgnoreCase("set")) {
							if(cmdUtils.hasPermission(permSet)) {
								sender.sendMessage(cmdSet);							
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(cmdUtils.hasPermission(permRemove)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									if(sqlAccount.getRank(target) != Rank.JOUEUR) {
										if(sqlAccount.getRank(target) == Rank.FONDATEUR && !sender.getName().equals("Voltariuss")) {
											sender.sendMessage(changeRankImpossible);
										} else if(sqlAccount.getRank(target) == Rank.CO_FONDATEUR && (!sender.getName().equals("Voltariuss") && !sender.getName().equals("Glynix"))) {
											sender.sendMessage(changeRankImpossible);
										} else {
											sqlAccount.setRank(target, Rank.JOUEUR);
											sender.sendMessage("§aLe rang du joueur §b" + target.getName() + " §aa bien été retiré.");
										}
									} else {
										sender.sendMessage(defaultRank);
									}
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}
						} else if(args[0].equalsIgnoreCase("promote")) {
							if(cmdUtils.hasPermission(permPromote)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
									
								if(target != null) {
									Rank currentRank = sqlAccount.getRank(target);
									int power = currentRank.getPower();
									
									if(power <= Rank.values().length) {
										Rank newRank = Rank.getRank(power + 1);
										
										if(newRank == Rank.FONDATEUR && !sender.getName().equals("Voltariuss")) {
											sender.sendMessage(changeRankImpossible);	
										} else if(newRank == Rank.CO_FONDATEUR && (!sender.getName().equals("Voltariuss") && !sender.getName().equals("Glynix"))) {
											sender.sendMessage(changeRankImpossible);
										} else {
											sqlAccount.setRank(target, newRank);
											sender.sendMessage("§aLe joueur §b" + target.getName() + " §aa été promu au rang suivant: " + newRank.getRankColor() + newRank.getRankName());
										}			
									} else {
										sender.sendMessage(maxPromote);
									}
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}
						} else if(args[0].equalsIgnoreCase("demote")) {
							if(cmdUtils.hasPermission(permDemote)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									Rank rank = sqlAccount.getRank(target);
									int power = rank.getPower() - 1;
									
									if(power >= 1) {
										if(rank == Rank.FONDATEUR && !sender.getName().equals("Voltariuss")) {
											sender.sendMessage(changeRankImpossible);	
										} else {
											if(rank == Rank.CO_FONDATEUR && (!sender.getName().equals("Voltariuss") && !sender.getName().equals("Glynix"))) {
												sender.sendMessage(changeRankImpossible);
											} else {
												sqlAccount.setRank(target, rank);
												sender.sendMessage("§aLe joueur §b" + target.getName() + " §aa été rétrogradé au rang suivant : " + rank.getRankColor() + rank.getRankName());																				
											}
										}
									} else {
										sender.sendMessage(defaultRank);
									}		
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}
						} else if(args[0].equalsIgnoreCase("info")) {
							if(cmdUtils.hasPermission(permInfo)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									Rank rank = Rank.getDefault();
									rank = sqlAccount.getRank(target);
									sender.sendMessage("§6Rang du joueur §b" + target.getName() + " §6: " + rank.getRankColor() + rank.getRankName());
								} else {
									cmdUtils.sendUnknowPlayer();
								}
							}
						} else if(args[0].equalsIgnoreCase("help")) {
							cmdUtils.sendTooManyArguments(cmdUtils.getHelpCommand());
						} else {
							cmdUtils.sendWrongCommand();
						}
					} else if(args.length == 3) {
						if(args[0].equalsIgnoreCase("set")) {
							if(cmdUtils.hasPermission(permSet)) {
								UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
								OfflinePlayer target = Utils.getOfflinePlayer(uuid);
								
								if(target != null) {
									boolean rankValide = false;
									Rank rank = Rank.JOUEUR;
									
									for(Rank r : Rank.values()) {
										if(args[2].equalsIgnoreCase(r.getRankName())) {
											rankValide = true;
											rank = r;
											break;
										}
									}
									
									if(rankValide) {
										if(rank == Rank.FONDATEUR && !sender.getName().equals("Voltariuss")) {
											sender.sendMessage(changeRankImpossible);	
										} else {
											if(rank == Rank.CO_FONDATEUR && (!sender.getName().equals("Voltariuss") && !sender.getName().equals("Glynix"))) {
												sender.sendMessage(changeRankImpossible);
											} else {
												sqlAccount.setRank(target, rank);
												sender.sendMessage("§aLe rang " + rank.getRankColor() + rank.getRankName() + " §aa bien été attribué au joueur §b" + target.getName() + "§a.");
											}
										}
									} else {
										sender.sendMessage(unknowRank);
									}
								} else {
									cmdUtils.sendUnknowPlayer();
								}							
							}
						} else {
							for(int i = 1; i < subCmdList.length; i++) {
								if(args[0].equalsIgnoreCase(subCmdList[i])) {
									if(cmdUtils.hasPermission(permList[i])) {
										cmdUtils.sendTooManyArguments(cmdList[i]);
									}
									return true;
								}
							}
							
							if(args[0].equalsIgnoreCase("help")) {
								cmdUtils.sendTooManyArguments(cmdUtils.getHelpCommand());
							} else {
								cmdUtils.sendWrongCommand();							
							}
						}
					} else {
						for(int i = 0; i < subCmdList.length; i++) {
							if(args[0].equalsIgnoreCase(subCmdList[i])) {
								if(cmdUtils.hasPermission(permList[i])) {
									cmdUtils.sendTooManyArguments(cmdList[i]);
								}
								return true;
							}
						}
						
						if(args[0].equalsIgnoreCase("help")) {
							cmdUtils.sendTooManyArguments(cmdUtils.getHelpCommand());
						} else {
							cmdUtils.sendWrongCommand();						
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			cmdUtils.sendExceptionMessage();
		}
		return true;
	}
}
