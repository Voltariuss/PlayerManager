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
	 * Ouvre l'inventaire vitrine des pr�fixes par d�faut en fonction des donn�es du joueur cibl�.
	 * 
	 * @param humanEntity L'entit� humaine r�ceptrice de l'inventaire, non null
	 * @param target La joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void openInventory(HumanEntity humanEntity, OfflinePlayer target) throws SQLException {
		InteractiveInventory interactiveInventory = new InteractiveInventory(getDefaultsPrefixsInventoryItemMap(target), 27, target.getName(), false);
		interactiveInventory.openInventory(humanEntity);			
	}
	
	/**
	 * Cr�er et retourne les items constituant l'inventaire � cr�er.
	 * 
	 * @param player Le joueur cibl�, non null
	 * @return La liste des items index� par leur position dans l'inventaire � cr�er, non null
	 * @throws SQLException
	 */
	public static HashMap<Integer, InventoryItem> getDefaultsPrefixsInventoryItemMap(OfflinePlayer player) throws SQLException {
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		int i = 0;
		
		for(Prefix prefix : Prefix.values()) {
			if(prefix.getRequieredLevel() > 0) {
				int requiredLevel = prefix.getRequieredLevel();
				inventoryItemMap.put(i, new InventoryItem(ItemUtils.generateItem(prefix.getMaterial(), 1, (short) 0, InventoryPrefix.getPrefixItemName(prefix.toString(), null),
						Arrays.asList("", "�eNiveau requis : " + (LevelManager.getLevel(player) >= requiredLevel ? "�a" : "�c") + Integer.toString(requiredLevel)))));
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
