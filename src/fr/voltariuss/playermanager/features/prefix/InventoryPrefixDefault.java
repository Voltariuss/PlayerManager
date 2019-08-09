package fr.voltariuss.playermanager.features.prefix;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.playermanager.features.level.LevelManager;
import fr.voltariuss.simpledevapi.inventories.InteractiveInventory;
import fr.voltariuss.simpledevapi.inventories.InventoryUtils;
import fr.voltariuss.simpledevapi.inventories.ItemInteractive;
import fr.voltariuss.simpledevapi.items.ItemUtils;

public final class InventoryPrefixDefault {

	/**
	 * Ouvre l'inventaire vitrine des préfixes par défaut en fonction des données du
	 * joueur ciblé.
	 * 
	 * @param player Le joueur récepteur de l'inventaire, non null
	 * @param target La joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void openInventory(Player player, OfflinePlayer target) throws SQLException {
		InteractiveInventory interactiveInventory = new InteractiveInventory(getDefaultsPrefixsInventoryItemMap(target),
				27, target.getName(), false);
		interactiveInventory.openInventory(player);
	}

	/**
	 * Créer et retourne les items constituant l'inventaire à créer.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return La liste des items indexés par leur position dans l'inventaire à
	 *         créer, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static HashMap<Integer, ItemInteractive> getDefaultsPrefixsInventoryItemMap(OfflinePlayer player)
			throws SQLException {
		HashMap<Integer, ItemInteractive> inventoryItemMap = new HashMap<>();
		int i = 0;

		for (Prefix prefix : Prefix.values()) {
			if (prefix.getRequieredLevel() > 0) {
				int requiredLevel = prefix.getRequieredLevel();
				inventoryItemMap.put(i, new ItemInteractive(ItemUtils.generateItem(prefix.getMaterial(), 1, (short) 0,
						InventoryPrefix.getPrefixItemName(prefix.toString(), null),
						Arrays.asList("", String.format(UtilsPlayerManager.PREFIX_LEVEL_REQUIRED_TAG,
								(LevelManager.getLevel(player) >= requiredLevel ? ChatColor.GREEN : ChatColor.RED),
								requiredLevel)))));
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
