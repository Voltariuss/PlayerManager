package fr.voltariuss.dornacraftplayermanager.features.subrank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
import fr.voltariuss.dornacraftapi.inventories.InteractiveInventory;
import fr.voltariuss.dornacraftapi.inventories.InventoryItem;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftapi.items.ItemUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.SQLAccount;

public class SubRankManager extends FeatureManager {
	
	//Instances
	private SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	private SQLSubRank sqlSubRank = DornacraftPlayerManager.getInstance().getSQLSubRank();

	//Messages d'erreur
	public static final String UNKNOW_SUBRANK = "Le sous-rang spécifié est incorrect.";
	public static final String HAS_SUBRANK = "Ce joueur possède déjà le sous-rang spécifié.";
	public static final String DONT_HAS_SPECIFIED_SUBRANK = "Ce joueur ne possède pas le sous-rang spécifié.";
	public static final String DONT_HAS_SUBRANK = "Ce joueur ne possède pas de sous-rang.";
	
	//Autres messages
	public static final String SUCCESS_SUBRANK_ADD = "§aLe sous-rang §6% §aa bien été attribué au joueur §b%§a.";
	public static final String SUCCESS_SUBRANK_REMOVE = "§aLe sous-rang §6% §aa bien été retiré au joueur §b%§a.";
	public static final String SUCCESS_REMOVEALL_SUBRANKS = "§aTous les sous-rangs ont été retirés au joueur §b%§a.";
	public static final String LIST_SUBRANK = "§6Liste des sous-rangs du joueur §b% §6: %";
	
	//Menus
	public static final String INFO_ADD_SUBRANK = "§e§lClique pour attribuer ce sous-rang";
	public static final String INFO_REMOVE_SUBRANK = "§e§lClique pour retirer ce sous-rang";
	
	public static final List<String> loresInfoAdd = Arrays.asList("", INFO_ADD_SUBRANK);
	public static final List<String> loresInfoRemove = Arrays.asList("", INFO_REMOVE_SUBRANK);
	
	public SubRankManager(CommandSender sender) {
		super(sender);
	}
	
