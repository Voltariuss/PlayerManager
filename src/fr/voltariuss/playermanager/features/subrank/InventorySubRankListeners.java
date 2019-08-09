package fr.voltariuss.playermanager.features.subrank;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;

import fr.voltariuss.playermanager.AccountManager;
import fr.voltariuss.simpledevapi.MessageLevel;
import fr.voltariuss.simpledevapi.UtilsAPI;
import fr.voltariuss.simpledevapi.inventories.InteractiveInventory;
import fr.voltariuss.simpledevapi.inventories.InventoryItemInteractEvent;
import fr.voltariuss.simpledevapi.inventories.InventoryItemInteractListener;
import fr.voltariuss.simpledevapi.inventories.ItemInteractive;

public final class InventorySubRankListeners {

	/**
	 * @return Le listener ajoutant le sous-rang sélectionné au joueur concerné, non
	 *         null
	 */
	static InventoryItemInteractListener getAddSubRankListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				HumanEntity sender = event.getPlayer();

				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					ItemInteractive inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager
							.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();

					for (SubRank subRank : SubRank.values()) {
						if (title.contains(subRank.getColoredName())) {
							SubRankManager.addSubRank(sender, target, subRank);
						}
					}
					InventorySubRank.openInventory(sender, target);
				} catch (Exception e) {
					UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.INTERNAL_EXCEPTION);
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * @return Le listener retirant le sous-rang sélectionné au joueur concerné, non
	 *         null
	 */
	static InventoryItemInteractListener getRemoveSubRankListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				HumanEntity sender = event.getPlayer();

				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					ItemInteractive inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager
							.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();

					for (SubRank subRank : SubRank.values()) {
						if (title.contains(subRank.getColoredName())) {
							SubRankManager.removeSubRank(sender, target, subRank);
						}
					}
					InventorySubRank.openInventory(sender, target);
				} catch (Exception e) {
					UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.INTERNAL_EXCEPTION);
					e.printStackTrace();
				}
			}
		};
	}
}
