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
 * Classe de d�finition de l'inventaire des pr�fixes d'un joueur
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class InventoryPrefix {

	public static final String DEFAULT_PREFIX_ITEM_NAME = "�ePr�fixe par d�faut";
	public static final String UNAVALAIBLE_TAG = "�c�lIndisponible";
	public static final String AVALAIBLE_TAG = "�a�lDisponible";

	private static final List<String> CLICK_INFO_ACTIVATION = Arrays.asList("", "�7Clique pour activer");
	private static final List<String> ACTUAL_PREFIX_INFO = Arrays.asList("", "�a�lPr�fixe actuel");

	/**
	 * Ouvre l'inventaire de gestion des pr�fixes du joueur cibl�.
	 * 
	 * @param player
	 *            Le joueur r�cepteur de l'inventaire, non null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void openInventory(Player player, OfflinePlayer target) throws SQLException {
		InteractiveInventory interactiveInventory = new InteractiveInventory(getInventoryItemMap(target), 27,
				target.getName(), false);
		interactiveInventory.openInventory(player);
	}

	/**
	 * Cr�er et retourne les items constituant l'inventaire � cr�er.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return La liste des items index� par leur position dans l'inventaire �
	 *         cr�er, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static HashMap<Integer, ItemInteractive> getInventoryItemMap(OfflinePlayer target) throws SQLException {
		String prefixType = SQLPrefixType.getPrefixType(target);
		ArrayList<SubRank> subRanks = SubRankManager.getSubRanks(target);
		HashMap<Integer, ItemInteractive> inventoryItemMap = new HashMap<>();
		int i = 0;

		// On ajoute tous les pr�fixes associ�s aux sous-rangs
		for (SubRank subRank : SubRank.values()) {
			// A l'exclusion des sous-rangs VIP qui seront trait�s diff�rement plus bas
			if (subRank != SubRank.VIP && subRank != SubRank.VIP_PLUS) {
				// On cr�er l'item par d�faut d'un sous-rang (une poche d'encre)
				ItemInteractive item = new ItemInteractive(ItemUtils.generateItem(Material.INK_SACK, 1, (short) 8,
						getPrefixItemName(subRank.getPrefix().toString(), UNAVALAIBLE_TAG), CLICK_INFO_ACTIVATION));

				if (subRanks.contains(subRank)) {
					// Si le joueur poss�de la sous-rang, alors on modifie l'item
					item.setType(subRank.getMaterial());
					item.setDurability((short) 0);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(meta.getDisplayName().replace(UNAVALAIBLE_TAG, AVALAIBLE_TAG));
					item.setItemMeta(meta);

					if (prefixType.equalsIgnoreCase(subRank.name())) {
						// Si il s'agit du pr�fixe utilis� par le joueur, alors on lui ajoute une
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
				// On ajoute chaque pr�fixe au d�but de l'inventaire
				inventoryItemMap.put(i, item);
				i++;
			}
		}
		// On ajoute l'item repr�sentant le pr�fixe par d�faut (qui correspond au grade
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
		lore.add("�7Clic-droit pour plus d'infos");
		defaultPrefixMeta.setLore(lore);
		defaultPrefixItem.setItemMeta(defaultPrefixMeta);
		defaultPrefixItem.getListeners().add(InventoryPrefixListeners.getOpenDefaultPrefixesInventoryListener());

		// On ajoute l'item repr�sentant le pr�fixe VIP correspondant � celui poss�d�
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
	 *            Le pr�fixe, non null
	 * @param tag
	 *            Le tag, peut �tre null
	 * @return Le nom d'item comportant le pr�fixe et �ventuellement un tag, non
	 *         null
	 */
	static String getPrefixItemName(String prefix, String tag) {
		return "�6Pr�fixe : " + prefix + (tag == null ? "" : " " + tag);
	}

	/**
	 * @param subRank
	 *            Le {@link SubRank}, non null
	 * @return La condition d'avoir le sous-rang sp�cifi� sous la forme d'une cha�ne
	 *         de caract�res, non null
	 */
	private static String getSubRankTermString(SubRank subRank) {
		return "�7Sous-rang : " + subRank.getColoredName();
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
