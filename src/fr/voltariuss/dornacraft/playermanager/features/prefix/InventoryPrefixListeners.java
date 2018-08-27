package fr.voltariuss.dornacraft.playermanager.features.prefix;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import fr.dornacraft.cache.PlayerCache;
import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryItem;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.features.rank.Rank;
import fr.voltariuss.dornacraft.playermanager.features.subrank.SubRank;

public final class InventoryPrefixListeners {
	
	public static final String ALREADY_HAS_PREFIX = "Préfixe déjà possédé.";
	public static final String LOCKED_PREFIX = "Préfixe vérouillé.";

	/**
	 * @return Le listener qui effectue un changement de préfixe, non null
	 */
	static InventoryItemInteractListener getChangePrefixListener() {
		return new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				if(event.getInventoryItem().getType() != Material.WORKBENCH || (event.getInventoryItem().getType() == Material.WORKBENCH && event.getClickType() == ClickType.LEFT)) {
					Player sender = event.getPlayer();
					
					try {
						InteractiveInventory interactiveInventory = event.getInteractiveInventory();
						InventoryItem inventoryItem = event.getInventoryItem();
						OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
						String title = inventoryItem.getItemMeta().getDisplayName();
						
						for(SubRank subRank : SubRank.values()) {		
							if(title.contains(subRank.getPrefix().toString())) {
								PlayerCache playerCache = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId());
								
								if(playerCache.getSubRanks().contains(subRank) || playerCache.getRank() == Rank.ADMINISTRATEUR) {
									PrefixManager.setPrefixType(sender, target, subRank.getPrefix().name());								
								} else {
									Utils.sendErrorMessage(sender, "Vous ne possédez pas le sous-rang : " + subRank.getColoredName() + "§c.");
								}
							}
						}
						if(title.equalsIgnoreCase(InventoryPrefix.DEFAULT_PREFIX_ITEM_NAME)) {
							PrefixManager.setPrefixType(sender, target, Prefix.getDefault());
						}
						InventoryPrefix.openInventory(sender, target);
					} catch (Exception e) {
						Utils.sendErrorMessage(sender, ErrorMessage.EXCEPTION_MESSAGE);
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
				
				if(event.getClickType() == ClickType.RIGHT) {
					try {
						InteractiveInventory interactiveInventory = event.getInteractiveInventory();
						OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
						InventoryPrefixDefault.openInventory(sender, target);
					} catch (Exception e) {
						Utils.sendErrorMessage(sender, ErrorMessage.EXCEPTION_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		};
	}
	
	/**
	 * @return Le listener envoyant un message d'erreur indiquant la possession du préfixe sélectionné, non null
	 */
	static InventoryItemInteractListener getAlreadyHasPrefixListener() {
		return new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Utils.sendErrorMessage(event.getPlayer(), ALREADY_HAS_PREFIX);
			}
		};
	}
	
	/**
	 * @return Le listener envoyant un message d'erreur indiquant l'impossibilité d'utiliser le préfixe sélectionné, non null
	 */
	static InventoryItemInteractListener getLockedPrefixListener() {
		return new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Utils.sendErrorMessage(event.getPlayer(), LOCKED_PREFIX);
			}
		};
	}
	
	/**
	 * @return Le listener renvoyant vers le menu précédent, à savoir le menu {@link InventoryPrefix}, non null
	 */
	static InventoryItemInteractListener getBackListener() {
		return new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Player sender = event.getPlayer();
				
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					InventoryPrefix.openInventory(sender, target);
				} catch(Exception e) {
					Utils.sendErrorMessage(sender, ErrorMessage.EXCEPTION_MESSAGE);
					e.printStackTrace();
				}
			}
		};
	}
}
