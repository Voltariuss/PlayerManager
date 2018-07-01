package fr.voltariuss.dornacraftplayermanager.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.voltariuss.dornacraftplayermanager.inventories.InteractInventory;

public class InventoryInteractListener implements Listener {
	
	private static final HashMap<Inventory, InteractInventory> listenerMap = new HashMap<>();
	
	public static final addListener()

	@EventHandler
	public void onInventoryInteract(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		Player player = (Player) event.getWhoClicked();
		
		if(listenerMap.containsKey(inventory)) {
		}
	}
}
