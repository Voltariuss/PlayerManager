package fr.voltariuss.dornacraft.playermanager.features.subrank;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;

import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraft.api.inventories.ItemInteractive;
import fr.voltariuss.dornacraft.api.utils.MessageLevel;
import fr.voltariuss.dornacraft.api.utils.MessageUtils;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;

public final class InventorySubRankListeners {

	/**
	 * @return Le listener ajoutant le sous-rang sélectionné au joueur concerné, non null
	 */
	static InventoryItemInteractListener getAddSubRankListener() {
		return new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				HumanEntity sender = event.getHumanEntity();
				
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					ItemInteractive inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(SubRank subRank : SubRank.values()) {
						if(title.contains(subRank.getColoredName())) {
							SubRankManager.addSubRank(sender, target, subRank);
						}
					}
					InventorySubRank.openInventory(sender, target);
				} catch (Exception e) {
					Utils.sendSystemMessage(MessageLevel.ERROR, sender, MessageUtils.INTERNAL_EXCEPTION);
					e.printStackTrace();
				}
			}
		};
	}
	
	/**
	 * @return Le listener retirant le sous-rang sélectionné au joueur concerné, non null
	 */
	static InventoryItemInteractListener getRemoveSubRankListener() {
		return new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				HumanEntity sender = event.getHumanEntity();
				
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					ItemInteractive inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(SubRank subRank : SubRank.values()) {
						if(title.contains(subRank.getColoredName())) {
							SubRankManager.removeSubRank(sender, target, subRank);
						}
					}
					InventorySubRank.openInventory(sender, target);
				} catch (Exception e) {
					Utils.sendSystemMessage(MessageLevel.ERROR, sender, MessageUtils.INTERNAL_EXCEPTION);
					e.printStackTrace();
				}
			}
		};
	}
}
