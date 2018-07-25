package fr.voltariuss.dornacraftplayermanager.features.prefix;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraftapi.FeatureManager;
import fr.voltariuss.dornacraftapi.inventories.InteractiveInventory;
import fr.voltariuss.dornacraftapi.inventories.InventoryItem;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftapi.items.ItemUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.SQLAccount;
import fr.voltariuss.dornacraftplayermanager.features.subrank.SubRank;

public class PrefixManager extends FeatureManager {
	
	private final SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();

	public PrefixManager(CommandSender sender) {
		super(sender);
	}
	
	public void setPrefix(OfflinePlayer player, Prefix prefix) {
		
	}
	
	public void removePrefix(OfflinePlayer player) {
		
	}
	
	public void openSetPrefixInventory(OfflinePlayer player) throws Exception {
		if(this.getSender() instanceof Player) {
			Player p = (Player) this.getSender();
			InteractiveInventory interactiveInventory = new InteractiveInventory(this.getInventoryItemMap(player), 27, player.getName(), this);
			interactiveInventory.openInventory(p);
		} else {
			this.sendErrorMessage(Utils.getMustBeAPlayerMessage());
		}
	}
	
	public HashMap<Integer, InventoryItem> getInventoryItemMap(OfflinePlayer player) throws Exception {
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		
		for(SubRank subRank : SubRank.values()) {
			
		}
		
		ItemStack it = ItemUtils.generateItem(Material.WORKBENCH, 1, (short) 0, "§ePréfixe par défaut", Arrays.asList("", "§7Clique pour activer"));
		
		if(sqlAccount.getPrefixType(player).equalsIgnoreCase("DEFAULT")) {
			ItemMeta meta = it.getItemMeta();
			meta.setLore(Arrays.asList("", "§aPréfixe actuel"));
			it.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		InventoryItem defaultPrefix = new InventoryItem(ItemUtils.generateItem(Material.WORKBENCH, 1, (short) 0, "§ePréfixe par défaut", Arrays.asList("", "§7Clique pour activer")));
		inventoryItemMap.put(22, new InventoryItem(ItemUtils.generateItem(Material.WORKBENCH, 1, (short) 0, "§ePréfixe par défaut", Arrays.asList("", "§7Clique pour activer"))));
		inventoryItemMap.put(26, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
}
