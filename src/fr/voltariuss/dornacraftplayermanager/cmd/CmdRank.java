package fr.voltariuss.dornacraftplayermanager.cmd;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.Rank;
import fr.voltariuss.dornacraftplayermanager.inventories.SetRankInventory;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;

public class CmdRank extends CustomCommand implements CommandExecutor {
	
	private static SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	
	//Messages d'erreur
	public static final String CHANGE_RANK_DENIED = "§cImpossible de modifier le rang d'un joueur ayant un rang supérieur au votre.";
	public static final String HAS_HIGHEST_RANK = "§cLe joueur possède dèjà le rang le plus élevé.";
	public static final String HAS_LOWER_RANK = "§cLe joueur possède déjà le rang le plus bas.";
	public static final String UNKNOW_RANK = "§cLe rang spécifié est incorrect.";
	public static final String ALREADY_HAS_RANK = "§cLe joueur possède déjà ce rang.";
	
	//Autres messages
	public static final String CHANGE_RANK_SUCCESS = "§aLe rang du joueur §b% §aa été modifié avec succès !";
	public static final String RANK_INFO = "§6Rang du joueur §b% §6: ";
	
	//Arguments
	public static final String ARG_RANK_SET = "set";
	public static final String ARG_RANK_REMOVE = "remove";
	public static final String ARG_RANK_PROMOTE = "promote";
	public static final String ARG_RANK_DEMOTE = "demote";
	public static final String ARG_RANK_INFO = "info";
	
	//Messages d'aide sur les commandes
	public static final String MSG_RANK_SET = "§ePour définir le rang d'un joueur:\n §6/rank set §b<joueur>";
	public static final String MSG_RANK_REMOVE = "§ePour retirer le rang d'un joueur:\n §6/rank remove §b<joueur>";
	public static final String MSG_RANK_PROMOTE = "§ePour promouvoir un joueur:\n §6/rank promote §b<joueur>";
	public static final String MSG_RANK_DEMOTE = "§ePour rétrograder un joueur:\n §6/rank demote §b<joueur>";
	public static final String MSG_RANK_INFO = "§ePour voir le rang d'un joueur:\n §6/rank info §b<joueur>";
	
	//Permissions
	public static final String PERM_RANK_GLOBAL = "dornacraft.rank";
	public static final String PERM_RANK_SET = PERM_RANK_GLOBAL + "." + ARG_RANK_SET;
	public static final String PERM_RANK_REMOVE = PERM_RANK_GLOBAL + "." + ARG_RANK_REMOVE;
	public static final String PERM_RANK_PROMOTE = PERM_RANK_GLOBAL + "." + ARG_RANK_PROMOTE;
	public static final String PERM_RANK_DEMOTE = PERM_RANK_GLOBAL + "." + ARG_RANK_DEMOTE;
	public static final String PERM_RANK_INFO = PERM_RANK_GLOBAL + "." + ARG_RANK_INFO;
	public static final String PERM_RANK_ADMIN = PERM_RANK_GLOBAL + ".*";
	
	//Menus
	public static final String INFO_CHANGE_RANK = "§e§lCliquez pour attribuer ce rang au joueur.";
	
	public static final List<String> loreChangeRank = Arrays.asList("", INFO_CHANGE_RANK);
	
	//Tableaux	
	private final String[] HELP_MESSAGES = {MSG_RANK_SET,MSG_RANK_REMOVE,MSG_RANK_PROMOTE,MSG_RANK_DEMOTE,MSG_RANK_INFO};
	private final String[] SUB_COMMANDS = {ARG_RANK_SET,ARG_RANK_REMOVE,ARG_RANK_PROMOTE,ARG_RANK_DEMOTE,ARG_RANK_INFO};
	private final String[] PERMISSIONS = {PERM_RANK_SET,PERM_RANK_REMOVE,PERM_RANK_PROMOTE,PERM_RANK_DEMOTE,PERM_RANK_INFO};

