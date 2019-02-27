package fr.voltariuss.dornacraft.playermanager.features.rank;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraft.api.inventories.ItemInteractive;
import fr.voltariuss.dornacraft.api.msgs.DornacraftAPIMessage;
import fr.voltariuss.dornacraft.api.msgs.MessageLevel;
import fr.voltariuss.dornacraft.api.msgs.MessageUtils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;

/**
 * Classe comportant les listeners de l'inventaire {@link InventoryRank}
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
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
					MessageUtils.sendSystemMessage(MessageLevel.ERROR, humanEntity,
							DornacraftAPIMessage.INTERNAL_EXCEPTION);
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
				MessageUtils.sendSystemMessage(MessageLevel.ERROR, event.getPlayer(), RankManager.ALREADY_HAS_RANK);
			}
		};
	}
}
