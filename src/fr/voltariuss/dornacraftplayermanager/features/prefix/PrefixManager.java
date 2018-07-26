package fr.voltariuss.dornacraftplayermanager.features.prefix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraftapi.FeatureManager;
import fr.voltariuss.dornacraftapi.cmds.CommandUtils;
import fr.voltariuss.dornacraftapi.inventories.InteractiveInventory;
import fr.voltariuss.dornacraftapi.inventories.InventoryItem;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftapi.items.ItemUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.SQLAccount;
import fr.voltariuss.dornacraftplayermanager.cache.playercache.PlayerCache;
import fr.voltariuss.dornacraftplayermanager.features.rank.Rank;
import fr.voltariuss.dornacraftplayermanager.features.subrank.SQLSubRank;
import fr.voltariuss.dornacraftplayermanager.features.subrank.SubRank;

public class PrefixManager extends FeatureManager {
	
	private final SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	private final SQLSubRank sqlSubRank = DornacraftPlayerManager.getInstance().getSQLSubRank();
	
	//Messages d'erreur
	public static final String ALREADY_HAS_PREFIX = "§cPréfixe déjà possédé.";
	public static final String DONT_HAS_SUBRANK = "§cVous ne possédez pas le sous-rang : %§c.";
	public static final String LOCKED_PREFIX = "§cPréfixe vérouillé.";
	
	//Autres messages
	public static final String CURRENT_PREFIX = "§ePréfixe actuel : %";
	public static final String SUCCESS_CHANGE_PREFIX = "§aPréfixe modifié avec succès !";
	
	//Menu
	public static final String DEFAULT_PREFIX_ITEM_NAME = "§ePréfixe par défaut";
	public static final String PREFIX_ITEM_NAME = "§6Préfixe : %%";
	public static final String UNAVALAIBLE_TAG = " §c§lIndisponible";
	public static final String AVALAIBLE_TAG = " §a§lDisponible";
	
	private final List<String> CLICK_INFO_ACTIVATION = Arrays.asList("", "§7Clique pour activer");
	private final List<String> ACTUAL_PREFIX_INFO = Arrays.asList("", "§a§lPréfixe actuel");
	private final List<String> LOCKED_PREFIX_INFO = Arrays.asList("", "§cRequis : %");

	public PrefixManager(CommandSender sender) {
		super(sender);
	}
	
	public void setPrefixType(OfflinePlayer player, String prefixType) throws Exception {
		Rank rank = sqlAccount.getRank(player);
		int level = sqlAccount.getLevel(player);
		Prefix prefix = Prefix.fromString(prefixType, rank, level);
		sqlAccount.setPrefixType(player, prefixType);
		
		this.sendSuccessChangePrefixMessage(prefix.toString());
	}
	
	public void sendCurrentPrefixMessage(String prefix) {
		this.sendMessage(CURRENT_PREFIX.replaceFirst("%", prefix));
	}
	
	public void sendSuccessChangePrefixMessage(String prefix) {
		this.sendMessage(SUCCESS_CHANGE_PREFIX);
		this.sendCurrentPrefixMessage(prefix);
	}
	
	public void sendDontHasSubRankMessage(SubRank subRank) {
		this.sendErrorMessage(DONT_HAS_SUBRANK.replaceFirst("%", subRank.getSubRankColorName()));
	}
	
	public void openSetPrefixInventory(OfflinePlayer player) throws Exception {
		if(this.getSender() instanceof Player) {
			Player p = (Player) this.getSender();
			
			if(p.getOpenInventory() != null) {
				p.closeInventory();
			}
			InteractiveInventory interactiveInventory = new InteractiveInventory(this.getInventoryItemMap(player), 27, player.getName(), this);
			interactiveInventory.openInventory(p);
		} else {
			this.sendErrorMessage(Utils.getMustBeAPlayerMessage());
		}
	}
	
