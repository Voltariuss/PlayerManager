package fr.voltariuss.playermanager.features.rank;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.voltariuss.playermanager.AccountManager;
import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.simpledevapi.MessageLevel;
import fr.voltariuss.simpledevapi.UtilsAPI;
import fr.voltariuss.simpledevapi.inventories.InteractiveInventory;
import fr.voltariuss.simpledevapi.inventories.InventoryItemInteractEvent;
import fr.voltariuss.simpledevapi.inventories.InventoryItemInteractListener;
import fr.voltariuss.simpledevapi.inventories.ItemInteractive;

public final class InventoryRankListeners {

	/**
	 * @return Le listener modifiant le rang du joueur concerné, non null
	 */
	static InventoryItemInteractListener getChangeRankListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Player humanEntity = event.getPlayer();

				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					ItemInteractive inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager
							.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();

					for (Rank rank : Rank.values()) {
						if (title.contains(rank.getColoredName())) {
							RankManager.setRank(humanEntity, target, rank);
						}
					}
					InventoryRank.openInventory(humanEntity, target);
				} catch (Exception e) {
					UtilsAPI.sendSystemMessage(MessageLevel.ERROR, humanEntity, UtilsAPI.INTERNAL_EXCEPTION);
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * @return Le listener envoyant un message d'erreur concernant la possession
	 *         actuel du rang sélectionné, non null
	 */
	static InventoryItemInteractListener getAlreadyHasRangListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, event.getPlayer(),
						UtilsPlayerManager.RANK_ALREADY_OWNED);
			}
		};
	}
}
