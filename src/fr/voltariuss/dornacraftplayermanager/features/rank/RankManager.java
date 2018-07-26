package fr.voltariuss.dornacraftplayermanager.features.rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraftapi.FeatureManager;
import fr.voltariuss.dornacraftapi.cmds.CommandUtils;
import fr.voltariuss.dornacraftapi.inventories.InteractiveInventory;
import fr.voltariuss.dornacraftapi.inventories.InventoryItem;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftapi.items.ItemUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.SQLAccount;

public class RankManager extends FeatureManager {
	
	private SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	
	//Messages d'erreur
	public static final String HAS_HIGHEST_RANK = "§cLe joueur possède dèjà le rang le plus élevé.";
	public static final String HAS_LOWER_RANK = "§cLe joueur possède déjà le rang le plus bas.";
	public static final String ALREADY_HAS_RANK = "§cLe joueur possède déjà ce rang.";
	
	//Autres messages
	public static final String CHANGE_RANK_SUCCESS = "§aLe rang du joueur §b% §aa été modifié avec succès !";
	public static final String RANK_INFO = "§6Rang du joueur §b% §6: ";
	
	//Menus
	public static final String INFO_CHANGE_RANK = "§e§lClique pour attribuer ce rang";
	public static final String WARNING_ALREADY_HAS_RANK = "§c§lRang possédé par le joueur";
	
	public static final List<String> loresInfo = Arrays.asList("", INFO_CHANGE_RANK);
	public static final List<String> loresWarning = Arrays.asList("", WARNING_ALREADY_HAS_RANK);

	public RankManager(CommandSender sender) {
		super(sender);
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
		this.sendChangeRankSuccessMessage(player.getName());
		this.info(player);
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
			this.changeRank(player, rank);
		} else {
			this.sendErrorMessage(ALREADY_HAS_RANK);
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
			this.changeRank(player, Rank.JOUEUR);
		} else {
			this.sendErrorMessage(HAS_LOWER_RANK);
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
			this.changeRank(player, Rank.fromPower(playerRank.getPower() + 1));		
		} else {
			this.sendErrorMessage(HAS_HIGHEST_RANK);
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
			this.changeRank(player, Rank.fromPower(playerRank.getPower() - 1));
		} else {
			this.sendErrorMessage(HAS_LOWER_RANK);
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
		this.sendRankInfoMessage(player, playerRank);
	}
	
	/**
	 * Send a message to the sender to notice him that the rank has been changed successfully.
	 * 
	 * @param playerName The name of the player whose rank has been changed.
	 */
	public void sendChangeRankSuccessMessage(String playerName) {
		this.sendMessage(CHANGE_RANK_SUCCESS.replace("%", playerName));
	}
	
	/**
	 * Send a message to the sender with informations about the rank of the target player.
	 * 
	 * @param player The target player.
	 * @param rank The rank of the player.
	 */
	public void sendRankInfoMessage(OfflinePlayer player, Rank rank) {
		this.sendMessage(RANK_INFO.replace("%", player.getName()) + rank.getRankNameColor());
	}
	
	public void openSetRankInventory(OfflinePlayer player) throws Exception {
		if(this.getSender() instanceof Player) {
			Player p = (Player) this.getSender();
			
			if(p.getOpenInventory() != null) {
				p.closeInventory();
			}
			InteractiveInventory inventory = new InteractiveInventory(this.getInventoryItemMap(player), 9, player.getName(), this);
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
					RankManager rankManager = (RankManager) event.getFeatureManager();
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					UUID uuid = sqlAccount.getUUIDOfPlayer(interactiveInventory.getInventory().getName());
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(Rank rank : Rank.values()) {
						if(title.contains(rank.getRankNameColor())) {
							rankManager.setRank(player, rank);
						}
					}
					rankManager.openSetRankInventory(player);
				} catch (Exception e) {
					event.getPlayer().sendMessage(Utils.getExceptionMessage());
					e.printStackTrace();
				}
			}
		};
		
		InventoryItemInteractListener alreadyHasRank = new InventoryItemInteractListener() {
			
			@Override	
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				CommandUtils.sendErrorMessage(event.getPlayer(), ALREADY_HAS_RANK);
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
