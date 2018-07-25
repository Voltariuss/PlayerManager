package fr.voltariuss.dornacraftplayermanager.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fr.voltariuss.dornacraftapi.cmds.CustomCommand;
import fr.voltariuss.dornacraftapi.cmds.SubCommand;
import fr.voltariuss.dornacraftapi.inventories.InteractiveInventory;
import fr.voltariuss.dornacraftapi.inventories.InventoryItem;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractEvent;
import fr.voltariuss.dornacraftapi.inventories.InventoryItemInteractListener;
import fr.voltariuss.dornacraftapi.inventories.InventoryUtils;
import fr.voltariuss.dornacraftapi.items.ItemUtils;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.SubRank;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;
import fr.voltariuss.dornacraftplayermanager.sql.SQLSubRank;

public class CmdSubRank extends CustomCommand implements CommandExecutor {
	
	//Instances
	private SQLAccount sqlAccount = DornacraftPlayerManager.getInstance().getSQLAccount();
	private SQLSubRank sqlSubRank = DornacraftPlayerManager.getInstance().getSQLSubRank(); 
	
	//Messages d'erreur
	public static final String UNKNOW_SUBRANK = "Le sous-rang spécifié est incorrect.";
	public static final String HAS_SUBRANK = "Ce joueur possède déjà le sous-rang spécifié.";
	public static final String DONT_HAS_SPECIFIED_SUBRANK = "Ce joueur ne possède pas le sous-rang spécifié.";
	public static final String DONT_HAS_SUBRANK = "Ce joueur ne possède pas de sous-rang.";
	
	//Arguments
	public static final String ARG_SET = "set";
	public static final String ARG_REMOVEALL = "removeall";
	public static final String ARG_LIST = "list";
	
	//Messages d'aide sur les commandes
	public static final String MSG_SUBRANK_SET = "§ePour définir les sous-rangs d'un joueur:\n §6/subrank set §b<joueur>";
	public static final String MSG_SUBRANK_REMOVEALL = "§ePour retirer tous les sous-rangs d'un joueur:\n §6/subrank removeall §b<joueur>";
	public static final String MSG_SUBRANK_LIST = "§ePour voir la liste des sous-rangs d'un joueur:\n §6/subrank list §b<joueur>";
	
	//Menus
	public static final String INFO_ADD_SUBRANK = "§e§lClique pour attribuer ce sous-rang";
	public static final String INFO_REMOVE_SUBRANK = "§e§lClique pour retirer ce sous-rang";
	
	List<String> loresInfoAdd = Arrays.asList("", INFO_ADD_SUBRANK);
	List<String> loresInfoRemove = Arrays.asList("", INFO_REMOVE_SUBRANK);
	
