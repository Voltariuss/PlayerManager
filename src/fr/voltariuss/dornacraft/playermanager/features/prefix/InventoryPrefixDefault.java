package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;

import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryItem;
import fr.voltariuss.dornacraft.api.inventories.InventoryUtils;
import fr.voltariuss.dornacraft.api.items.ItemUtils;
import fr.voltariuss.dornacraft.playermanager.features.level.LevelManager;

public final class InventoryPrefixDefault {
	
	/**
	 * Ouvre l'inventaire vitrine des préfixes par défaut en fonction des données du joueur ciblé.
	 * 
	 * @param humanEntity L'entité humaine réceptrice de l'inventaire, non null
	 * @param target La joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void openInventory(HumanEntity humanEntity, OfflinePlayer target) throws SQLException {
		InteractiveInventory interactiveInventory = new InteractiveInventory(getDefaultsPrefixsInventoryItemMap(target), 27, target.getName(), false);
		interactiveInventory.openInventory(humanEntity);			
	}
	
	/**
	 * Créer et retourne les items constituant l'inventaire à créer.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return La liste des items indexé par leur position dans l'inventaire à créer, non null
	 * @throws SQLException
	 */
	public static HashMap<Integer, InventoryItem> getDefaultsPrefixsInventoryItemMap(OfflinePlayer player) throws SQLException {
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		int i = 0;
		
		for(Prefix prefix : Prefix.values()) {
			if(prefix.getRequieredLevel() > 0) {
				int requiredLevel = prefix.getRequieredLevel();
				inventoryItemMap.put(i, new InventoryItem(ItemUtils.generateItem(prefix.getMaterial(), 1, (short) 0, InventoryPrefix.getPrefixItemName(prefix.toString(), null),
						Arrays.asList("", "§eNiveau requis : " + (LevelManager.getLevel(player) >= requiredLevel ? "§a" : "§c") + Integer.toString(requiredLevel)))));
				i++;
			} else {
				break;
			}
		}
		inventoryItemMap.put(25, InventoryUtils.getBackItem(InventoryPrefixListeners.getBackListener()));
		inventoryItemMap.put(26, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
}
