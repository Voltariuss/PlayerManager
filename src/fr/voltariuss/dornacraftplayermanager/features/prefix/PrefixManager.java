package fr.voltariuss.dornacraftplayermanager.features.prefix;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fr.dornacraft.cache.PlayerCache;
import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraftapi.inventories.InteractiveInventory;
import fr.voltariuss.dornacraftapi.inventories.InventoryItem;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftapi.items.ItemUtils;
import fr.voltariuss.dornacraftapi.utils.ErrorMessage;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.AccountManager;
import fr.voltariuss.dornacraftplayermanager.features.level.LevelManager;
import fr.voltariuss.dornacraftplayermanager.features.level.SQLLevel;
import fr.voltariuss.dornacraftplayermanager.features.rank.Rank;
import fr.voltariuss.dornacraftplayermanager.features.rank.RankManager;
import fr.voltariuss.dornacraftplayermanager.features.subrank.SQLSubRank;
import fr.voltariuss.dornacraftplayermanager.features.subrank.SubRank;

public class PrefixManager {
	
	//Messages d'erreur
	public static final String ALREADY_HAS_PREFIX = "Préfixe déjà possédé.";
	public static final String DONT_HAS_SUBRANK = "Vous ne possédez pas le sous-rang : %§c.";
	public static final String LOCKED_PREFIX = "Préfixe vérouillé.";
	
	//Autres messages
	public static final String CURRENT_PREFIX = "§ePréfixe actuel : %";
	public static final String SUCCESS_CHANGE_PREFIX = "§aPréfixe modifié avec succès !";
	
	//Menu
	public static final String CLICK_RIGHT_INFO = "§7Clic-droit pour plus d'infos";
	public static final String DEFAULT_PREFIX_ITEM_NAME = "§ePréfixe par défaut";
	public static final String PREFIX_ITEM_NAME = "§6Préfixe : %%";
	public static final String SUBRANK_TERM = "§7Sous-rang : %";
	public static final String UNAVALAIBLE_TAG = " §c§lIndisponible";
	public static final String AVALAIBLE_TAG = " §a§lDisponible";
	
	private static final List<String> CLICK_INFO_ACTIVATION = Arrays.asList("", "§7Clique pour activer");
	private static final List<String> ACTUAL_PREFIX_INFO = Arrays.asList("", "§a§lPréfixe actuel");
	private static final List<String> LOCKED_PREFIX_INFO = Arrays.asList("", "§cRequis : %");
	private static final List<String> REQUIRED_LEVEL_INFO = Arrays.asList("", "§eNiveau requis : %");
	
	/**
	 * Récupère le préfixe du joueur dans la mémoire centrale si il est connecté,
	 * dans la base de données sinon.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return Le préfixe du joueur, non null
	 * @throws SQLException 
	 */
	public static String getPrefixType(OfflinePlayer player) throws SQLException {
		String prefixType = Prefix.getDefault();
		
		if(player.isOnline()) {
			prefixType = PlayerCacheManager.getPlayerCacheMap().get(player.getUniqueId()).getPrefixType();
		} else {
			prefixType = SQLPrefixType.getPrefixType(player);
		}
		return prefixType;
	}
	