	public HashMap<Integer, InventoryItem> getInventoryItemMap(OfflinePlayer player) throws Exception {
		String prefixType = sqlAccount.getPrefixType(player);
		ArrayList<SubRank> subRanks = sqlSubRank.getSubRanks(player);
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		int i = 0;
		
		InventoryItemInteractListener setPrefix = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				try {
					PrefixManager prefixManager = (PrefixManager) event.getFeatureManager();
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					UUID uuid = sqlAccount.getUUIDOfPlayer(interactiveInventory.getInventory().getName());
					Player player = event.getPlayer();
					OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
					String title = inventoryItem.getItemMeta().getDisplayName();

					for(SubRank subRank : SubRank.values()) {		
						if(title.contains(subRank.getPrefix().toString())) {
							PlayerCache playerCache = DornacraftPlayerManager.getInstance().getPlayerCacheMap().get(player.getUniqueId());
							
							if(playerCache.getSubRanks().contains(subRank) || playerCache.getRank() == Rank.ADMINISTRATEUR) {
								prefixManager.setPrefixType(target, subRank.getPrefix().name());								
							} else {
								prefixManager.sendDontHasSubRankMessage(subRank);
							}
						}
					}
					if(title.equalsIgnoreCase(DEFAULT_PREFIX_ITEM_NAME)) {
						prefixManager.setPrefixType(target, Prefix.getDefault());
					}
					prefixManager.openSetPrefixInventory(target);
				} catch (Exception e) {
					event.getPlayer().sendMessage(Utils.getExceptionMessage());
					e.printStackTrace();
				}
			}
		};
		
		InventoryItemInteractListener alreadyHasPrefix = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				CommandUtils.sendErrorMessage(event.getPlayer(), ALREADY_HAS_PREFIX);
			}
		};
		
		InventoryItemInteractListener lockedPrefix = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				CommandUtils.sendErrorMessage(event.getPlayer(), PrefixManager.LOCKED_PREFIX);
			}
		};
		
		for(SubRank subRank : SubRank.values()) {
			if(subRank != SubRank.VIP && subRank != SubRank.VIP_PLUS) {
				InventoryItem item = new InventoryItem(ItemUtils.generateItem(Material.INK_SACK, 1, (short) 8, this.getPrefixItemName(subRank.getPrefix().toString(), UNAVALAIBLE_TAG), CLICK_INFO_ACTIVATION));
				
				if(subRanks.contains(subRank)) {
					item.setType(subRank.getMaterial());
					item.setDurability((short) 0);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(meta.getDisplayName().replace(UNAVALAIBLE_TAG, AVALAIBLE_TAG));
					
					if(prefixType.equalsIgnoreCase(subRank.name())) {
						meta.setDisplayName(meta.getDisplayName().replace(AVALAIBLE_TAG, ""));
						meta.setLore(ACTUAL_PREFIX_INFO);
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
						item.addInventoryItemListener(alreadyHasPrefix);
					} else {
						item.addInventoryItemListener(setPrefix);
					}
					item.setItemMeta(meta);
				} else {
					ItemMeta meta = item.getItemMeta();
					meta.setLore(this.getLockedPrefixInfoLore("§7Sous-rang " + subRank.getSubRankColorName()));
					item.setItemMeta(meta);
					item.addInventoryItemListener(lockedPrefix);
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
			defaultPrefixItem.addInventoryItemListener(alreadyHasPrefix);
		} else {
			defaultPrefixItem.addInventoryItemListener(setPrefix);
		}
		InventoryItem vipPrefixItem = new InventoryItem(ItemUtils.generateItem(Material.GLASS, 1, (short) 0, this.getPrefixItemName(SubRank.VIP.getPrefix().toString(), UNAVALAIBLE_TAG), CLICK_INFO_ACTIVATION));
		
		if(subRanks.contains(SubRank.VIP_PLUS)) {
			vipPrefixItem.setType(Material.DIAMOND);
			ItemMeta meta = vipPrefixItem.getItemMeta();
			meta.setDisplayName(this.getPrefixItemName(SubRank.VIP_PLUS.getPrefix().toString(), AVALAIBLE_TAG));
			vipPrefixItem.setItemMeta(meta);
		} else if(subRanks.contains(SubRank.VIP)) {
			vipPrefixItem.setType(Material.EMERALD);
			ItemMeta meta = vipPrefixItem.getItemMeta();
			meta.setDisplayName(this.getPrefixItemName(SubRank.VIP.getPrefix().toString(), AVALAIBLE_TAG));
			vipPrefixItem.setItemMeta(meta);
		}
		if(prefixType.equalsIgnoreCase(SubRank.VIP.name()) || prefixType.equalsIgnoreCase(SubRank.VIP_PLUS.name())) {
			ItemMeta meta = vipPrefixItem.getItemMeta();
			meta.setDisplayName(meta.getDisplayName().replace(UNAVALAIBLE_TAG, ""));
			meta.setLore(ACTUAL_PREFIX_INFO);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			vipPrefixItem.setItemMeta(meta);
			vipPrefixItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			vipPrefixItem.addInventoryItemListener(alreadyHasPrefix);
		} else {
			vipPrefixItem.addInventoryItemListener(setPrefix);
		}
		inventoryItemMap.put(21, defaultPrefixItem);
		inventoryItemMap.put(23, vipPrefixItem);
		inventoryItemMap.put(26, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
	
	public String getPrefixItemName(String prefix, String tag) {
		return PREFIX_ITEM_NAME.replaceFirst("%", prefix).replaceFirst("%", tag);
	}
	
	public List<String> getLockedPrefixInfoLore(String condition) {
		ArrayList<String> lores = new ArrayList<>();
		lores.add(LOCKED_PREFIX_INFO.get(0));
		lores.add(LOCKED_PREFIX_INFO.get(1).replaceFirst("%", condition));
		return lores;
	}
}