	public CmdSubRank(String cmdLabel) {
		super(cmdLabel, DornacraftPlayerManager.getInstance());
		this.getSubCommands().add(new SubCommand(ARG_SET, "Pour définir les sous-rangs d'un joueur:\n §6/subrank set §b<joueur>", 1));
		this.getSubCommands().add(new SubCommand(ARG_REMOVEALL, "Pour retirer tous les sous-rangs d'un joueur:\n §6/subrank removeall §b<joueur>", 2));
		this.getSubCommands().add(new SubCommand(ARG_LIST, "Pour voir la liste des sous-rangs d'un joueur:\n §6/subrank list §b<joueur>", 3));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		super.setSender(sender);
		
		if(sender.hasPermission(this.getPrimaryPermission())) {
			try {
				if(args.length == 0) {
					this.sendDescriptionCommandMessage();
				} else if(args.length == 1) {
					for(int i = 0; i < this.getSubCommands().size(); i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands().get(i).getArg())) {
							if(sender.hasPermission(this.getSubCommands().get(i).getPermission())) {
								sender.sendMessage(this.getSubCommands().get(i).getHelpMessage());
							} else {
								this.sendLakePermissionMessage();
							}
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendHelpMessage();
					} else {
						this.sendWrongCommandMessage();
					}
				} else if(args.length == 2) {
					UUID uuid = sqlAccount.getUUIDOfPlayer(args[1]);
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					
					if(player != null) {
						if(args[0].equalsIgnoreCase("set")) {
							if(sender.hasPermission(this.getSubCommand(ARG_SET).getPermission())) {
								this.openSetSubRankInventory(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("removeall")) {
							if(sender.hasPermission(this.getSubCommand(ARG_REMOVEALL).getPermission())) {
								this.removeAllSubRank(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("list")) {
							if(sender.hasPermission(this.getSubCommand(ARG_LIST).getPermission())) {
								this.sendListSubRank(player);
							} else {
								this.sendLakePermissionMessage();
							}
						} else if(args[0].equalsIgnoreCase("help")) {
							this.sendTooManyArgumentsMessage();
						} else {
							this.sendWrongCommandMessage();
						}
					} else {
						this.sendUnknowPlayerMessage();
					}
				} else {
					for(int i = 0; i < this.getSubCommands().size(); i++) {
						if(args[0].equalsIgnoreCase(this.getSubCommands().get(i).getArg())) {
							this.sendTooManyArgumentsMessage();
							return true;
						}
					}
					
					if(args[0].equalsIgnoreCase("help")) {
						this.sendTooManyArgumentsMessage();
					} else {
						this.sendWrongCommandMessage();
					}
				}	
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(Utils.getExceptionMessage());
			}	
		} else {
			this.sendLakePermissionMessage();
		}
		return true;
	}
	
	public void addSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		if(!sqlSubRank.hasSubRank(player, subRank)) {
			sqlSubRank.addSubRank(player, subRank);
			this.sendMessage("§aLe sous-rang §6" + subRank.getName() + " §aa bien été attribué au joueur §b" + player.getName() + "§a.");
		} else {
			this.sendErrorMessage(HAS_SUBRANK);
		}
	}
	
	public void removeSubRank(OfflinePlayer player, SubRank subRank) throws Exception {
		if(sqlSubRank.hasSubRank(player, subRank)) {
			sqlSubRank.removeSubRank(player, subRank);
			this.sendMessage("§aLe sous-rang §6" + subRank.getName() + " §aa bien été retiré au joueur §b" + player.getName() + "§a.");
		} else {
			this.sendErrorMessage(DONT_HAS_SPECIFIED_SUBRANK);
		}
	}
	
	public void removeAllSubRank(OfflinePlayer player) throws Exception {
		if(sqlSubRank.hasSubRank(player)) {
			sqlSubRank.removeAllSubRanks(player);
			this.sendMessage("§aTous les sous-rangs ont été retirés au joueur §b" + player.getName() + "§a.");
		} else {
			this.sendErrorMessage(DONT_HAS_SUBRANK);
		}
	}
	
	public void sendListSubRank(OfflinePlayer player) throws Exception {
		ArrayList<SubRank> subRanks = sqlSubRank.getSubRanks(player);
		
		if(sqlSubRank.hasSubRank(player)) {
			this.sendMessage("§6Liste des sous-rangs du joueur §b" + player.getName() + " §6:");
			
			while(!subRanks.isEmpty()) {
				SubRank subRank = subRanks.get(subRanks.size() - 1);
				this.sendMessage("§f - " + subRank.getSubRankColor() + subRank.getName());		
				subRanks.remove(subRanks.size() - 1);
			}		
		} else {
			this.sendErrorMessage(DONT_HAS_SUBRANK);
		}
	}
	
	public void openSetSubRankInventory(OfflinePlayer player) throws Exception {
		if(this.getSender() instanceof Player) {
			Player p = (Player) this.getSender();
			InteractiveInventory inventory = new InteractiveInventory(this.getInventoryItemMap(player), 9, player.getName());
			inventory.openInventory(p);
		} else {
			this.sendErrorMessage(Utils.getMustBeAPlayerMessage());
		}
	}
	
	public HashMap<Integer, InventoryItem> getInventoryItemMap(OfflinePlayer player) throws Exception {
		HashMap<Integer, InventoryItem> inventoryItemMap = new HashMap<>();
		ArrayList<SubRank> subRanks = sqlSubRank.getSubRanks(player);
		String name = "§cSous-rang: ";
		int amount = 1;
		
		InventoryItemInteractListener addSubRank = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					UUID uuid = sqlAccount.getUUIDOfPlayer(interactiveInventory.getInventory().getName());
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(SubRank subRank : SubRank.values()) {
						if(title.contains(subRank.getSubRankNameColor())) {
							addSubRank(player, subRank);
						}
					}
					event.getPlayer().closeInventory();
					openSetSubRankInventory(player);
				} catch (Exception e) {
					event.getPlayer().sendMessage(Utils.getExceptionMessage());
					e.printStackTrace();
				}
			}
		};
		
		InventoryItemInteractListener removeSubRank = new InventoryItemInteractListener() {
			
			@Override
			public void onInventoryItemClick(InventoryItemInteractEvent event) {
				try {
					InteractiveInventory interactiveInventory = event.getInteractiveInventory();
					InventoryItem inventoryItem = event.getInventoryItem();
					UUID uuid = sqlAccount.getUUIDOfPlayer(interactiveInventory.getInventory().getName());
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					String title = inventoryItem.getItemMeta().getDisplayName();
					
					for(SubRank subRank : SubRank.values()) {
						if(title.contains(subRank.getSubRankNameColor())) {
							removeSubRank(player, subRank);
						}
					}
					event.getPlayer().closeInventory();
					openSetSubRankInventory(player);
				} catch (Exception e) {
					event.getPlayer().sendMessage(Utils.getExceptionMessage());
					e.printStackTrace();
				}
			}
		};
		
		ArrayList<InventoryItem> items = new ArrayList<>();
		items.add(new InventoryItem(ItemUtils.generateItem(Material.EMERALD, amount, (short) 0, name + SubRank.VIP.getSubRankNameColor(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.DIAMOND, amount, (short) 0, name + SubRank.VIP_PLUS.getSubRankNameColor(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.GRASS, amount, (short) 0, name + SubRank.ARCHITECTE.getSubRankNameColor(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.REDSTONE_COMPARATOR, amount, (short) 0, name + SubRank.DEVELOPPEUR.getSubRankNameColor(), loresInfoAdd)));
		items.add(new InventoryItem(ItemUtils.generateItem(Material.BOOK_AND_QUILL, amount, (short) 0, name + SubRank.REDACTEUR.getSubRankNameColor(), loresInfoAdd)));
		
		for(int i = 0; i < items.size(); i++) {
			InventoryItem item = items.get(i);
			ItemMeta meta = item.getItemMeta();
			Iterator<SubRank> iterator = subRanks.iterator();
			boolean trouve = false;
			
			while(iterator.hasNext() && !trouve) {
				SubRank subRank = iterator.next();
				
				if(meta.getDisplayName().contains(subRank.getSubRankNameColor())) {
					meta.setLore(loresInfoRemove);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
					item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					item.addInventoryItemListener(removeSubRank);
					trouve = true;
				}
			}
			
			if(!trouve) {
				item.addInventoryItemListener(addSubRank);
			}
			inventoryItemMap.put(i, item);
		}
		inventoryItemMap.put(8, InventoryUtils.getExitItem());
		return inventoryItemMap;
	}
}