	/**
	 * Modifie le type de préfixe du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param player Le joueur ciblé, non null
	 * @param prefixType Le nouveau type de préfixe du joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void setPrefixType(CommandSender sender, OfflinePlayer player, String prefixType) throws SQLException {
		SQLPrefixType.setPrefixType(player, prefixType);
		//Actualise le type de préfixe du joueur ciblé dans la mémoire centrale
		if(player.isOnline()) {
			PlayerCacheManager.getPlayerCacheMap().get(player.getUniqueId()).setPrefixType(prefixType);
		}
		
		if(sender != null) {
			sendSuccessChangePrefixMessage(sender, Prefix.fromString(prefixType, RankManager.getRank(player), LevelManager.getLevel(player)).toString());
		}
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête contenant le préfixe actuel du joueur ciblé. 
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param prefix Le préfixe du joueur ciblé sous la forme d'une chaîne de caractères, non null
	 */
	public static void sendCurrentPrefixMessage(CommandSender sender, String prefix) {
		sender.sendMessage(CURRENT_PREFIX.replaceFirst("%", prefix));
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête annonçant le succès du changement de préfixe du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param prefix Le préfixe du joueur ciblé sous la forme d'une chaîne de caractères, non null
	 */
	public static void sendSuccessChangePrefixMessage(CommandSender sender, String prefix) {
		sender.sendMessage(SUCCESS_CHANGE_PREFIX);
		sendCurrentPrefixMessage(sender, prefix);
	}
	
	/**
	 * Envoie un message d'erreur à l'émetteur de la requête spécifiant la non-possession du sous-rang associé au préfixe souhaité.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param subRank Le sous-rang associé au préfixe souhaité, non null
	 */
	public static void sendDontHasSubRankMessage(CommandSender sender, SubRank subRank) {
		Utils.sendErrorMessage(sender, DONT_HAS_SUBRANK.replaceFirst("%", subRank.getColoredName()));
	}
	
	/**
	 * Ouvre l'inventaire de gestion des préfixes du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param target La joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void openSetPrefixInventory(CommandSender sender, OfflinePlayer target) throws SQLException {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.getOpenInventory() != null) {
				player.closeInventory();
			}
			InteractiveInventory interactiveInventory = new InteractiveInventory(getSetPrefixInventoryItemMap(target), 27, target.getName());
			interactiveInventory.openInventory(player);			
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.MUST_BE_A_PLAYER);
		}
	}
	
	/**
	 * Créer et retourne les items constituant l'inventaire à créer.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return La liste des items indexé par leur position dans l'inventaire à créer, non null
	 * @throws SQLException
	 */
	public static HashMap<Integer, InventoryItem> getSetPrefixInventoryItemMap(OfflinePlayer player) throws SQLException {
		String prefixType = SQLPrefixType.getPrefixType(player);
		ArrayList<SubRank> subRanks = SQLSubRank.getSubRanks(player);
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		int i = 0;
		
		InventoryItemInteractListener setPrefix = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				if(event.getInventoryItem().getType() != Material.WORKBENCH || (event.getInventoryItem().getType() == Material.WORKBENCH && event.getClick() == ClickType.LEFT)) {
					Player sender = event.getPlayer();
					try {
						InteractiveInventory interactiveInventory = event.getInteractiveInventory();
						InventoryItem inventoryItem = event.getInventoryItem();
						OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
						String title = inventoryItem.getItemMeta().getDisplayName();
						
						for(SubRank subRank : SubRank.values()) {		
							if(title.contains(subRank.getPrefix().toString())) {
								PlayerCache playerCache = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId());
								
								if(playerCache.getSubRanks().contains(subRank) || playerCache.getRank() == Rank.ADMINISTRATEUR) {
									PrefixManager.setPrefixType(sender, target, subRank.getPrefix().name());								
								} else {
									PrefixManager.sendDontHasSubRankMessage(sender, subRank);
								}
							}
						}
						if(title.equalsIgnoreCase(DEFAULT_PREFIX_ITEM_NAME)) {
							PrefixManager.setPrefixType(sender, target, Prefix.getDefault());
						}
						PrefixManager.openSetPrefixInventory(sender, target);
					} catch (Exception e) {
						Utils.sendErrorMessage(sender, ErrorMessage.EXCEPTION_MESSAGE);
						e.printStackTrace();
					}	
				}
			}
		};
		
		InventoryItemInteractListener openDefaultsPrefixs = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Player sender = event.getPlayer();
				
				if(event.getClick() == ClickType.RIGHT) {
					try {
						InteractiveInventory interactiveInventory = event.getInteractiveInventory();
						OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
						PrefixManager.openDefaultsPrefixsInventory(sender, target);
					} catch (Exception e) {
						Utils.sendErrorMessage(sender, ErrorMessage.EXCEPTION_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		};
		
		InventoryItemInteractListener alreadyHasPrefix = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Utils.sendErrorMessage(event.getPlayer(), ALREADY_HAS_PREFIX);
			}
		};
		
		InventoryItemInteractListener lockedPrefix = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Utils.sendErrorMessage(event.getPlayer(), PrefixManager.LOCKED_PREFIX);
			}
		};
		
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
						item.getListeners().add(setPrefix);
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
			defaultPrefixItem.getListeners().add(setPrefix);
		}
		ItemMeta defaultPrefixMeta = defaultPrefixItem.getItemMeta();
		List<String> lore = defaultPrefixMeta.getLore();
		lore.set(1, lore.get(1).replace("Clique", "Clic-gauche"));
		lore.add(CLICK_RIGHT_INFO);
		defaultPrefixMeta.setLore(lore);
		defaultPrefixItem.setItemMeta(defaultPrefixMeta);
		defaultPrefixItem.getListeners().add(openDefaultsPrefixs);
		
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
				vipPrefixItem.getListeners().add(setPrefix);
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
	 * Ouvre l'inventaire vitrine des préfixes par défaut en fonction des données du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param target La joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void openDefaultsPrefixsInventory(CommandSender sender, OfflinePlayer target) throws SQLException {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.getOpenInventory() != null) {
				player.closeInventory();
			}
			InteractiveInventory interactiveInventory = new InteractiveInventory(getDefaultsPrefixsInventoryItemMap(target), 27, target.getName());
			interactiveInventory.openInventory(player);			
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.MUST_BE_A_PLAYER);
		}
	}
	
	/**
	 * Créer et retourne les items constituant l'inventaire à créer.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return La liste des items indexé par leur position dans l'inventaire à créer, non null
	 * @throws SQLException
	 */
	public static HashMap<Integer, InventoryItem> getDefaultsPrefixsInventoryItemMap(OfflinePlayer player) throws SQLException {
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		int i = 0;
		
		for(Prefix prefix : Prefix.values()) {
			if(prefix.getRequieredLevel() > 0) {
				int requiredLevel = prefix.getRequieredLevel();
				inventoryItemMap.put(i, new InventoryItem(ItemUtils.generateItem(prefix.getMaterial(), 1, (short) 0, getPrefixItemName(prefix.toString()),
						getRequiredLevelInfoLore((SQLLevel.getLevel(player) >= requiredLevel ? "§a" : "§c") + Integer.toString(requiredLevel)))));
				i++;
			} else {
				break;
			}
		}
		
		InventoryItemInteractListener back = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Player sender = event.getPlayer();
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					openSetPrefixInventory(sender, target);
				} catch(Exception e) {
					Utils.sendErrorMessage(sender, ErrorMessage.EXCEPTION_MESSAGE);
					e.printStackTrace();
				}
			}
		};
		
		inventoryItemMap.put(25, InventoryUtils.getBackItem(back));
		inventoryItemMap.put(26, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
	
	public static String getPrefixItemName(String prefix) {
		return PREFIX_ITEM_NAME.replaceFirst("%%", prefix);
	}
	
	public static String getPrefixItemName(String prefix, String tag) {
		return PREFIX_ITEM_NAME.replaceFirst("%", prefix).replaceFirst("%", tag);
	}
	
	public static String getSubRankTermString(SubRank subRank) {
		return SUBRANK_TERM.replaceFirst("%", subRank.getColoredName());
	}
	
	public static List<String> getLockedPrefixInfoLore(String condition) {
		ArrayList<String> lores = new ArrayList<>();
		lores.add(LOCKED_PREFIX_INFO.get(0));
		lores.add(LOCKED_PREFIX_INFO.get(1).replaceFirst("%", condition));
		return lores;
	}
	
	public static List<String> getRequiredLevelInfoLore(String requiredLevel) {
		ArrayList<String> lores = new ArrayList<>();
		lores.add(REQUIRED_LEVEL_INFO.get(0));
		lores.add(REQUIRED_LEVEL_INFO.get(1).replaceFirst("%", requiredLevel));
		return lores;
	}
}
