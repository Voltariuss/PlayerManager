package fr.voltariuss.dornacraftplayermanager;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.cmd.Cmds;
import fr.voltariuss.dornacraftplayermanager.listeners.PlayerConnectionListener;
import fr.voltariuss.dornacraftplayermanager.listeners.InventoryInteractListener;
import fr.voltariuss.dornacraftplayermanager.perm.PermissionManager;
import fr.voltariuss.dornacraftplayermanager.playercache.PlayerCache;
import fr.voltariuss.dornacraftplayermanager.playercache.PlayerCacheManager;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;
import fr.voltariuss.dornacraftplayermanager.sql.SQLPerm;
import fr.voltariuss.dornacraftplayermanager.sql.SQLPlayerCache;
import fr.voltariuss.dornacraftplayermanager.sql.SQLSubRank;

public class DornacraftPlayerManager extends JavaPlugin implements Listener {
		
	//Instances
	private static DornacraftPlayerManager instance;
	private final SQLAccount sqlAccount = new SQLAccount();
	private final SQLPlayerCache sqlPlayerCache = new SQLPlayerCache();
	private final SQLSubRank sqlSubRank = new SQLSubRank();
	private final SQLPerm sqlPerm = new SQLPerm();
	private final PlayerCacheManager playerCacheManager = new PlayerCacheManager();
	private final PermissionManager permissionManager = new PermissionManager();
	private final InventoryInteractListener inventoryInteractListener = new InventoryInteractListener();
	
	//Collection
	private static final HashMap<UUID,PlayerCache> playerCacheMap = new HashMap<>();
	private static final HashMap<UUID,PermissionAttachment> permissionAttachementMap = new HashMap<>();
	
	//Utils
	private static final String pluginName = "Dornacraft-PlayerManager";
	private static final int maxLevel = 80;
	
	public void onEnable() {
		instance = this;
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new PlayerConnectionListener(), this);
		pm.registerEvents(inventoryInteractListener, this);
		
		getCommand("rank").setExecutor(new Cmds());
		getCommand("subrank").setExecutor(new Cmds());
		getCommand("perm").setExecutor(new Cmds());
		getCommand("level").setExecutor(new Cmds());
		getCommand("prefix").setExecutor(new Cmds());
		
		saveDefaultConfig();
		
		Utils.sendActivationMessage(pluginName, true);
	}
	
	public void onDisable() {
		playerCacheMap.clear();
		permissionAttachementMap.clear();
		Utils.sendActivationMessage(pluginName, false);
	}
	
	public static DornacraftPlayerManager getInstance() {
		return instance;
	}
	
	public HashMap<UUID,PlayerCache> getPlayerCacheMap() {
		return playerCacheMap;
	}
	
	public HashMap<UUID,PermissionAttachment> getPermissionAttachmentMap() {
		return permissionAttachementMap;
	}
	
	public String getPluginName() {
		return pluginName;
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public SQLAccount getSQLAccount() {
		return sqlAccount;
	}
	
	public SQLPlayerCache getSQLPlayerCache() {
		return sqlPlayerCache;
	}
	
	public SQLSubRank getSQLSubRank() {
		return sqlSubRank;
	}
	
	public SQLPerm getSqlPerm() {
		return sqlPerm;
	}
	
	public PlayerCacheManager getPlayerCacheManager() {
		return playerCacheManager;
	}
	
	public PermissionManager getPermissionManager() {
		return permissionManager;
	}
	
	public InventoryInteractListener getInventoryInteractListener() {
		return inventoryInteractListener;
	}
}
