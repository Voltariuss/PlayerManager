package fr.voltariuss.dornacraft.playermanager.features.rank;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryItem;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractListener;
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
				Player player = event.getPlayer();
				
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(Rank rank : Rank.values()) {
						if(title.contains(rank.getColoredName())) {
							RankManager.setRank(player, target, rank);
						}
					}
					InventoryRank.openInventory(player, target);
				} catch (Exception e) {
					Utils.sendErrorMessage(player, ErrorMessage.EXCEPTION_MESSAGE);
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
				Utils.sendErrorMessage(event.getPlayer(), RankManager.ALREADY_HAS_RANK);
			}
		};
	}
}
