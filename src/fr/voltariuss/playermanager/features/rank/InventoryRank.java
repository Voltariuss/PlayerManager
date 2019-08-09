package fr.voltariuss.playermanager.features.rank;

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

import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.simpledevapi.inventories.InteractiveInventory;
import fr.voltariuss.simpledevapi.inventories.InventoryUtils;
import fr.voltariuss.simpledevapi.inventories.ItemInteractive;
import fr.voltariuss.simpledevapi.items.ItemUtils;

public final class InventoryRank {

	private static final List<String> LORE_AWARDING_RANK_INFO = Arrays.asList("", UtilsPlayerManager.RANK_AWARDING_TAG);

	/**
	 * Ouvre l'inventaire de gestion des rangs du joueur ciblé au joueur récepteur
	 * spécifié.
	 * 
	 * @param player
	 *            Le joueur récepteur de l'inventaire, non null
	 * @param target
	 *            Le joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static void openInventory(Player player, OfflinePlayer target) throws SQLException {
		InteractiveInventory inventory = new InteractiveInventory(getInventoryItemMap(target), 9, target.getName(),
				false);
		inventory.openInventory(player);
	}

	/**
	 * Créer et retourne les items constituant l'inventaire à créer.
	 * 
	 * @param target
	 *            Le joueur ciblé, non null
	 * @return La liste des items indexés par leur position dans l'inventaire à
	 *         créer, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static HashMap<Integer, ItemInteractive> getInventoryItemMap(OfflinePlayer target) throws SQLException {
		HashMap<Integer, ItemInteractive> inventoryItemMap = new HashMap<>();
		Rank rank = SQLRank.getRank(target);
		Material type = Material.STAINED_CLAY;
		int amount = 1;

		ArrayList<ItemInteractive> items = new ArrayList<>();
		items.add(new ItemInteractive(
				ItemUtils.generateItem(type, amount, (short) 9, UtilsPlayerManager.RANK_ITEM_NAME + Rank.PLAYER.getColoredName(), LORE_AWARDING_RANK_INFO)));
		items.add(new ItemInteractive(
				ItemUtils.generateItem(type, amount, (short) 11, UtilsPlayerManager.RANK_ITEM_NAME + Rank.HELPER.getColoredName(), LORE_AWARDING_RANK_INFO)));
		items.add(new ItemInteractive(
				ItemUtils.generateItem(type, amount, (short) 1, UtilsPlayerManager.RANK_ITEM_NAME + Rank.MODERATOR.getColoredName(), LORE_AWARDING_RANK_INFO)));
		items.add(new ItemInteractive(ItemUtils.generateItem(type, amount, (short) 14,
				UtilsPlayerManager.RANK_ITEM_NAME + Rank.ADMIN.getColoredName(), LORE_AWARDING_RANK_INFO)));

		for (int i = 0; i < items.size(); i++) {
			ItemInteractive item = items.get(i);
			ItemMeta meta = item.getItemMeta();

			if (meta.getDisplayName().contains(rank.getColoredName())) {
				meta.setLore(Arrays.asList("", UtilsPlayerManager.RANK_ACTUAL_TAG));
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				item.getListeners().add(InventoryRankListeners.getAlreadyHasRangListener());
			} else {
				item.getListeners().add(InventoryRankListeners.getChangeRankListener());
			}
			inventoryItemMap.put(i, item);
		}
		inventoryItemMap.put(8, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
}
