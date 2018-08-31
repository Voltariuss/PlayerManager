package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryItem;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraft.api.inventories.InventoryUtils;
import fr.voltariuss.dornacraft.api.items.ItemUtils;
import fr.voltariuss.dornacraft.playermanager.features.subrank.SubRank;
import fr.voltariuss.dornacraft.playermanager.features.subrank.SubRankManager;

public final class InventoryPrefix {
	
	public static final String DEFAULT_PREFIX_ITEM_NAME = "§ePréfixe par défaut";
	public static final String UNAVALAIBLE_TAG = "§c§lIndisponible";
	public static final String AVALAIBLE_TAG = "§a§lDisponible";
	
	private static final List<String> CLICK_INFO_ACTIVATION = Arrays.asList("", "§7Clique pour activer");
	private static final List<String> ACTUAL_PREFIX_INFO = Arrays.asList("", "§a§lPréfixe actuel");
	

	/**
	 * Ouvre l'inventaire de gestion des préfixes du joueur ciblé.
	 * 
	 * @param humanEntity L'entité humaine réceptrice de l'inventaire, non null
	 * @param target Le joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void openInventory(HumanEntity humanEntity, OfflinePlayer target) throws SQLException {
		InteractiveInventory interactiveInventory = new InteractiveInventory(getInventoryItemMap(target), 27, target.getName(), false);
		interactiveInventory.openInventory(humanEntity);
	}
	
	/**
	 * Créer et retourne les items constituant l'inventaire à créer.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return La liste des items indexé par leur position dans l'inventaire à créer, non null
	 * @throws SQLException
	 */
	public static HashMap<Integer, InventoryItem> getInventoryItemMap(OfflinePlayer target) throws SQLException {
		String prefixType = SQLPrefixType.getPrefixType(target);
		ArrayList<SubRank> subRanks = SubRankManager.getSubRanks(target);
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		int i = 0;
		
		InventoryItemInteractListener changePrefixListener = InventoryPrefixListeners.getChangePrefixListener();
		InventoryItemInteractListener alreadyHasPrefix = InventoryPrefixListeners.getAlreadyHasPrefixListener();
		InventoryItemInteractListener lockedPrefix = InventoryPrefixListeners.getLockedPrefixListener();
		
		for(SubRank subRank : SubRank.values()) {
			if(subRank != SubRank.VIP && subRank != SubRank.VIP_PLUS) {
				InventoryItem item = new InventoryItem(ItemUtils.generateItem(Material.INK_SACK, 1, (short) 8, 
						getPrefixItemName(subRank.getPrefix().toString(), UNAVALAIBLE_TAG), CLICK_INFO_ACTIVATION));
				
				if(subRanks.contains(subRank)) {
					item.setType(subRank.getMaterial());
					item.setDurability((short) 0);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(meta.getDisplayName().replace(UNAVALAIBLE_TAG, AVALAIBLE_TAG));
					item.setItemMeta(meta);
					
					if(prefixType.equalsIgnoreCase(subRank.name())) {
						meta.setDisplayName(meta.getDisplayName().replace(AVALAIBLE_TAG, ""));
						meta.setLore(ACTUAL_PREFIX_INFO);
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						item.setItemMeta(meta);
						item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
						item.getListeners().add(alreadyHasPrefix);
					} else {
						item.getListeners().add(changePrefixListener);
					}
				} else {
					ItemMeta meta = item.getItemMeta();
					meta.setLore(getLockedPrefixInfoLore(getSubRankTermString(subRank)));
					item.setItemMeta(meta);
					item.getListeners().add(lockedPrefix);
				}
				inventoryItemMap.put(i, item);
				i++;
			}
		}
		
		InventoryItem defaultPrefixItem = new InventoryItem(ItemUtils.generateItem(Material.WORKBENCH, 1, (short) 0, DEFAULT_PREFIX_ITEM_NAME, CLICK_INFO_ACTIVATION));
		
		if(prefixType.equalsIgnoreCase("DEFAULT")) {
			ItemMeta meta = defaultPrefixItem.getItemMeta();
			meta.setLore(ACTUAL_PREFIX_INFO);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			defaultPrefixItem.setItemMeta(meta);
			defaultPrefixItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			defaultPrefixItem.getListeners().add(alreadyHasPrefix);
		} else {
			defaultPrefixItem.getListeners().add(changePrefixListener);
		}
		ItemMeta defaultPrefixMeta = defaultPrefixItem.getItemMeta();
		List<String> lore = defaultPrefixMeta.getLore();
		lore.set(1, lore.get(1).replace("Clique", "Clic-gauche"));
		lore.add("§7Clic-droit pour plus d'infos");
		defaultPrefixMeta.setLore(lore);
		defaultPrefixItem.setItemMeta(defaultPrefixMeta);
		defaultPrefixItem.getListeners().add(InventoryPrefixListeners.getOpenDefaultPrefixesInventoryListener());
		
		InventoryItem vipPrefixItem = new InventoryItem(ItemUtils.generateItem(Material.GLASS, 1, (short) 0, 
				getPrefixItemName(SubRank.VIP.getPrefix().toString(), UNAVALAIBLE_TAG), 
				getLockedPrefixInfoLore(getSubRankTermString(SubRank.VIP))));
		
		if(subRanks.contains(SubRank.VIP_PLUS)) {
			vipPrefixItem.setType(Material.DIAMOND);
			ItemMeta meta = vipPrefixItem.getItemMeta();
			meta.setDisplayName(getPrefixItemName(SubRank.VIP_PLUS.getPrefix().toString(), AVALAIBLE_TAG));
			vipPrefixItem.setItemMeta(meta);
		} else if(subRanks.contains(SubRank.VIP)) {
			vipPrefixItem.setType(Material.EMERALD);
			ItemMeta meta = vipPrefixItem.getItemMeta();
			meta.setDisplayName(getPrefixItemName(SubRank.VIP.getPrefix().toString(), AVALAIBLE_TAG));
			vipPrefixItem.setItemMeta(meta);
		}
		if(subRanks.contains(SubRank.VIP_PLUS) || subRanks.contains(SubRank.VIP)) {
			if(prefixType.equalsIgnoreCase(SubRank.VIP.name()) || prefixType.equalsIgnoreCase(SubRank.VIP_PLUS.name())) {
				ItemMeta meta = vipPrefixItem.getItemMeta();
				meta.setDisplayName(meta.getDisplayName().replace(AVALAIBLE_TAG, ""));
				meta.setLore(ACTUAL_PREFIX_INFO);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				vipPrefixItem.setItemMeta(meta);
				vipPrefixItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				vipPrefixItem.getListeners().add(alreadyHasPrefix);
			} else {
				vipPrefixItem.getListeners().add(changePrefixListener);
			}			
		} else {
			vipPrefixItem.getListeners().add(lockedPrefix);
		}
		inventoryItemMap.put(21, defaultPrefixItem);
		inventoryItemMap.put(23, vipPrefixItem);
		inventoryItemMap.put(26, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
	
	/**
	 * @param prefix Le préfixe, non null
	 * @param tag Le tag, peut être null
	 * @return Le nom d'item comportant le préfixe et éventuellement un tag, non null
	 */
	static String getPrefixItemName(String prefix, String tag) {
		return "§6Préfixe : " + prefix + (tag == null ? "" : " " + tag);
	}
	
	/**
	 * @param subRank Le {@link SubRank}, non null
	 * @return La condition d'avoir le sous-rang spécifié sous la forme d'une chaîne de caractères, non null
	 */
	private static String getSubRankTermString(SubRank subRank) {
		return "§7Sous-rang : " + subRank.getColoredName();
	}
	
	/**
	 * @param term La condition
	 * @return La description de la condition, non null
	 */
	private static List<String> getLockedPrefixInfoLore(String term) {
		return Arrays.asList("", term);
	}
}
