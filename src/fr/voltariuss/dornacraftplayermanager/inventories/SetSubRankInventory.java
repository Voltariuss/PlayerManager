package fr.voltariuss.dornacraftplayermanager.inventories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

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
import fr.voltariuss.dornacraftplayermanager.SubRank;
import fr.voltariuss.dornacraftplayermanager.cmd.CmdSubRank;
import fr.voltariuss.dornacraftplayermanager.listeners.InventoryInteractListener;
import net.md_5.bungee.api.ChatColor;

public class SetSubRankInventory extends DornacraftInventory implements InteractInventory {

	private static final String LORE_CLICK_INFO_ADD = ChatColor.YELLOW + "Clique pour attribuer ce sous-rang";
	private static final String LORE_CLICK_INFO_REMOVE = ChatColor.YELLOW + "Clique pour retirer ce sous-rang";
	
	private static final Material MATERIAL_SUBRANG_VIP = Material.EMERALD;
	private static final String ITEM_NAME_VIP = SubRank.VIP.getSubRankColor() + SubRank.VIP.getName();
	
	private static final Material MATERIAL_SUBRANG_VIP_PLUS = Material.DIAMOND;
	private static final String ITEM_NAME_VIP_PLUS = SubRank.VIP_PLUS.getSubRankColor() + SubRank.VIP_PLUS.getName();
	
	private static final Material MATERIAL_SUBRANG_ARCHITECTE = Material.GRASS;
	private static final String ITEM_NAME_ARCHITECTE = SubRank.ARCHITECTE.getSubRankColor() + SubRank.ARCHITECTE.getName();
	
	private static final Material MATERIAL_SUBRANG_REDACTEUR = Material.BOOK_AND_QUILL;
	private static final String ITEM_NAME_REDACTEUR = SubRank.REDACTEUR.getSubRankColor() + SubRank.REDACTEUR.getName();
	
	private static final Material MATERIAL_SUBRANG_DEVELOPPEUR = Material.REDSTONE_COMPARATOR;
	private static final String ITEM_NAME_DEVELOPPEUR = SubRank.DEVELOPPEUR.getSubRankColor() + SubRank.DEVELOPPEUR.getName();

	private OfflinePlayer player;
	private ArrayList<SubRank> subRanks;
	private CmdSubRank cmdSubRank;
	private Inventory inventory;
	private HashMap<Integer, ItemStack> itemMap = new HashMap<>();
	
	public SetSubRankInventory(OfflinePlayer player, ArrayList<SubRank> subRanks, CmdSubRank cmdSubRank) {
		this.setPlayer(player);
		this.setSubRanks(subRanks);
		this.setCmdSubRank(cmdSubRank);
		this.setInventory(Bukkit.createInventory(null, 9, "Définition des sous-rangs : " + player.getName()));
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
		ItemStack vip, vipPlus, architecte, redacteur, developpeur;
		ItemMeta im = null;
		
		vip = new ItemStack(MATERIAL_SUBRANG_VIP);
		im = vip.getItemMeta();
		im.setDisplayName(ITEM_NAME_VIP);
		vip.setItemMeta(im);
		
		vipPlus = new ItemStack(MATERIAL_SUBRANG_VIP_PLUS);
		im = vipPlus.getItemMeta();
		im.setDisplayName(ITEM_NAME_VIP_PLUS);
		vipPlus.setItemMeta(im);
		
		architecte = new ItemStack(MATERIAL_SUBRANG_ARCHITECTE);
		im = architecte.getItemMeta();
		im.setDisplayName(ITEM_NAME_ARCHITECTE);
		architecte.setItemMeta(im);
		
		redacteur = new ItemStack(MATERIAL_SUBRANG_REDACTEUR);
		im = redacteur.getItemMeta();
		im.setDisplayName(ITEM_NAME_REDACTEUR);
		redacteur.setItemMeta(im);
		
		developpeur = new ItemStack(MATERIAL_SUBRANG_DEVELOPPEUR);
		im = developpeur.getItemMeta();
		im.setDisplayName(ITEM_NAME_DEVELOPPEUR);
		developpeur.setItemMeta(im);
		
		
		
		this.addItems(vip, vipPlus, architecte, redacteur, developpeur);
	}
	
