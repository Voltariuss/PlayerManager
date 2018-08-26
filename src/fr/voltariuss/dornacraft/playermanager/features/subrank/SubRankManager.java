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

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.inventories.InteractiveInventory;
import fr.voltariuss.dornacraft.api.inventories.InventoryItem;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraft.api.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraft.api.inventories.InventoryUtils;
import fr.voltariuss.dornacraft.api.items.ItemUtils;
import fr.voltariuss.dornacraft.api.utils.ErrorMessage;
import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.AccountManager;
import fr.voltariuss.dornacraft.playermanager.features.permission.PermissionManager;
import fr.voltariuss.dornacraft.playermanager.features.prefix.Prefix;
import fr.voltariuss.dornacraft.playermanager.features.prefix.PrefixManager;

public class SubRankManager {

	//Messages d'erreur
	public static final String UNKNOW_SUBRANK = "Le sous-rang spécifié est incorrect.";
	public static final String HAS_ALREADY_SUBRANK = "Ce joueur possède déjà le sous-rang spécifié.";
	public static final String DONT_HAS_SPECIFIED_SUBRANK = "Ce joueur ne possède pas le sous-rang spécifié.";
	public static final String DONT_HAS_SUBRANK = "Ce joueur ne possède pas de sous-rang.";
	
	//Autres messages
	public static final String SUCCESS_SUBRANK_ADD = "§aLe sous-rang §6% §aa bien été attribué au joueur §b%§a.";
	public static final String SUCCESS_SUBRANK_REMOVE = "§aLe sous-rang §6% §aa bien été retiré au joueur §b%§a.";
	public static final String SUCCESS_REMOVEALL_SUBRANKS = "§aTous les sous-rangs ont été retirés au joueur §b%§a.";
	public static final String LIST_SUBRANK = "§6Liste des sous-rangs du joueur §b% §6: %";
	
	//Menus
	public static final String INFO_ADD_SUBRANK = "§e§lClique pour attribuer ce sous-rang";
	public static final String INFO_REMOVE_SUBRANK = "§e§lClique pour retirer ce sous-rang";
	
	public static final List<String> loresInfoAdd = Arrays.asList("", INFO_ADD_SUBRANK);
	public static final List<String> loresInfoRemove = Arrays.asList("", INFO_REMOVE_SUBRANK);
	
	/**
	 * Récupère et retourne la liste des sous-rangs du joueur ciblé.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return La liste des sous-rangs du joueur ciblé
	 * @throws SQLException 
	 */
	public static ArrayList<SubRank> getSubRanks(OfflinePlayer player) throws SQLException {
		ArrayList<SubRank> subRanks = new ArrayList<>();
		
		if(player.isOnline()) {
			subRanks = PlayerCacheManager.getPlayerCacheMap().get(player.getUniqueId()).getSubRanks();
		} else {
			subRanks = SQLSubRank.getSubRanks(player);
		}
		return subRanks;
	}
	