	public void addSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		if(!sqlSubRank.hasSubRank(player, subRank)) {
			sqlSubRank.addSubRank(player, subRank);
			this.sendSuccessSubRankAddMessage(subRank.getName(), player.getName());
		} else {
			this.sendErrorMessage(HAS_SUBRANK);
		}
	}
	
	public void removeSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		if(sqlSubRank.hasSubRank(player, subRank)) {
			sqlSubRank.removeSubRank(player, subRank);
			this.sendSuccessSubRankRemoveMessage(subRank.getName(), player.getName());
		} else {
			this.sendErrorMessage(DONT_HAS_SPECIFIED_SUBRANK);
		}
	}
	
	public void removeAllSubRank(OfflinePlayer player) throws Exception {
		if(sqlSubRank.hasSubRank(player)) {
			sqlSubRank.removeAllSubRanks(player);
			this.sendSuccessRemoveAllSubRanksMessage(player.getName());;
		} else {
			this.sendErrorMessage(DONT_HAS_SUBRANK);
		}
	}
	
	public void sendListSubRank(OfflinePlayer player) throws Exception {
		ArrayList<SubRank> subRanks = sqlSubRank.getSubRanks(player);
		
		if(sqlSubRank.hasSubRank(player)) {
			String strSubRanks = "";
			while(!subRanks.isEmpty()) {
				SubRank subRank = subRanks.get(subRanks.size() - 1);
				strSubRanks = strSubRanks + "\n§f - " + subRank.getSubRankColorName();
				subRanks.remove(subRanks.size() - 1);
			}
			this.sendPlayerListSubRanksMessage(player.getName(), strSubRanks);
		} else {
			this.sendErrorMessage(DONT_HAS_SUBRANK);
		}
	}
	
	public void sendSuccessSubRankAddMessage(String subRankName, String playerName) {
		this.sendMessage(SUCCESS_SUBRANK_ADD.replaceFirst("%", subRankName).replaceFirst("%", playerName));
	}
	
	public void sendSuccessSubRankRemoveMessage(String subRankName, String playerName) {
		this.sendMessage(SUCCESS_SUBRANK_REMOVE.replaceFirst("%", subRankName).replaceFirst("%", playerName));
	}
	
	public void sendSuccessRemoveAllSubRanksMessage(String playerName) {
		this.sendMessage(SUCCESS_REMOVEALL_SUBRANKS.replaceFirst("%", playerName));
	}
	
	public void sendPlayerListSubRanksMessage(String playerName, String strSubRanks) {
		this.sendMessage(LIST_SUBRANK.replaceFirst("%", playerName).replaceFirst("%", strSubRanks));
	}
	
	public void openSetSubRankInventory(OfflinePlayer player) throws Exception {
		if(this.getSender() instanceof Player) {
			Player p = (Player) this.getSender();
			InteractiveInventory inventory = new InteractiveInventory(this.getInventoryItemMap(player), 9, player.getName(), this);
			inventory.openInventory(p);
		} else {
			this.sendErrorMessage(Utils.getMustBeAPlayerMessage());
		}
	}
	
	public HashMap<Integer, InventoryItem> getInventoryItemMap(OfflinePlayer player) throws Exception {
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		ArrayList<SubRank> subRanks = sqlSubRank.getSubRanks(player);
		String name = "§cSous-rang: ";
		int amount = 1;
		
		InventoryItemInteractListener addSubRank = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				try {
					SubRankManager subRankManager = (SubRankManager) event.getFeatureManager();
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					UUID uuid = sqlAccount.getUUIDOfPlayer(interactiveInventory.getInventory().getName());
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(SubRank subRank : SubRank.values()) {
						if(title.contains(subRank.getSubRankColorName())) {
							subRankManager.addSubRank(player, subRank);
						}
					}
					event.getPlayer().closeInventory();
					subRankManager.openSetSubRankInventory(player);
				} catch (Exception e) {
					event.getPlayer().sendMessage(Utils.getExceptionMessage());
					e.printStackTrace();
				}
			}
		};
		
		InventoryItemInteractListener removeSubRank = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				try {
					SubRankManager command = (SubRankManager) event.getFeatureManager();
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					UUID uuid = sqlAccount.getUUIDOfPlayer(interactiveInventory.getInventory().getName());
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(SubRank subRank : SubRank.values()) {
						if(title.contains(subRank.getSubRankColorName())) {
							removeSubRank(player, subRank);
						}
					}
					event.getPlayer().closeInventory();
					command.openSetSubRankInventory(player);
				} catch (Exception e) {
					event.getPlayer().sendMessage(Utils.getExceptionMessage());
					e.printStackTrace();
				}
			}
		};
		
		ArrayList<InventoryItem> items = new ArrayList<>();
		items.add(new InventoryItem(ItemUtils.generateItem(Material.EMERALD, amount, (short) 0, name + SubRank.VIP.getSubRankColorName(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.DIAMOND, amount, (short) 0, name + SubRank.VIP_PLUS.getSubRankColorName(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.GRASS, amount, (short) 0, name + SubRank.ARCHITECTE.getSubRankColorName(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.REDSTONE_COMPARATOR, amount, (short) 0, name + SubRank.DEVELOPPEUR.getSubRankColorName(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.BOOK_AND_QUILL, amount, (short) 0, name + SubRank.REDACTEUR.getSubRankColorName(), loresInfoAdd)));
		
		for(int i = 0; i < items.size(); i++) {
			InventoryItem item = items.get(i);
			ItemMeta meta = item.getItemMeta();
			Iterator<SubRank> iterator = subRanks.iterator();
			boolean trouve = false;
			
			while(iterator.hasNext() && !trouve) {
				SubRank subRank = iterator.next();
				
				if(meta.getDisplayName().contains(subRank.getSubRankColorName())) {
					meta.setLore(loresInfoRemove);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
					item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					item.addInventoryItemListener(removeSubRank);
					trouve = true;
				}
			}
			
			if(!trouve) {
				item.addInventoryItemListener(addSubRank);
			}
			inventoryItemMap.put(i, item);
		}
		inventoryItemMap.put(8, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
}
