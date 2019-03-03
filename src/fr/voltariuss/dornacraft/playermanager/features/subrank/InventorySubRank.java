package fr.voltariuss.dornacraft.playermanager.features.subrank;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraft.api.MessageLevel;
import fr.voltariuss.dornacraft.api.UtilsAPI;
import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryUtils;
import fr.voltariuss.dornacraft.api.inventories.ItemInteractive;
import fr.voltariuss.dornacraft.api.items.ItemUtils;
import fr.voltariuss.dornacraft.playermanager.UtilsPlayerManager;

/**
 * Classe de d�finition de l'inventaire de gestion des sous-rangs d'un joueur
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class InventorySubRank {

	private static final List<String> LORE_AWARDING_INFO = Arrays.asList("", UtilsPlayerManager.SUBRANK_AWARDING_TAG);

	/**
	 * Ouvre l'inventaire de gestion des sous-rangs du joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, non null
	 * @param target
	 *            La joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void openInventory(CommandSender sender, OfflinePlayer target) throws SQLException {
		if (sender instanceof Player) {
			InteractiveInventory inventory = new InteractiveInventory(getInventoryItemMap(target), 9, target.getName(),
					false);
			inventory.openInventory((Player) sender);
		} else {
			UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.CONSOLE_NOT_ALLOWED);
		}
	}

	/**
	 * Cr�er et retourne les items constituant l'inventaire � cr�er.
	 * 
	 * @param player
	 *            Le joueur cibl�, non null
	 * @return La liste des items index� par leur position dans l'inventaire �
	 *         cr�er, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static HashMap<Integer, ItemInteractive> getInventoryItemMap(OfflinePlayer player) throws SQLException {
		HashMap<Integer, ItemInteractive> inventoryItemMap = new HashMap<>();
		ArrayList<SubRank> subRanks = SubRankManager.getSubRanks(player);
		int amount = 1;

		ArrayList<ItemInteractive> items = new ArrayList<>();
		items.add(new ItemInteractive(ItemUtils.generateItem(Material.EMERALD, amount, (short) 0,
				UtilsPlayerManager.SUBRANK_ITEM_NAME + SubRank.VIP.getColoredName(), LORE_AWARDING_INFO)));
		items.add(new ItemInteractive(ItemUtils.generateItem(Material.DIAMOND, amount, (short) 0,
				UtilsPlayerManager.SUBRANK_ITEM_NAME + SubRank.VIP_PLUS.getColoredName(), LORE_AWARDING_INFO)));
		items.add(new ItemInteractive(ItemUtils.generateItem(Material.BOOK_AND_QUILL, amount, (short) 0,
				UtilsPlayerManager.SUBRANK_ITEM_NAME + SubRank.EDITOR.getColoredName(), LORE_AWARDING_INFO)));
		items.add(new ItemInteractive(ItemUtils.generateItem(Material.GRASS, amount, (short) 0,
				UtilsPlayerManager.SUBRANK_ITEM_NAME + SubRank.BUILDER.getColoredName(), LORE_AWARDING_INFO)));
		items.add(new ItemInteractive(ItemUtils.generateItem(Material.REDSTONE_COMPARATOR, amount, (short) 0,
				UtilsPlayerManager.SUBRANK_ITEM_NAME + SubRank.DEVELOPER.getColoredName(), LORE_AWARDING_INFO)));

		for (int i = 0; i < items.size(); i++) {
			ItemInteractive item = items.get(i);
			ItemMeta meta = item.getItemMeta();
			Iterator<SubRank> iterator = subRanks.iterator();
			boolean trouve = false;

			while (iterator.hasNext() && !trouve) {
				SubRank subRank = iterator.next();

				if (meta.getDisplayName().contains(subRank.getColoredName())) {
					meta.setLore(Arrays.asList("", UtilsPlayerManager.SUBRANK_REMOVING_TAG));
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
					item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					item.getListeners().add(InventorySubRankListeners.getRemoveSubRankListener());
					trouve = true;
				}
			}

			if (!trouve) {
				item.getListeners().add(InventorySubRankListeners.getAddSubRankListener());
			}
			inventoryItemMap.put(i, item);
		}
		inventoryItemMap.put(8, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
}
