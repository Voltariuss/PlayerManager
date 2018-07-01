package fr.voltariuss.dornacraftplayermanager.inventories;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraftapi.inventories.DornacraftInventory;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.Rank;
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
	
	private Inventory inventory = Bukkit.createInventory(null, 9, "Définition du rang");
	private HashMap<Integer, ItemStack> itemMap = new HashMap<>();
	private Rank currentRank;
	
	public SetRankInventory(Rank currentRank) {
		this.setCurrentRank(currentRank);
		this.createItems();
		this.addItemsToInventory();
		DornacraftPlayerManager.getInstance().getInventoryInteractListener().
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
	
	public void interact(Player player) {
		
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

	public Rank getCurrentRank() {
		return currentRank;
	}

	private void setCurrentRank(Rank currentRank) {
		this.currentRank = currentRank;
	}
}
