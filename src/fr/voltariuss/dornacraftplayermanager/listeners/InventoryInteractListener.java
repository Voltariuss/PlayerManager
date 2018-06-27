package fr.voltariuss.dornacraftplayermanager.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryInteractListener implements Listener {
	
	private Inventory inventory;
	private Player player;
	private ItemStack currentItem;

	@EventHandler
	public void onInventoryInteract(InventoryClickEvent event) {
		inventory = event.getInventory();
		player = (Player) event.getWhoClicked();
		currentItem = event.getCurrentItem();
		
		if(currentItem != null) {
			checkSetRankMenu();
		}
	}
	
	public void checkSetRankMenu() {
		if(currentItem.getType() == Material.STAINED_CLAY) {
			
		}
	}
}