	/**
	 * Ajoute le sous-rang spécifié au joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param player Le joueur ciblé, non null
	 * @param subRank Le sous-rang à ajouter au joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void addSubRank(CommandSender sender, OfflinePlayer player, SubRank subRank) throws SQLException {
		boolean hasAlreadySubRank = hasSubRank(player, subRank);
		
		if(!hasAlreadySubRank) {
			boolean terms = subRank == SubRank.VIP_PLUS && !hasSubRank(player, SubRank.VIP);
			
			if(terms) {
				SQLSubRank.addSubRank(player, SubRank.VIP);
			}
			SQLSubRank.addSubRank(player, subRank);
			//Actualisation des sous-rangs du joueur dans la mémoire centrale
			if(player.isOnline()) {
				PlayerCacheManager.getPlayerCacheMap().get(player.getUniqueId()).getSubRanks().add(subRank);
				
				if(terms) {
					PlayerCacheManager.getPlayerCacheMap().get(player.getUniqueId()).getSubRanks().add(SubRank.VIP);
				}
				PermissionManager.updatePermissions((Player) player);
			}
		}
		
		if(sender != null) {
			if(!hasAlreadySubRank) {				
				sendSuccessSubRankAddMessage(sender, subRank.getName(), player.getName());
			} else {
				Utils.sendErrorMessage(sender, HAS_ALREADY_SUBRANK);				
			}
		}
	}
	
	/**
	 * Retire le sous-rang spécifié au joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param player Le joueur ciblé, non null
	 * @param subRank Le sous-rang à retirer au joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void removeSubRank(CommandSender sender, OfflinePlayer player, SubRank subRank) throws SQLException {
		boolean hasAlreadySubRank = hasSubRank(player, subRank);
		
		if(hasAlreadySubRank) {
			boolean terms = subRank == SubRank.VIP && hasSubRank(player, SubRank.VIP_PLUS);
			
			if(terms) {
				SQLSubRank.removeSubRank(player, SubRank.VIP_PLUS);					
			}
			SQLSubRank.removeSubRank(player, subRank);
			//Actualisation des sous-rangs du joueur dans la mémoire centrale
			if(player.isOnline()) {
				PlayerCacheManager.getPlayerCacheMap().get(player.getUniqueId()).getSubRanks().remove(subRank);
				
				if(terms) {
					PlayerCacheManager.getPlayerCacheMap().get(player.getUniqueId()).getSubRanks().remove(SubRank.VIP_PLUS);
				}
				PermissionManager.updatePermissions((Player) player);
			}
			
			if(PrefixManager.getPrefixType(player).equalsIgnoreCase(subRank.getPrefix().name())) {
				PrefixManager.setPrefixType(null, player, Prefix.getDefault());
			}
		}
		
		if(sender != null) {
			if(hasAlreadySubRank) {				
				sendSuccessSubRankRemoveMessage(sender, subRank.getName(), player.getName());
			} else {
				Utils.sendErrorMessage(sender, DONT_HAS_SPECIFIED_SUBRANK);			
			}
		}
	}
	
	/**
	 * Retire tous les sous-rangs du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, peut être null
	 * @param player Le joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void removeAllSubRank(CommandSender sender, OfflinePlayer player) throws SQLException {
		boolean hasSubRank = hasSubRank(player);
		
		if(hasSubRank) {
			SQLSubRank.removeAllSubRanks(player);
		}
		
		if(sender != null) {
			if(hasSubRank) {
				sendSuccessRemoveAllSubRanksMessage(sender, player.getName());
				//Actualisation des sous-rangs du joueur dans la mémoire centrale
				if(player.isOnline()) {
					PlayerCacheManager.getPlayerCacheMap().get(player.getUniqueId()).getSubRanks().clear();
					PermissionManager.updatePermissions((Player) player);
				}
				
				for(SubRank subRank : SubRank.values()) {
					if(PrefixManager.getPrefixType(player).equalsIgnoreCase(subRank.getPrefix().name())) {
						PrefixManager.setPrefixType(null, player, Prefix.getDefault());
						break;
					}
				}
			} else {
				Utils.sendErrorMessage(sender, DONT_HAS_SUBRANK);				
			}
		}
	}
	
	/**
	 * Vérifie si le joueur possède le sous-rang spécifié.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @param subRank Le sous-rang à vérifier, non null
	 * @return "vrai" si le joueur possède le sous-rang, "faux" sinon
	 * @throws SQLException
	 */
	public static boolean hasSubRank(OfflinePlayer player, SubRank subRank) throws SQLException {
		return getSubRanks(player).contains(subRank);
	}
	
