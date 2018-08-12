package fr.voltariuss.dornacraftplayermanager.features.rank;

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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fr.dornacraft.cache.DornacraftCache;
import fr.voltariuss.dornacraftapi.inventories.InteractiveInventory;
import fr.voltariuss.dornacraftapi.inventories.InventoryItem;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftapi.items.ItemUtils;
import fr.voltariuss.dornacraftapi.utils.ErrorMessage;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.AccountManager;
import fr.voltariuss.dornacraftplayermanager.features.permission.PermissionManager;

public class RankManager {
		
	//Messages d'erreur
	public static final String HAS_HIGHEST_RANK = "�cLe joueur poss�de d�j� le rang le plus �lev�.";
	public static final String HAS_LOWER_RANK = "�cLe joueur poss�de d�j� le rang le plus bas.";
	public static final String ALREADY_HAS_RANK = "�cLe joueur poss�de d�j� ce rang.";
	
	//Autres messages
	public static final String CHANGE_RANK_SUCCESS = "�aLe rang du joueur �b% �aa �t� modifi� avec succ�s !";
	public static final String RANK_INFO = "�6Rang du joueur �b% �6: ";
	
	//Menus
	public static final String INFO_CHANGE_RANK = "�e�lClique pour attribuer ce rang";
	public static final String WARNING_ALREADY_HAS_RANK = "�c�lRang poss�d� par le joueur";
	
	public static final List<String> loresInfo = Arrays.asList("", INFO_CHANGE_RANK);
	public static final List<String> loresWarning = Arrays.asList("", WARNING_ALREADY_HAS_RANK);
	
	/**
	 * R�cup�re le rank du joueur dans la m�moire centrale si il est connect�,
	 * dans la base de donn�es sinon.
	 * 
	 * @param player Le joueur cibl�, non null
	 * @return Le rank du joueur cibl�, non null
	 * @throws SQLException
	 */
	public static Rank getRank(OfflinePlayer player) throws SQLException {
		Rank rank = Rank.getDefault();
		
		if(player.isOnline()) {
			rank = DornacraftCache.getPlayerCacheMap().get(player.getUniqueId()).getRank();
		} else {
			rank = SQLRank.getRank(player);
		}
		return rank;
	}
	
	/**
	 * D�finit le rang du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param player Le joueur cibl�, non null
	 * @param rank Le rang � d�finir, non null
	 * @throws SQLException
	 */
	public static void setRank(CommandSender sender, OfflinePlayer player, Rank rank) throws SQLException {
		Rank playerRank = getRank(player);
		boolean hasAlreadyRank = playerRank != rank;
		
		if(!hasAlreadyRank) {
			SQLRank.setRank(player, rank);
			
			if(player.isOnline()) {
				//Actualise le rang du joueur dans la m�moire centrale si il est connect�
				DornacraftCache.getPlayerCacheMap().get(player.getUniqueId()).setRank(rank);
				//Actualise les permissions du joueur
				PermissionManager.updatePermissions(player.getPlayer());
			}
			
			if(rank == Rank.MODERATEUR || rank == Rank.ADMINISTRATEUR) {
				
				
				
				
				
				//PrefixManager.setPrefixType(player, Prefix.getDefaultPrefixType());
			}
		}
		
		if(sender != null) {
			if(!hasAlreadyRank) {
				sendChangeRankSuccessMessage(sender, player.getName(), rank);
			} else {
				Utils.sendErrorMessage(sender, ALREADY_HAS_RANK);							
			}				
		}
	}
	
	/**
	 * Retire le rang du joueur cibl� et lui attribut celui par d�faut.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param player Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void removeRank(CommandSender sender, OfflinePlayer player) throws SQLException {
		Rank playerRank = getRank(player);
		boolean isDefaultRank = playerRank != Rank.getDefault();
		
		if(!isDefaultRank) {
			setRank(sender, player, Rank.getDefault());
		} else if(sender != null) {
			Utils.sendErrorMessage(sender, HAS_LOWER_RANK);
		}
	}
	
	/**
	 * Promeut le joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param player Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void promote(CommandSender sender, OfflinePlayer player) throws SQLException {
		Rank playerRank = getRank(player);
		boolean hasHigherRank = playerRank == Rank.values()[Rank.values().length - 1];
		
		if(!hasHigherRank) {
			setRank(sender, player, Rank.fromPower(playerRank.getPower() + 1));
		} else if(sender != null) {
			Utils.sendErrorMessage(sender, HAS_HIGHEST_RANK);
		}
	}
	
	/**
	 * R�trograde le joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, peut �tre null
	 * @param player Le joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void demote(CommandSender sender, OfflinePlayer player) throws SQLException {
		Rank playerRank = getRank(player);
		boolean hasLowerRank = playerRank == Rank.getDefault();
		
		if(!hasLowerRank) {
			setRank(sender, player, Rank.fromPower(playerRank.getPower() - 1));
		} else if(sender != null) {
			Utils.sendErrorMessage(sender, HAS_LOWER_RANK);
		}
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te annon�ant le succ�s du changement du rang du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param playerName Le nom du joueur cibl�, non null
	 */
	public static void sendChangeRankSuccessMessage(CommandSender sender, String playerName, Rank rank) {
		sender.sendMessage(CHANGE_RANK_SUCCESS.replace("%", playerName));
		sendRankInfoMessage(sender, playerName, rank);
	}
	
