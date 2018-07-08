package fr.voltariuss.dornacraftplayermanager.inventories;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraftapi.inventories.DornacraftInventory;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.Rank;
import fr.voltariuss.dornacraftplayermanager.cmd.CmdRank;
import fr.voltariuss.dornacraftplayermanager.listeners.InventoryInteractListener;
import net.md_5.bungee.api.ChatColor;

public class SetRankInventory extends DornacraftInventory implements InteractInventory {
		
	private static final Material MATERIAL_RANG = Material.STAINED_CLAY;
	private static final String LORE_CLICK_INFO = ChatColor.YELLOW + "Clique pour attribuer ce rang";
	private static final String LORE_CLICK_WARNING = ChatColor.RED + "Rang déjà possédé par le joueur";
	
	private static final String ITEM_NAME_JOUEUR = Rank.JOUEUR.getRankColor() + Rank.JOUEUR.getRankName();
	private static final short ITEM_DATA_JOUEUR = 9;
	
	private static final String ITEM_NAME_GUIDE = Rank.GUIDE.getRankColor() + Rank.GUIDE.getRankName();
	private static final short ITEM_DATA_GUIDE = 11;
	
	private static final String ITEM_NAME_MODERATEUR = Rank.MODERATEUR.getRankColor() + Rank.MODERATEUR.getRankName();
	private static final short ITEM_DATA_MODERATEUR = 1;
	
	private static final String ITEM_NAME_ADMINISTRATEUR = Rank.ADMINISTRATEUR.getRankColor() + Rank.ADMINISTRATEUR.getRankName();
	private static final short ITEM_DATA_ADMINISTRATEUR = 14;
	

	private OfflinePlayer player;
	private Rank currentRank;
	private CmdRank cmdRank;
	private Inventory inventory;
	private HashMap<Integer, ItemStack> itemMap = new HashMap<>();
	
	public SetRankInventory(OfflinePlayer player, Rank currentRank, CmdRank cmdRank) {
		this.setPlayer(player);
		this.setCurrentRank(currentRank);
		this.setCmdRank(cmdRank);
		this.setInventory(Bukkit.createInventory(null, 9, "Définition du rang : " + player.getName()));
		this.createItems();
		this.addItemsToInventory();
		InventoryInteractListener.addListener(this.getInventory(), this);
	}

	@Override
	public void openInventory(Player player) {
		player.openInventory(inventory);
	}

	@Override
	protected void createItems() {
		ItemStack joueur, guide, moderateur, administrateur;
		ItemMeta im = null;
		
		joueur = new ItemStack(MATERIAL_RANG, 1, ITEM_DATA_JOUEUR);
		im = joueur.getItemMeta();
		im.setDisplayName(ITEM_NAME_JOUEUR);
		joueur.setItemMeta(im);
		
		guide = new ItemStack(MATERIAL_RANG, 1, ITEM_DATA_GUIDE);
		im = guide.getItemMeta();
		im.setDisplayName(ITEM_NAME_GUIDE);
		guide.setItemMeta(im);
		
		moderateur = new ItemStack(MATERIAL_RANG, 1, ITEM_DATA_MODERATEUR);
		im = moderateur.getItemMeta();
		im.setDisplayName(ITEM_NAME_MODERATEUR);
		moderateur.setItemMeta(im);
		
		administrateur = new ItemStack(MATERIAL_RANG, 1, ITEM_DATA_ADMINISTRATEUR);
		im = administrateur.getItemMeta();
		im.setDisplayName(ITEM_NAME_ADMINISTRATEUR);
		administrateur.setItemMeta(im);
		
		this.addItems(joueur, guide, moderateur, administrateur);
	}
	
	public void interact(Player player, int slot) {
		ItemStack currentItem = this.getItemMap().get(slot);
		String name = currentItem.getItemMeta().getDisplayName();
		
		try {
			if(name.contains(Rank.JOUEUR.getRankName())) {
				cmdRank.setRank(this.getPlayer(), Rank.JOUEUR);
				player.closeInventory();
			} else if(name.contains(Rank.GUIDE.getRankName())) {
				cmdRank.setRank(this.getPlayer(), Rank.GUIDE);
				player.closeInventory();
			} else if(name.contains(Rank.MODERATEUR.getRankName())) {	
				cmdRank.setRank(this.getPlayer(), Rank.MODERATEUR);
				player.closeInventory();
			} else if(name.contains(Rank.ADMINISTRATEUR.getRankName())) {
				cmdRank.setRank(this.getPlayer(), Rank.ADMINISTRATEUR);
				player.closeInventory();
			} else if(currentItem == InventoryUtils.EXIT) {
				player.closeInventory();
			}
		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(Utils.getExceptionMessage());
		}
	}

	private void addItems(ItemStack... ranks) {
		int i = 0;
		
		for(ItemStack rank : ranks) {
			ItemMeta im = rank.getItemMeta();
			
			if(rank.getItemMeta().getDisplayName().contains(this.getCurrentRank().getRankName())) {
				im.addEnchant(Enchantment.DURABILITY, 10, true);
				im.setLore(Arrays.asList("", LORE_CLICK_WARNING));
			} else {
				im.setLore(Arrays.asList("", LORE_CLICK_INFO));
			}
			rank.setItemMeta(im);
			InventoryUtils.getAsDecorationItem(rank);
			this.getItemMap().put(i, rank);
			i++;
		}
		this.getItemMap().put(8, InventoryUtils.EXIT);
	}
	
	public void addItemsToInventory() {
		for(Integer i : this.getItemMap().keySet()) {
			ItemStack it = this.getItemMap().get(i);
			this.getInventory().setItem(i, it);
		}
	}

	public HashMap<Integer, ItemStack> getItemMap() {
		return itemMap;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	private void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

	public Rank getCurrentRank() {
		return currentRank;
	}
	
	private void setCmdRank(CmdRank cmdRank) {
		this.cmdRank = cmdRank;
	}

	private void setCurrentRank(Rank currentRank) {
		this.currentRank = currentRank;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
}
