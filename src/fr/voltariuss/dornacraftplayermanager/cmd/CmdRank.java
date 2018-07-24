package fr.voltariuss.dornacraftplayermanager.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftapi.inventories.InteractiveInventory;
import fr.voltariuss.dornacraftapi.inventories.InventoryItem;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftapi.items.ItemUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.Rank;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;

public class CmdRank extends CustomCommand implements CommandExecutor {
	
	private static SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	
	//Messages d'erreur
	public static final String HAS_HIGHEST_RANK = "§cLe joueur possède dèjà le rang le plus élevé.";
	public static final String HAS_LOWER_RANK = "§cLe joueur possède déjà le rang le plus bas.";
	public static final String ALREADY_HAS_RANK = "§cLe joueur possède déjà ce rang.";
	
	//Autres messages
	public static final String CHANGE_RANK_SUCCESS = "§aLe rang du joueur §b% §aa été modifié avec succès !";
	public static final String RANK_INFO = "§6Rang du joueur §b% §6: ";
	
	//Arguments
	public static final String ARG_SET = "set";
	public static final String ARG_REMOVE = "remove";
	public static final String ARG_PROMOTE = "promote";
	public static final String ARG_DEMOTE = "demote";
	public static final String ARG_INFO = "info";
	
	//Menus
	public static final String INFO_CHANGE_RANK = "§e§lClique pour attribuer ce rang";
	public static final String WARNING_ALREADY_HAS_RANK = "§c§lRang possédé par le joueur";
	
	List<String> loresInfo = Arrays.asList("", INFO_CHANGE_RANK);
	List<String> loresWarning = Arrays.asList("", WARNING_ALREADY_HAS_RANK);

	/**
	 * Constructor of the command /rank.
	 * 
	 * @param sender The command sender.
	 * @param cmdLabel The label of the command.
	 * @param plugin The plugin who generate this command.
	 */
	public CmdRank(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
		this.getSubCommands().add(new SubCommand(ARG_SET, "Pour définir le rang d'un joueur :\n §6/rank set §b<joueur>", 1));
		this.getSubCommands().add(new SubCommand(ARG_REMOVE, "Pour retirer le rang d'un joueur :\n §6/rank remove §b<joueur>", 2));
		this.getSubCommands().add(new SubCommand(ARG_PROMOTE, "Pour promouvoir un joueur :\n §6/rank promote §b<joueur>", 3));
		this.getSubCommands().add(new SubCommand(ARG_DEMOTE, "Pour rétrograder un joueur :\n §6/rank demote §b<joueur>", 4));
		this.getSubCommands().add(new SubCommand(ARG_INFO, "Pour voir le rang d'un joueur :\n §6/rank info §b<joueur>", 5));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		this.setSender(sender);
		
		if(sender.hasPermission(this.getPrimaryPermission())) {
			try {
				if(args.length == 0) {
					this.sendDescriptionCommandMessage();
				} else if(args.length == 1) {
					for(int i = 0; i < this.getSubCommands().size(); i++) {
						SubCommand subCommand = this.getSubCommands().get(i);
						if(args[0].equalsIgnoreCase(subCommand.getArg())) {
							if(sender.hasPermission(subCommand.getPermission())) {
								this.sendMessage(subCommand.getHelpMessage());
							} else {
								this.sendLakePermissionMessage();
							}
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendHelpMessage();
					} else {
						this.sendWrongCommandMessage();
					}
				} else if(args.length == 2) {
					UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					
					if(player != null) {
						if(args[0].equalsIgnoreCase(ARG_SET)) {
							if(sender.hasPermission(this.getSubCommand(ARG_SET).getPermission())) {
								this.openSetRankInventory(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase(ARG_REMOVE)) {
							if(sender.hasPermission(this.getSubCommand(ARG_REMOVE).getPermission())) {
								this.removeRank(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase(ARG_PROMOTE)) {
							if(sender.hasPermission(this.getSubCommand(ARG_PROMOTE).getPermission())) {
								this.promote(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase(ARG_DEMOTE)) {
							if(sender.hasPermission(this.getSubCommand(ARG_DEMOTE).getPermission())) {
								this.demote(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase(ARG_INFO)) {
							if(sender.hasPermission(this.getSubCommand(ARG_INFO).getPermission())) {
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
					for(int i = 0; i < this.getSubCommands().size(); i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands().get(i).getArg())) {
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
	 * Try to change the rank of the target player with the rank specified
	 * 
	 * @param player
	 * @param rank
	 * @throws Exception
	 */
	public void setRank(OfflinePlayer player, Rank rank) throws Exception {
		Rank playerRank = sqlAccount.getRank(player);
		
		if(playerRank != rank) {
			changeRank(player, rank);
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
			changeRank(player, Rank.JOUEUR);
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
			changeRank(player, Rank.fromPower(playerRank.getPower() + 1));		
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
			changeRank(player, Rank.fromPower(playerRank.getPower() - 1));
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
	
	public void openSetRankInventory(OfflinePlayer player) throws Exception {
		if(this.getSender() instanceof Player) {
			Player p = (Player) this.getSender();
			InteractiveInventory inventory = new InteractiveInventory(this.getInventoryItemMap(player), 9, player.getName());
			inventory.openInventory(p);
		} else {
			this.sendErrorMessage(Utils.getMustBeAPlayerMessage());
		}
	}
	
	public HashMap<Integer, InventoryItem> getInventoryItemMap(OfflinePlayer player) throws Exception {
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		Rank rank = sqlAccount.getRank(player);
		String name = "§cRang: ";
		Material type = Material.STAINED_CLAY;
		int amount = 1;
		
		InventoryItemInteractListener setRank = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					UUID uuid = sqlAccount.getUUIDOfPlayer(interactiveInventory.getInventory().getName());
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(Rank rank : Rank.values()) {
						if(title.contains(rank.getRankNameColor())) {
							setRank(player, rank);
						}
					}
					event.getPlayer().closeInventory();
					openSetRankInventory(player);
				} catch (Exception e) {
					event.getPlayer().sendMessage(Utils.getExceptionMessage());
					e.printStackTrace();
				}
			}
		};
		
		InventoryItemInteractListener alreadyHasRank = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				event.getPlayer().sendMessage(CmdRank.ALREADY_HAS_RANK);
			}
		};
		
		ArrayList<InventoryItem> items = new ArrayList<>();
		items.add(new InventoryItem(ItemUtils.generateItem(type, amount, (short) 9, name + Rank.JOUEUR.getRankNameColor(), loresInfo)));
		items.add(new InventoryItem(ItemUtils.generateItem(type, amount, (short) 11, name + Rank.GUIDE.getRankNameColor(), loresInfo)));
		items.add(new InventoryItem(ItemUtils.generateItem(type, amount, (short) 1, name + Rank.MODERATEUR.getRankNameColor(), loresInfo)));
		items.add(new InventoryItem(ItemUtils.generateItem(type, amount, (short) 14, name + Rank.ADMINISTRATEUR.getRankNameColor(), loresInfo)));
		
		for(int i = 0; i < items.size(); i++) {
			InventoryItem item = items.get(i);
			ItemMeta meta = item.getItemMeta();
			
			if(meta.getDisplayName().contains(rank.getRankNameColor())) {
				meta.setLore(loresWarning);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				item.addInventoryItemListener(alreadyHasRank);
			} else {
				item.addInventoryItemListener(setRank);
			}
			inventoryItemMap.put(i, item);
		}		
		inventoryItemMap.put(8, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
}
