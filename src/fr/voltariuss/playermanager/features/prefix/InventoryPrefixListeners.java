package fr.voltariuss.playermanager.features.prefix;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import fr.dornacraft.cache.PlayerCache;
import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.playermanager.AccountManager;
import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.playermanager.features.rank.Rank;
import fr.voltariuss.playermanager.features.subrank.SubRank;
import fr.voltariuss.simpledevapi.MessageLevel;
import fr.voltariuss.simpledevapi.UtilsAPI;
import fr.voltariuss.simpledevapi.inventories.InteractiveInventory;
import fr.voltariuss.simpledevapi.inventories.InventoryItemInteractEvent;
import fr.voltariuss.simpledevapi.inventories.InventoryItemInteractListener;
import fr.voltariuss.simpledevapi.inventories.ItemInteractive;

public final class InventoryPrefixListeners {

	/**
	 * @return Le listener qui effectue un changement de préfixe, non null
	 */
	static InventoryItemInteractListener getChangePrefixListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				if (event.getInventoryItem().getType() != Material.WORKBENCH
						|| (event.getInventoryItem().getType() == Material.WORKBENCH
								&& event.getClickType() == ClickType.LEFT)) {
					Player sender = event.getPlayer();

					try {
						InteractiveInventory interactiveInventory = event.getInteractiveInventory();
						ItemInteractive inventoryItem = event.getInventoryItem();
						OfflinePlayer target = AccountManager
								.getOfflinePlayer(interactiveInventory.getInventory().getName());
						String title = inventoryItem.getItemMeta().getDisplayName();

						for (SubRank subRank : SubRank.values()) {
							if (title.contains(subRank.getPrefix().toString())) {
								PlayerCache playerCache = PlayerCacheManager.getPlayerCacheMap()
										.get(target.getUniqueId());

								if (playerCache.getSubRanks().contains(subRank)
										|| playerCache.getRank() == Rank.ADMIN) {
									PrefixManager.setPrefixType(sender, target, subRank.getPrefix().name());
								} else {
									UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender,
											UtilsPlayerManager.SUBRANK_NOT_OWNED, subRank.getColoredName());
								}
							}
						}
						if (title.equalsIgnoreCase(UtilsPlayerManager.PREFIX_DEFAULT_ITEM_NAME)) {
							PrefixManager.setPrefixType(sender, target, UtilsPlayerManager.PREFIX_DEFAULT_TYPE);
						}
						InventoryPrefix.openInventory(sender, target);
					} catch (Exception e) {
						UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.INTERNAL_EXCEPTION);
						e.printStackTrace();
					}
				}
			}
		};
	}

	/**
	 * @return Le listener ouvrant l'inventaire des préfixes par défaut, non null
	 */
	static InventoryItemInteractListener getOpenDefaultPrefixesInventoryListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Player sender = event.getPlayer();

				if (event.getClickType() == ClickType.RIGHT) {
					try {
						InteractiveInventory interactiveInventory = event.getInteractiveInventory();
						OfflinePlayer target = AccountManager
								.getOfflinePlayer(interactiveInventory.getInventory().getName());
						InventoryPrefixDefault.openInventory(sender, target);
					} catch (Exception e) {
						UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.INTERNAL_EXCEPTION);
						e.printStackTrace();
					}
				}
			}
		};
	}

	/**
	 * @return Le listener envoyant un message d'erreur indiquant la possession du
	 *         préfixe sélectionné, non null
	 */
	static InventoryItemInteractListener getAlreadyHasPrefixListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, event.getPlayer(),
						UtilsPlayerManager.PREFIX_ALREADY_IN_USE);
			}
		};
	}

	/**
	 * @return Le listener envoyant un message d'erreur indiquant l'impossibilité
	 *         d'utiliser le préfixe sélectionné, non null
	 */
	static InventoryItemInteractListener getLockedPrefixListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				UtilsAPI.sendSystemMessage(MessageLevel.ERROR, event.getPlayer(), UtilsPlayerManager.PREFIX_NOT_OWNED);
			}
		};
	}

	/**
	 * @return Le listener renvoyant vers le menu précédent, à savoir le menu
	 *         {@link InventoryPrefix}, non null
	 */
	static InventoryItemInteractListener getBackListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Player sender = event.getPlayer();

				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					OfflinePlayer target = AccountManager
							.getOfflinePlayer(interactiveInventory.getInventory().getName());
					InventoryPrefix.openInventory(sender, target);
				} catch (Exception e) {
					UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.INTERNAL_EXCEPTION);
					e.printStackTrace();
				}
			}
		};
	}
}
