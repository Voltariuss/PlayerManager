package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraft.api.UtilsAPI;
import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryUtils;
import fr.voltariuss.dornacraft.api.inventories.ItemInteractive;
import fr.voltariuss.dornacraft.api.items.ItemUtils;
import fr.voltariuss.dornacraft.playermanager.UtilsPlayerManager;
import fr.voltariuss.dornacraft.playermanager.features.subrank.SubRank;
import fr.voltariuss.dornacraft.playermanager.features.subrank.SubRankManager;

/**
 * Classe de définition de l'inventaire des préfixes d'un joueur
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class InventoryPrefix {

	private static final List<String> LORE_ACTIVATION_INFO = Arrays.asList("", ChatColor.GRAY + UtilsAPI.INVENTORY_INDICATION_TO_ACTIVATE);
	private static final List<String> LORE_ACTUAL_PREFIX_INFO = Arrays.asList("", UtilsPlayerManager.PREFIX_ACTUAL_TAG);

	/**
	 * Ouvre l'inventaire de gestion des préfixes du joueur ciblé.
	 * 
	 * @param player
	 *            Le joueur récepteur de l'inventaire, non null
	 * @param target
	 *            Le joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static void openInventory(Player player, OfflinePlayer target) throws SQLException {
		InteractiveInventory interactiveInventory = new InteractiveInventory(getInventoryItemMap(target), 27,
				target.getName(), false);
		interactiveInventory.openInventory(player);
	}

	/**
	 * Créer et retourne les items constituant l'inventaire à créer.
	 * 
	 * @param target
	 *            Le joueur ciblé, non null
	 * @return La liste des items indexé par leur position dans l'inventaire à
	 *         créer, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static HashMap<Integer, ItemInteractive> getInventoryItemMap(OfflinePlayer target) throws SQLException {
		String prefixType = SQLPrefixType.getPrefixType(target);
		ArrayList<SubRank> subRanks = SubRankManager.getSubRanks(target);
		HashMap<Integer, ItemInteractive> inventoryItemMap = new HashMap<>();
		int i = 0;

		// On ajoute tous les préfixes associés aux sous-rangs
		for (SubRank subRank : SubRank.values()) {
			// A l'exclusion des sous-rangs VIP qui seront traités différement plus bas
			if (subRank != SubRank.VIP && subRank != SubRank.VIP_PLUS) {
				// On créer l'item par défaut d'un sous-rang (une poche d'encre)
				ItemInteractive item = new ItemInteractive(ItemUtils.generateItem(Material.INK_SACK, 1, (short) 8,
						getPrefixItemName(subRank.getPrefix().toString(), UtilsPlayerManager.PREFIX_UNAVAILABLE_TAG), LORE_ACTIVATION_INFO));

				if (subRanks.contains(subRank)) {
					// Si le joueur possède la sous-rang, alors on modifie l'item
					item.setType(subRank.getMaterial());
					item.setDurability((short) 0);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(meta.getDisplayName().replace(UtilsPlayerManager.PREFIX_UNAVAILABLE_TAG, UtilsPlayerManager.PREFIX_AVAILABLE_TAG));
					item.setItemMeta(meta);

					if (prefixType.equalsIgnoreCase(subRank.name())) {
						// Si il s'agit du préfixe utilisé par le joueur, alors on lui ajoute une
						// disctinction
						meta.setDisplayName(meta.getDisplayName().replace(UtilsPlayerManager.PREFIX_AVAILABLE_TAG, ""));
						meta.setLore(LORE_ACTUAL_PREFIX_INFO);
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						item.setItemMeta(meta);
						item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
						item.getListeners().add(InventoryPrefixListeners.getAlreadyHasPrefixListener());
					} else {
						item.getListeners().add(InventoryPrefixListeners.getChangePrefixListener());
					}
				} else {
					ItemMeta meta = item.getItemMeta();
					meta.setLore(Arrays.asList("", UtilsPlayerManager.PREFIX_SUBRANK_TERM_TAG + subRank.getColoredName()));
					item.setItemMeta(meta);
					item.getListeners().add(InventoryPrefixListeners.getLockedPrefixListener());
				}
				// On ajoute chaque préfixe au début de l'inventaire
				inventoryItemMap.put(i, item);
				i++;
			}
		}
		// On ajoute l'item représentant le préfixe par défaut (qui correspond au grade
		// du joueur en jeu tel que Vagabond)
		ItemInteractive defaultPrefixItem = new ItemInteractive(ItemUtils.generateItem(Material.WORKBENCH, 1, (short) 0,
				UtilsPlayerManager.PREFIX_DEFAULT_ITEM_NAME, LORE_ACTIVATION_INFO));

		if (prefixType.equalsIgnoreCase(UtilsPlayerManager.PREFIX_DEFAULT_TYPE)) {
			ItemMeta meta = defaultPrefixItem.getItemMeta();
			meta.setLore(LORE_ACTUAL_PREFIX_INFO);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			defaultPrefixItem.setItemMeta(meta);
			defaultPrefixItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			defaultPrefixItem.getListeners().add(InventoryPrefixListeners.getAlreadyHasPrefixListener());
		} else {
			defaultPrefixItem.getListeners().add(InventoryPrefixListeners.getChangePrefixListener());
		}
		ItemMeta defaultPrefixMeta = defaultPrefixItem.getItemMeta();
		List<String> lore = defaultPrefixMeta.getLore();
		lore.set(1, UtilsPlayerManager.PREFIX_ACTIVATION_INFO_LEFT_TAG);
		lore.add(UtilsPlayerManager.PREFIX_MORE_INFO_RIGHT_TAG);
		defaultPrefixMeta.setLore(lore);
		defaultPrefixItem.setItemMeta(defaultPrefixMeta);
		defaultPrefixItem.getListeners().add(InventoryPrefixListeners.getOpenDefaultPrefixesInventoryListener());

		// On ajoute l'item représentant le préfixe VIP correspondant à celui possédé
		// par le joueur, sinon un bloc de verre
		ItemInteractive vipPrefixItem = new ItemInteractive(ItemUtils.generateItem(Material.GLASS, 1, (short) 0,
				getPrefixItemName(SubRank.VIP.getPrefix().toString(), UtilsPlayerManager.PREFIX_UNAVAILABLE_TAG),
				Arrays.asList("", UtilsPlayerManager.PREFIX_SUBRANK_TERM_TAG + SubRank.VIP.getColoredName())));

		if (subRanks.contains(SubRank.VIP_PLUS)) {
			vipPrefixItem.setType(Material.DIAMOND);
			ItemMeta meta = vipPrefixItem.getItemMeta();
			meta.setDisplayName(getPrefixItemName(SubRank.VIP_PLUS.getPrefix().toString(), UtilsPlayerManager.PREFIX_AVAILABLE_TAG));
			vipPrefixItem.setItemMeta(meta);
		} else if (subRanks.contains(SubRank.VIP)) {
			vipPrefixItem.setType(Material.EMERALD);
			ItemMeta meta = vipPrefixItem.getItemMeta();
			meta.setDisplayName(getPrefixItemName(SubRank.VIP.getPrefix().toString(), UtilsPlayerManager.PREFIX_AVAILABLE_TAG));
			vipPrefixItem.setItemMeta(meta);
		}
		if (subRanks.contains(SubRank.VIP_PLUS) || subRanks.contains(SubRank.VIP)) {
			if (prefixType.equalsIgnoreCase(SubRank.VIP.name())
					|| prefixType.equalsIgnoreCase(SubRank.VIP_PLUS.name())) {
				ItemMeta meta = vipPrefixItem.getItemMeta();
				meta.setDisplayName(meta.getDisplayName().replace(UtilsPlayerManager.PREFIX_AVAILABLE_TAG, ""));
				meta.setLore(LORE_ACTUAL_PREFIX_INFO);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				vipPrefixItem.setItemMeta(meta);
				vipPrefixItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				vipPrefixItem.getListeners().add(InventoryPrefixListeners.getAlreadyHasPrefixListener());
			} else {
				vipPrefixItem.getListeners().add(InventoryPrefixListeners.getChangePrefixListener());
			}
		} else {
			vipPrefixItem.getListeners().add(InventoryPrefixListeners.getLockedPrefixListener());
		}
		inventoryItemMap.put(21, defaultPrefixItem);
		inventoryItemMap.put(23, vipPrefixItem);
		inventoryItemMap.put(26, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}

	/**
	 * @param prefix
	 *            Le préfixe, non null
	 * @param tag
	 *            Le tag, peut être null
	 * @return Le nom d'item comportant le préfixe et éventuellement un tag, non
	 *         null
	 */
	static String getPrefixItemName(String prefix, String tag) {
		return UtilsPlayerManager.PREFIX_ITEM_NAME + prefix + (tag == null ? "" : " " + tag);
	}
}
