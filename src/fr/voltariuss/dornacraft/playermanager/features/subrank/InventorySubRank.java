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

import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryUtils;
import fr.voltariuss.dornacraft.api.items.ItemInteractive;
import fr.voltariuss.dornacraft.api.items.ItemUtils;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;

public final class InventorySubRank {
	
	private static final List<String> LORE_INFO_ADD = Arrays.asList("", "§e§lClique pour attribuer ce sous-rang");

	/**
	 * Ouvre l'inventaire de gestion des sous-rangs du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param target La joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void openInventory(CommandSender sender, OfflinePlayer target) throws SQLException {
		if(sender instanceof Player) {
			InteractiveInventory inventory = new InteractiveInventory(getInventoryItemMap(target), 9, target.getName(), false);
			inventory.openInventory((Player) sender);	
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.PLAYER_ONLINE_ONLY);
		}
	}
	
	/**
	 * Créer et retourne les items constituant l'inventaire à créer.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return La liste des items indexé par leur position dans l'inventaire à créer, non null
	 * @throws SQLException
	 */
	public static HashMap<Integer, ItemInteractive> getInventoryItemMap(OfflinePlayer player) throws SQLException {
		HashMap<Integer, ItemInteractive> inventoryItemMap = new HashMap<>();
		ArrayList<SubRank> subRanks = SubRankManager.getSubRanks(player);
		String name = "§cSous-rang: ";
		int amount = 1;
		
		ArrayList<ItemInteractive> items = new ArrayList<>();
		items.add(new ItemInteractive(ItemUtils.generateItem(Material.EMERALD, amount, (short) 0, name + SubRank.VIP.getColoredName(), LORE_INFO_ADD)));
		items.add(new ItemInteractive(ItemUtils.generateItem(Material.DIAMOND, amount, (short) 0, name + SubRank.VIP_PLUS.getColoredName(), LORE_INFO_ADD)));
		items.add(new ItemInteractive(ItemUtils.generateItem(Material.GRASS, amount, (short) 0, name + SubRank.ARCHITECTE.getColoredName(), LORE_INFO_ADD)));
		items.add(new ItemInteractive(ItemUtils.generateItem(Material.REDSTONE_COMPARATOR, amount, (short) 0, name + SubRank.DEVELOPPEUR.getColoredName(), LORE_INFO_ADD)));
		items.add(new ItemInteractive(ItemUtils.generateItem(Material.BOOK_AND_QUILL, amount, (short) 0, name + SubRank.REDACTEUR.getColoredName(), LORE_INFO_ADD)));
		
		for(int i = 0; i < items.size(); i++) {
			ItemInteractive item = items.get(i);
			ItemMeta meta = item.getItemMeta();
			Iterator<SubRank> iterator = subRanks.iterator();
			boolean trouve = false;
			
			while(iterator.hasNext() && !trouve) {
				SubRank subRank = iterator.next();
				
				if(meta.getDisplayName().contains(subRank.getColoredName())) {
					meta.setLore(Arrays.asList("", "§e§lClique pour retirer ce sous-rang"));
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
					item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					item.getListeners().add(InventorySubRankListeners.getRemoveSubRankListener());
					trouve = true;
				}
			}
			
			if(!trouve) {
				item.getListeners().add(InventorySubRankListeners.getAddSubRankListener());
			}
			inventoryItemMap.put(i, item);
		}
		inventoryItemMap.put(8, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
}