	/**
	 * Envoie un message � l'�metteur de la requ�te comportant le rang du joueur cibl�.
	 * 
	 * @param sender L'�metteur de la requ�te, non null
	 * @param player Le joueur cibl�, non null
	 * @param rank Le rang du joueur, non null
	 */
	public static void sendRankInfoMessage(CommandSender sender, String playerName, Rank rank) {
		sender.sendMessage(RANK_INFO.replace("%", playerName) + rank.getColoredName());
	}
	
	/**
	 * Ouvre l'inventaire de gestion des rangs du joueur cibl�.
	 * 
	 * @param player Le joueur � ouvrir l'inventaire cr��, non null
	 * @param target La joueur cibl�, non null
	 * @throws SQLException
	 */
	public static void openSetRankInventory(Player player, OfflinePlayer target) throws SQLException {
		if(player.getOpenInventory() != null) {
			player.closeInventory();
		}
		InteractiveInventory inventory = new InteractiveInventory(getInventoryItemMap(target), 9, target.getName());
		inventory.openInventory(player);
	}
	
	/**
	 * Cr�er et retourne les items constituant l'inventaire � cr�er.
	 * 
	 * @param player Le joueur cibl�, non null
	 * @return La liste des items index� par leur position dans l'inventaire � cr�er, non null
	 * @throws SQLException
	 */
	public static HashMap<Integer, InventoryItem> getInventoryItemMap(OfflinePlayer player) throws SQLException {
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		Rank rank = SQLRank.getRank(player);
		String name = "�cRang: ";
		Material type = Material.STAINED_CLAY;
		int amount = 1;
		
		InventoryItemInteractListener setRank = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Player player = event.getPlayer();
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					OfflinePlayer target = AccountManager.getOfflinePlayer(interactiveInventory.getInventory().getName());
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(Rank rank : Rank.values()) {
						if(title.contains(rank.getColoredName())) {
							setRank(player, target, rank);
						}
					}
					openSetRankInventory(player, target);
				} catch (Exception e) {
					Utils.sendErrorMessage(player, ErrorMessage.EXCEPTION_MESSAGE);
					e.printStackTrace();
				}
			}
		};
		
		InventoryItemInteractListener alreadyHasRank = new InventoryItemInteractListener() {
			
			@Override	
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				Utils.sendErrorMessage(event.getPlayer(), ALREADY_HAS_RANK);
			}
		};
		
		ArrayList<InventoryItem> items = new ArrayList<>();
		items.add(new InventoryItem(ItemUtils.generateItem(type, amount, (short) 9, name + Rank.JOUEUR.getColoredName(), loresInfo)));
		items.add(new InventoryItem(ItemUtils.generateItem(type, amount, (short) 11, name + Rank.GUIDE.getColoredName(), loresInfo)));
		items.add(new InventoryItem(ItemUtils.generateItem(type, amount, (short) 1, name + Rank.MODERATEUR.getColoredName(), loresInfo)));
		items.add(new InventoryItem(ItemUtils.generateItem(type, amount, (short) 14, name + Rank.ADMINISTRATEUR.getColoredName(), loresInfo)));
		
		for(int i = 0; i < items.size(); i++) {
			InventoryItem item = items.get(i);
			ItemMeta meta = item.getItemMeta();
			
			if(meta.getDisplayName().contains(rank.getColoredName())) {
				meta.setLore(loresWarning);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				item.getListeners().add(alreadyHasRank);
			} else {
				item.getListeners().add(setRank);
			}
			inventoryItemMap.put(i, item);
		}		
		inventoryItemMap.put(8, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
}
