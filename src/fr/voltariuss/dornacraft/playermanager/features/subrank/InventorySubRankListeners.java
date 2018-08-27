package fr.voltariuss.dornacraft.playermanager.features.subrank;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryItem;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
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
				Player sender = event.getPlayer();
				
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(SubRank subRank : SubRank.values()) {
						if(title.contains(subRank.getColoredName())) {
							SubRankManager.addSubRank(sender, target, subRank);
						}
					}
					InventorySubRank.openInventory(sender, target);
				} catch (Exception e) {
					Utils.sendErrorMessage(sender, ErrorMessage.EXCEPTION_MESSAGE);
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
				Player sender = event.getPlayer();
				
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(SubRank subRank : SubRank.values()) {
						if(title.contains(subRank.getColoredName())) {
							SubRankManager.removeSubRank(sender, target, subRank);
						}
					}
					InventorySubRank.openInventory(sender, target);
				} catch (Exception e) {
					Utils.sendErrorMessage(sender, ErrorMessage.EXCEPTION_MESSAGE);
					e.printStackTrace();
				}
			}
		};
	}
}
