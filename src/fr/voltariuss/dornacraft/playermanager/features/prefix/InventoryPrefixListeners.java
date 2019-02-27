package fr.voltariuss.dornacraft.playermanager.features.prefix;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import fr.dornacraft.cache.PlayerCache;
import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraft.api.inventories.ItemInteractive;
import fr.voltariuss.dornacraft.api.msgs.DornacraftAPIMessage;
import fr.voltariuss.dornacraft.api.msgs.MessageLevel;
import fr.voltariuss.dornacraft.api.msgs.MessageUtils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.features.rank.Rank;
import fr.voltariuss.dornacraft.playermanager.features.subrank.SubRank;

/**
 * Classe comportant les listeners de l'inventaire {@link InventoryPrefix}.
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class InventoryPrefixListeners {

	public static final String ALREADY_HAS_PREFIX = "Pr�fixe d�j� poss�d�.";
	public static final String LOCKED_PREFIX = "Pr�fixe v�rouill�.";

	/**
	 * @return Le listener qui effectue un changement de pr�fixe, non null
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
										|| playerCache.getRank() == Rank.ADMINISTRATEUR) {
									PrefixManager.setPrefixType(sender, target, subRank.getPrefix().name());
								} else {
									MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender,
											"Vous ne poss�dez pas le sous-rang : %s�r.", subRank.getColoredName());
								}
							}
						}
						if (title.equalsIgnoreCase(InventoryPrefix.DEFAULT_PREFIX_ITEM_NAME)) {
							PrefixManager.setPrefixType(sender, target, Prefix.getDefault());
						}
						InventoryPrefix.openInventory(sender, target);
					} catch (Exception e) {
						MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender,
								DornacraftAPIMessage.INTERNAL_EXCEPTION);
						e.printStackTrace();
					}
				}
			}
		};
	}

	/**
	 * @return Le listener ouvrant l'inventaire des pr�fixes par d�faut, non null
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
						MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender,
								DornacraftAPIMessage.INTERNAL_EXCEPTION);
						e.printStackTrace();
					}
				}
			}
		};
	}

	/**
	 * @return Le listener envoyant un message d'erreur indiquant la possession du
	 *         pr�fixe s�lectionn�, non null
	 */
	static InventoryItemInteractListener getAlreadyHasPrefixListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				MessageUtils.sendSystemMessage(MessageLevel.ERROR, event.getPlayer(), ALREADY_HAS_PREFIX);
			}
		};
	}

	/**
	 * @return Le listener envoyant un message d'erreur indiquant l'impossibilit�
	 *         d'utiliser le pr�fixe s�lectionn�, non null
	 */
	static InventoryItemInteractListener getLockedPrefixListener() {
		return new InventoryItemInteractListener() {

			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				MessageUtils.sendSystemMessage(MessageLevel.ERROR, event.getPlayer(), LOCKED_PREFIX);
			}
		};
	}

	/**
	 * @return Le listener renvoyant vers le menu pr�c�dent, � savoir le menu
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
					MessageUtils.sendSystemMessage(MessageLevel.ERROR, sender, DornacraftAPIMessage.INTERNAL_EXCEPTION);
					e.printStackTrace();
				}
			}
		};
	}
}
