package fr.voltariuss.dornacraft.playermanager.features.rank;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.events.InventoryItemInteractEvent;
import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.items.ItemInteractive;
import fr.voltariuss.dornacraft.api.listeners.InventoryItemInteractListener;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;

public final class InventoryRankListeners {
	
	/**
	 * @return Le listener modifiant le rang du joueur concerné, non null
	 */
	static InventoryItemInteractListener getChangeRankListener() {
		return new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Player humanEntity = (Player) event.getHumanEntity();
				
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					ItemInteractive inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(Rank rank : Rank.values()) {
						if(title.contains(rank.getColoredName())) {
							RankManager.setRank(humanEntity, target, rank);
						}
					}
					InventoryRank.openInventory(humanEntity, target);
				} catch (Exception e) {
					Utils.sendErrorMessage(humanEntity, ErrorMessage.INTERNAL_EXCEPTION);
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * @return Le listener envoyant un message d'erreur concernant la possession actuel du rang sélectionné, non null
	 */
	static InventoryItemInteractListener getAlreadyHasRangListener() {
		return new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Utils.sendErrorMessage(event.getHumanEntity(), RankManager.ALREADY_HAS_RANK);
			}
		};
	}
}