	/**
	 * Constructor of the command /rank.
	 * 
	 * @param sender The command sender.
	 * @param cmdLabel The label of the command.
	 * @param plugin The plugin who generate this command.
	 */
	public CmdRank(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		super.setCommandSender(sender);
		
		if(sender.hasPermission(PERM_RANK_GLOBAL)) {
			try {
				if(args.length == 0) {
					this.sendDescriptionCommandMessage();
				} else if(args.length == 1) {
					for(int i = 0; i < this.getSubCommands().length; i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands()[i])) {
							if(sender.hasPermission(this.getPermissions()[i])) {
								sender.sendMessage(this.getHelpMessages()[i]);
							} else {
								this.sendLakePermissionMessage();
							}
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendHelpMessage(this.getHelpMessages());
					} else {
						this.sendWrongCommandMessage();
					}
				} else if(args.length == 2) {
					UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					
					if(player != null) {
						if(args[0].equalsIgnoreCase("set")) {
							if(sender.hasPermission(PERM_RANK_SET)) {
								this.openSetRankInventory(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							if(sender.hasPermission(PERM_RANK_REMOVE)) {
								this.removeRank(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("promote")) {
							if(sender.hasPermission(PERM_RANK_PROMOTE)) {
								this.promote(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("demote")) {
							if(sender.hasPermission(PERM_RANK_DEMOTE)) {
								this.demote(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("info")) {
							if(sender.hasPermission(PERM_RANK_INFO)) {
								this.info(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("help")) {
							this.sendTooManyArgumentsMessage();
						} else {
							this.sendWrongCommandMessage();
						}
					} else {
						this.sendUnknowPlayerMessage();
					}
				} else {
					for(int i = 0; i < this.getSubCommands().length; i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands()[i])) {
							this.sendTooManyArgumentsMessage();
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendTooManyArgumentsMessage();
					} else {
						this.sendWrongCommandMessage();
					}
				}	
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(Utils.getExceptionMessage());
			}	
		} else {
			this.sendLakePermissionMessage();
		}
		return true;
	}
	
	/**
	 * @return The array of help messages of the command sorted in descending order of the number of arguments.
	 */
	@Override
	public String[] getHelpMessages() {
		return HELP_MESSAGES;
	}

	/**
	 * @return The array of permissions of the command sorted in descending order of the number of arguments.
	 */
	@Override
	public String[] getPermissions() {
		return PERMISSIONS;
	}

	/**
	 * @return The array of permissions of the command sorted in descending order of the number of arguments.
	 */
	@Override
	public String[] getSubCommands() {
		return SUB_COMMANDS;
	}
		
	/**
	 * Change the rank of the target player and send the corresponding message to the sender
	 * with informations about his new rank.
	 * 
	 * @param player The target player.
	 * @param rank The new rank of the player.
	 * @throws Exception
	 */
	public void changeRank(OfflinePlayer player, Rank rank) throws Exception {
		sqlAccount.setRank(player, rank);
		sendChangeRankSuccessMessage(player.getName());
		info(player);
	}
	
	/**
	 * Try to change the rank of the target player
	 * 
	 * @param player
	 * @throws Exception
	 */
	public void openSetRankInventory(OfflinePlayer player) throws Exception {
		SetRankInventory setRankInventory = new SetRankInventory(player, sqlAccount.getRank(player), this);
		Player p = Bukkit.getPlayer(getCommandSender().getName());
		setRankInventory.openInventory(p);
	}
	
	/**
	 * Try to change the rank of the target player with the rank specified
	 * 
	 * @param player
	 * @param rank
	 * @throws Exception
	 */
	public void setRank(OfflinePlayer player, Rank rank) throws Exception {
		Rank playerRank = sqlAccount.getRank(player);
		
		if(playerRank != rank) {
			if(getCommandSender() instanceof Player && !getCommandSender().hasPermission(PERM_RANK_ADMIN)) {
				Rank senderRank = sqlAccount.getRank(Bukkit.getPlayer(getCommandSender().getName()));
				
				if(playerRank.getPower() < senderRank.getPower()) {
					changeRank(player, rank);
				} else {
					sendChangeRankDeniedMessage();
				}
			} else {
				changeRank(player, rank);
			}
		} else {
			sendAlreadyHasRankMessage();
		}
	}
	
	/**
	 * Try to set the default rank to the target player.
	 * If the player has already the default rank, cancel the attempt and send the corresponding message.
	 * Similarly, if the sender has less power than the target <b>before</b> the attempt, do the same thing.
	 * 
	 * @param player The target player.
	 * @throws Exception
	 */
	public void removeRank(OfflinePlayer player) throws Exception {
		Rank playerRank = sqlAccount.getRank(player);
		
		if(playerRank != Rank.JOUEUR) {
			if(getCommandSender() instanceof Player && !getCommandSender().hasPermission(PERM_RANK_ADMIN)) {
				Rank senderRank = sqlAccount.getRank(Bukkit.getPlayer(getCommandSender().getName()));
				
				if((playerRank.getPower() < senderRank.getPower())) {
					changeRank(player, Rank.JOUEUR);
				} else {
					sendChangeRankDeniedMessage();
				}
			} else {
				changeRank(player, Rank.JOUEUR);
			}
		} else {
			sendHasLowerRankMessage();
		}
	}
	
	/**
	 * Try to promote the target player.
	 * If the player has the highest rank, cancel the attempt and send the corresponding message.
	 * Similarly, if the sender has less or same power than the target <b>after</b> the attempt, do the same thing.
	 * 
	 * @param player The target player.
	 * @throws Exception
	 */
	public void promote(OfflinePlayer player) throws Exception {
		Rank playerRank = sqlAccount.getRank(player);
		
		if(playerRank != Rank.ADMINISTRATEUR) {
			if(getCommandSender() instanceof Player && !getCommandSender().hasPermission(PERM_RANK_ADMIN)) {
				Rank senderRank = sqlAccount.getRank(Bukkit.getPlayer(getCommandSender().getName()));
				
				if((playerRank.getPower() + 1 < senderRank.getPower())) {
					changeRank(player, Rank.fromPower(playerRank.getPower() + 1));
				} else {
					sendChangeRankDeniedMessage();
				}
			} else {
				changeRank(player, Rank.fromPower(playerRank.getPower() + 1));
			}			
		} else {
			sendHasHighestRankMessage();
		}
	}
	
	/**
	 * Try to demote the target player.
	 * If the player has the lower rank, cancel the attempt and send the corresponding message.
	 * Similarly, if the sender has less power than the target <b>before</b> the attempt, do the same thing.
	 * 
	 * @param player The target player.
	 * @throws Exception
	 */
	public void demote(OfflinePlayer player) throws Exception {
		Rank playerRank = sqlAccount.getRank(player);
		
		if(playerRank != Rank.JOUEUR) {
			if(getCommandSender() instanceof Player && !getCommandSender().hasPermission(PERM_RANK_ADMIN)) {
				Rank senderRank = sqlAccount.getRank(Bukkit.getPlayer(getCommandSender().getName()));
				
				if(playerRank.getPower() < senderRank.getPower()) {
					changeRank(player, Rank.fromPower(playerRank.getPower() - 1));
				} else {
					sendChangeRankDeniedMessage();
				}
			} else {
				changeRank(player, Rank.fromPower(playerRank.getPower() - 1));
			}
		} else {
			sendHasLowerRankMessage();;
		}
	}
	
	/**
	 * Get informations about the rank of the target player and send the corresponding message.
	 * 
	 * @param player The target player.
	 * @throws Exception
	 */
	public void info(OfflinePlayer player) throws Exception {
		Rank playerRank = sqlAccount.getRank(player);
		sendRankInfoMessage(player, playerRank);
	}
	
	/**
	 * Send a message to the sender to notice him that it's not possible to change the rank of the player.
	 */
	public void sendChangeRankDeniedMessage() {
		sendErrorMessage(CHANGE_RANK_DENIED);
	}
	
	/**
	 * Send a message to the sender to notice him that the player has already the highest rank.
	 */
	public void sendHasHighestRankMessage() {
		sendErrorMessage(HAS_HIGHEST_RANK);
	}
	
	/**
	 * Send a message to the sender to notice him that the player has already the lower rank.
	 */
	public void sendHasLowerRankMessage() {
		sendErrorMessage(HAS_LOWER_RANK);
	}
	
	/**
	 * Send a message to the sender to notice him that the rank specified is wrong.
	 */
	public void sendUnknownRankMessage() {
		sendErrorMessage(UNKNOW_RANK);
	}
	
	/**
	 * Send a message to the sender to notice him that the player has already the rank specified.
	 */
	public void sendAlreadyHasRankMessage() {
		sendErrorMessage(ALREADY_HAS_RANK);
	}
	
	/**
	 * Send a message to the sender to notice him that the rank has been changed successfully.
	 * 
	 * @param playerName The name of the player whose rank has been changed.
	 */
	public void sendChangeRankSuccessMessage(String playerName) {
		sendMessage(CHANGE_RANK_SUCCESS.replace("%", playerName));
	}
	
	/**
	 * Send a message to the sender with informations about the rank of the target player.
	 * 
	 * @param player The target player.
	 * @param rank The rank of the player.
	 */
	public void sendRankInfoMessage(OfflinePlayer player, Rank rank) {
		sendMessage(RANK_INFO.replace("%", player.getName()) + rank.getRankNameColor());
	}
	
	public Inventory getInventorySetRang(OfflinePlayer player) {
		Inventory inventory = Bukkit.createInventory(null, 27, player.getName());
		inventory.setItem(11, getJoueurItem());
		inventory.setItem(12, getGuideItem());
		inventory.setItem(14, getModerateurItem());
		inventory.setItem(15, getAdministrateurItem());
		return inventory;
	}
	
	public ItemStack getJoueurItem() {
		ItemStack itemStack = InventoryUtils.getAsDecorationItem(new ItemStack(Material.STAINED_CLAY, 1));
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§cRang: §eJoueur");
		itemMeta.setLore(loreChangeRank);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	public ItemStack getGuideItem() {
		ItemStack itemStack = new ItemStack(Material.STAINED_CLAY, 1, (byte)11);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§cRang: §9Guide");
		itemMeta.setLore(loreChangeRank);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	public ItemStack getModerateurItem() {
		ItemStack itemStack = new ItemStack(Material.STAINED_CLAY, 1, (byte)1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§cRang: §6Modérateur");
		itemMeta.setLore(loreChangeRank);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	public ItemStack getAdministrateurItem() {
		ItemStack itemStack = new ItemStack(Material.STAINED_CLAY, 1, (byte)14);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§cRang: §4Administrateur");
		itemMeta.setLore(loreChangeRank);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
}
