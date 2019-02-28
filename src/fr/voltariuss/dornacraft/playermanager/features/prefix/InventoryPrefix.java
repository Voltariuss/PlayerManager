package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryUtils;
import fr.voltariuss.dornacraft.api.inventories.ItemInteractive;
import fr.voltariuss.dornacraft.api.items.ItemUtils;
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

	public static final String DEFAULT_PREFIX_ITEM_NAME = "§ePréfixe par défaut";
	public static final String UNAVALAIBLE_TAG = "§c§lIndisponible";
	public static final String AVALAIBLE_TAG = "§a§lDisponible";

	private static final List<String> CLICK_INFO_ACTIVATION = Arrays.asList("", "§7Clique pour activer");
	private static final List<String> ACTUAL_PREFIX_INFO = Arrays.asList("", "§a§lPréfixe actuel");

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
						getPrefixItemName(subRank.getPrefix().toString(), UNAVALAIBLE_TAG), CLICK_INFO_ACTIVATION));

				if (subRanks.contains(subRank)) {
					// Si le joueur possède la sous-rang, alors on modifie l'item
					item.setType(subRank.getMaterial());
					item.setDurability((short) 0);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(meta.getDisplayName().replace(UNAVALAIBLE_TAG, AVALAIBLE_TAG));
					item.setItemMeta(meta);

					if (prefixType.equalsIgnoreCase(subRank.name())) {
						// Si il s'agit du préfixe utilisé par le joueur, alors on lui ajoute une
						// disctinction
						meta.setDisplayName(meta.getDisplayName().replace(AVALAIBLE_TAG, ""));
						meta.setLore(ACTUAL_PREFIX_INFO);
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						item.setItemMeta(meta);
						item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
						item.getListeners().add(InventoryPrefixListeners.getAlreadyHasPrefixListener());
					} else {
						item.getListeners().add(InventoryPrefixListeners.getChangePrefixListener());
					}
				} else {
					ItemMeta meta = item.getItemMeta();
					meta.setLore(getLockedPrefixInfoLore(getSubRankTermString(subRank)));
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
				DEFAULT_PREFIX_ITEM_NAME, CLICK_INFO_ACTIVATION));

		if (prefixType.equalsIgnoreCase("DEFAULT")) {
			ItemMeta meta = defaultPrefixItem.getItemMeta();
			meta.setLore(ACTUAL_PREFIX_INFO);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			defaultPrefixItem.setItemMeta(meta);
			defaultPrefixItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			defaultPrefixItem.getListeners().add(InventoryPrefixListeners.getAlreadyHasPrefixListener());
		} else {
			defaultPrefixItem.getListeners().add(InventoryPrefixListeners.getChangePrefixListener());
		}
		ItemMeta defaultPrefixMeta = defaultPrefixItem.getItemMeta();
		List<String> lore = defaultPrefixMeta.getLore();
		lore.set(1, lore.get(1).replace("Clique", "Clic-gauche"));
		lore.add("§7Clic-droit pour plus d'infos");
		defaultPrefixMeta.setLore(lore);
		defaultPrefixItem.setItemMeta(defaultPrefixMeta);
		defaultPrefixItem.getListeners().add(InventoryPrefixListeners.getOpenDefaultPrefixesInventoryListener());

		// On ajoute l'item représentant le préfixe VIP correspondant à celui possédé
		// par le joueur, sinon un bloc de verre
		ItemInteractive vipPrefixItem = new ItemInteractive(ItemUtils.generateItem(Material.GLASS, 1, (short) 0,
				getPrefixItemName(SubRank.VIP.getPrefix().toString(), UNAVALAIBLE_TAG),
				getLockedPrefixInfoLore(getSubRankTermString(SubRank.VIP))));

		if (subRanks.contains(SubRank.VIP_PLUS)) {
			vipPrefixItem.setType(Material.DIAMOND);
			ItemMeta meta = vipPrefixItem.getItemMeta();
			meta.setDisplayName(getPrefixItemName(SubRank.VIP_PLUS.getPrefix().toString(), AVALAIBLE_TAG));
			vipPrefixItem.setItemMeta(meta);
		} else if (subRanks.contains(SubRank.VIP)) {
			vipPrefixItem.setType(Material.EMERALD);
			ItemMeta meta = vipPrefixItem.getItemMeta();
			meta.setDisplayName(getPrefixItemName(SubRank.VIP.getPrefix().toString(), AVALAIBLE_TAG));
			vipPrefixItem.setItemMeta(meta);
		}
		if (subRanks.contains(SubRank.VIP_PLUS) || subRanks.contains(SubRank.VIP)) {
			if (prefixType.equalsIgnoreCase(SubRank.VIP.name())
					|| prefixType.equalsIgnoreCase(SubRank.VIP_PLUS.name())) {
				ItemMeta meta = vipPrefixItem.getItemMeta();
				meta.setDisplayName(meta.getDisplayName().replace(AVALAIBLE_TAG, ""));
				meta.setLore(ACTUAL_PREFIX_INFO);
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
		return "§6Préfixe : " + prefix + (tag == null ? "" : " " + tag);
	}

	/**
	 * @param subRank
	 *            Le {@link SubRank}, non null
	 * @return La condition d'avoir le sous-rang spécifié sous la forme d'une chaîne
	 *         de caractères, non null
	 */
	private static String getSubRankTermString(SubRank subRank) {
		return "§7Sous-rang : " + subRank.getColoredName();
	}

	/**
	 * @param term
	 *            La condition
	 * @return La description de la condition, non null
	 */
	private static List<String> getLockedPrefixInfoLore(String term) {
		return Arrays.asList("", term);
	}
}