	public void interact(Player player, int slot) {
		ItemStack currentItem = this.getItemMap().get(slot);
		
		if(currentItem != null) {
			String name = currentItem.getItemMeta().getDisplayName();
			SubRank subRank = null;
			
			try {
				if(name.contains(SubRank.VIP.getName()) && !name.contains(SubRank.VIP_PLUS.getName())) {
					subRank = SubRank.VIP;
					
					if(this.getSubRanks().contains(subRank)) {
						cmdSubRank.removeSubRank(this.getPlayer(), subRank);
					} else {
						cmdSubRank.addSubRank(this.getPlayer(), subRank);
					}
					player.closeInventory();
					cmdSubRank.openSetSubRankInventory(player);
				} else if(name.contains(SubRank.VIP_PLUS.getName())) {
					subRank = SubRank.VIP_PLUS;
					
					if(this.getSubRanks().contains(subRank)) {
						cmdSubRank.removeSubRank(this.getPlayer(), subRank);
					} else {
						cmdSubRank.addSubRank(this.getPlayer(), subRank);
					}
					player.closeInventory();
					cmdSubRank.openSetSubRankInventory(player);
				} else if(name.contains(SubRank.ARCHITECTE.getName())) {
					subRank = SubRank.ARCHITECTE;
					
					if(this.getSubRanks().contains(subRank)) {
						cmdSubRank.removeSubRank(this.getPlayer(), subRank);
					} else {
						cmdSubRank.addSubRank(this.getPlayer(), subRank);
					}
					player.closeInventory();
					cmdSubRank.openSetSubRankInventory(player);
				} else if(name.contains(SubRank.REDACTEUR.getName())) {
					subRank = SubRank.REDACTEUR;
					
					if(this.getSubRanks().contains(subRank)) {
						cmdSubRank.removeSubRank(this.getPlayer(), subRank);
					} else {
						cmdSubRank.addSubRank(this.getPlayer(), subRank);
					}
					player.closeInventory();
					cmdSubRank.openSetSubRankInventory(player);
				} else if(name.contains(SubRank.DEVELOPPEUR.getName())) {
					subRank = SubRank.DEVELOPPEUR;
					
					if(this.getSubRanks().contains(subRank)) {
						cmdSubRank.removeSubRank(this.getPlayer(), subRank);
					} else {
						cmdSubRank.addSubRank(this.getPlayer(), subRank);
					}
					player.closeInventory();
					cmdSubRank.openSetSubRankInventory(player);
				} else if(currentItem == InventoryUtils.EXIT) {
					player.closeInventory();
				}
			} catch (Exception e) {
				e.printStackTrace();
				player.sendMessage(Utils.getExceptionMessage());
			}
		}
	}

	private void addItems(ItemStack... subRanks) {
		int i = 0;
		
		for(ItemStack subRank : subRanks) {
			ItemMeta im = subRank.getItemMeta();
			Iterator<SubRank> iterator = this.getSubRanks().iterator();
			boolean trouve = false;
			
			while(iterator.hasNext() && !trouve) {
				SubRank sr = iterator.next();
				
				if(subRank.getItemMeta().getDisplayName().contains(sr.getName()) && subRank.getItemMeta().getDisplayName().contains(sr.getSubRankColor())) {
					trouve = true;
					im.addEnchant(Enchantment.DURABILITY, 10, true);
					im.setLore(Arrays.asList("", LORE_CLICK_INFO_REMOVE));
					
				}
			}
			
			if(!trouve) {
				im.setLore(Arrays.asList("", LORE_CLICK_INFO_ADD));
			}
			subRank.setItemMeta(im);
			InventoryUtils.getAsDecorationItem(subRank);
			this.getItemMap().put(i, subRank);
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
	
	private void setCmdSubRank(CmdSubRank cmdSubRank) {
		this.cmdSubRank = cmdSubRank;
	}

	private void setSubRanks(ArrayList<SubRank> subRanks) {
		this.subRanks = subRanks;
	}
	
	public ArrayList<SubRank> getSubRanks() {
		return subRanks;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
}
