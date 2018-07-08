package fr.voltariuss.dornacraftplayermanager.listeners;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftplayermanager.inventories.InteractInventory;

public class InventoryInteractListener implements Listener {
	
	private static final HashMap<Inventory, InteractInventory> listenerMap = new HashMap<>();
	
	private static HashMap<Inventory, InteractInventory> getListenerMap() {
		return listenerMap;
	}
	
	public static void addListener(Inventory inventory, InteractInventory interactInventory) {
		getListenerMap().put(inventory, interactInventory);
	}

	@EventHandler
	public void onInventoryInteract(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		Player player = (Player) event.getWhoClicked();
		ItemStack currentItem = event.getCurrentItem();
		int slot = event.getSlot();
		
		if(currentItem == null || currentItem.getType() == Material.AIR) {
			return;
		}
		
		if(listenerMap.containsKey(inventory)) {
			event.setCancelled(true);
			
			if(currentItem == InventoryUtils.EXIT) {
				player.closeInventory();
			} else {
				InteractInventory interactInventory = getListenerMap().get(inventory);
				interactInventory.interact(player, slot);
			}
		}
	}
}
