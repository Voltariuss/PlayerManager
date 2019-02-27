package fr.voltariuss.dornacraft.playermanager.features.rank;

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

/**
 * Classe de définition de l'inventaire du rang du joueur correspondant
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class InventoryRank {

	private static final List<String> LORE_INFO = Arrays.asList("", "§e§lClique pour attribuer ce rang");

	/**
	 * Ouvre l'inventaire de gestion des rangs du joueur ciblé au joueur recepteur
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
	 * @return La liste des items indexé par leur position dans l'inventaire à
	 *         créer, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static HashMap<Integer, ItemInteractive> getInventoryItemMap(OfflinePlayer target) throws SQLException {
		HashMap<Integer, ItemInteractive> inventoryItemMap = new HashMap<>();
		Rank rank = SQLRank.getRank(target);
		String name = "§cRang: ";
		Material type = Material.STAINED_CLAY;
		int amount = 1;

		ArrayList<ItemInteractive> items = new ArrayList<>();
		items.add(new ItemInteractive(
				ItemUtils.generateItem(type, amount, (short) 9, name + Rank.JOUEUR.getColoredName(), LORE_INFO)));
		items.add(new ItemInteractive(
				ItemUtils.generateItem(type, amount, (short) 11, name + Rank.GUIDE.getColoredName(), LORE_INFO)));
		items.add(new ItemInteractive(
				ItemUtils.generateItem(type, amount, (short) 1, name + Rank.MODERATEUR.getColoredName(), LORE_INFO)));
		items.add(new ItemInteractive(ItemUtils.generateItem(type, amount, (short) 14,
				name + Rank.ADMINISTRATEUR.getColoredName(), LORE_INFO)));

		for (int i = 0; i < items.size(); i++) {
			ItemInteractive item = items.get(i);
			ItemMeta meta = item.getItemMeta();

			if (meta.getDisplayName().contains(rank.getColoredName())) {
				meta.setLore(Arrays.asList("", "§c§lRang possédé par le joueur"));
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