	/**
	 * Vérifie si le joueur possède ou moins un sous-rang.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return "vrai" si le joueur ciblé possède ou moins un sous-rang, "faux" sinon
	 * @throws SQLException
	 */
	public static boolean hasSubRank(OfflinePlayer player) throws SQLException {
		return !getSubRanks(player).isEmpty();
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête comportant la liste des sous-rangs du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param player Le joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void sendListSubRankMessage(CommandSender sender, OfflinePlayer player) throws SQLException {
		ArrayList<SubRank> subRanks = getSubRanks(player);
		boolean hasSubRank = !subRanks.isEmpty();
		
		if(hasSubRank) {
			String strSubRanks = "";
			
			for(SubRank subRank : subRanks) {
				strSubRanks += "\n§f - " + subRank.getColoredName();
			}
			sendPlayerListSubRanksMessage(sender, player.getName(), strSubRanks);
		} else {
			Utils.sendErrorMessage(sender, DONT_HAS_SUBRANK);							
		}
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête annonçant le succès de l'ajout du sous-rang au joueur spécifié.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param subRankName Le nom du sous-rang ajouté au joueur spécifié, non null
	 * @param playerName Le nom du joueur ciblé, non null
	 */
	public static void sendSuccessSubRankAddMessage(CommandSender sender, String subRankName, String playerName) {
		sender.sendMessage(SUCCESS_SUBRANK_ADD.replaceFirst("%", subRankName).replaceFirst("%", playerName));
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête annonçant le succès de la suppression du sous-rang au joueur spécifié.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param subRankName Le nom du sous-rang retiré au joueur spécifié, non null
	 * @param playerName Le nom du joueur spécifié, non null
	 */
	public static void sendSuccessSubRankRemoveMessage(CommandSender sender, String subRankName, String playerName) {
		sender.sendMessage(SUCCESS_SUBRANK_REMOVE.replaceFirst("%", subRankName).replaceFirst("%", playerName));
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête annonçant le succès de la suppression de toutes les sous-rangs du joueur spécifié.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param playerName Le nom du joueur ciblé, non null
	 */
	public static void sendSuccessRemoveAllSubRanksMessage(CommandSender sender, String playerName) {
		sender.sendMessage(SUCCESS_REMOVEALL_SUBRANKS.replaceFirst("%", playerName));
	}
	
	/**
	 * Envoie un message à l'émetteur de la requête comportant la liste des sous-rangs du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param playerName Le nom du joueur ciblé, non null
	 * @param strSubRanks La liste des sous-rangs du joueur spécifié sous la forme d'une chaîne de caractères, non null
	 */
	public static void sendPlayerListSubRanksMessage(CommandSender sender, String playerName, String strSubRanks) {
		sender.sendMessage(LIST_SUBRANK.replaceFirst("%", playerName).replaceFirst("%", strSubRanks));
	}
	
	/**
	 * Ouvre l'inventaire de gestion des sous-rangs du joueur ciblé.
	 * 
	 * @param sender L'émetteur de la requête, non null
	 * @param target La joueur ciblé, non null
	 * @throws SQLException
	 */
	public static void openSetSubRankInventory(CommandSender sender, OfflinePlayer target) throws SQLException {
		if(sender instanceof Player) {
			InteractiveInventory inventory = new InteractiveInventory(getInventoryItemMap(target), 9, target.getName(), false);
			inventory.openInventory((Player) sender);	
		} else {
			Utils.sendErrorMessage(sender, ErrorMessage.NOT_FOR_CONSOLE);
		}
	}
	
	/**
	 * Créer et retourne les items constituant l'inventaire à créer.
	 * 
	 * @param player Le joueur ciblé, non null
	 * @return La liste des items indexé par leur position dans l'inventaire à créer, non null
	 * @throws SQLException
	 */
	public static HashMap<Integer, InventoryItem> getInventoryItemMap(OfflinePlayer player) throws SQLException {
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		ArrayList<SubRank> subRanks = SQLSubRank.getSubRanks(player);
		String name = "§cSous-rang: ";
		int amount = 1;
		
		InventoryItemInteractListener addSubRank = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Player sender = event.getPlayer();
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(SubRank subRank : SubRank.values()) {
						if(title.contains(subRank.getColoredName())) {
							SubRankManager.addSubRank(sender, target, subRank);
						}
					}
					SubRankManager.openSetSubRankInventory(sender, target);
				} catch (Exception e) {
					Utils.sendErrorMessage(sender, ErrorMessage.EXCEPTION_MESSAGE);
					e.printStackTrace();
				}
			}
		};
		
		InventoryItemInteractListener removeSubRank = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Player sender = event.getPlayer();
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(SubRank subRank : SubRank.values()) {
						if(title.contains(subRank.getColoredName())) {
							SubRankManager.removeSubRank(sender, target, subRank);
						}
					}
					SubRankManager.openSetSubRankInventory(sender, target);
				} catch (Exception e) {
					Utils.sendErrorMessage(sender, ErrorMessage.EXCEPTION_MESSAGE);
					e.printStackTrace();
				}
			}
		};
		
		ArrayList<InventoryItem> items = new ArrayList<>();
		items.add(new InventoryItem(ItemUtils.generateItem(Material.EMERALD, amount, (short) 0, name + SubRank.VIP.getColoredName(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.DIAMOND, amount, (short) 0, name + SubRank.VIP_PLUS.getColoredName(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.GRASS, amount, (short) 0, name + SubRank.ARCHITECTE.getColoredName(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.REDSTONE_COMPARATOR, amount, (short) 0, name + SubRank.DEVELOPPEUR.getColoredName(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.BOOK_AND_QUILL, amount, (short) 0, name + SubRank.REDACTEUR.getColoredName(), loresInfoAdd)));
		
		for(int i = 0; i < items.size(); i++) {
			InventoryItem item = items.get(i);
			ItemMeta meta = item.getItemMeta();
			Iterator<SubRank> iterator = subRanks.iterator();
			boolean trouve = false;
			
			while(iterator.hasNext() && !trouve) {
				SubRank subRank = iterator.next();
				
				if(meta.getDisplayName().contains(subRank.getColoredName())) {
					meta.setLore(loresInfoRemove);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
					item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					item.getListeners().add(removeSubRank);
					trouve = true;
				}
			}
			
			if(!trouve) {
				item.getListeners().add(addSubRank);
			}
			inventoryItemMap.put(i, item);
		}
		inventoryItemMap.put(8, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
}
