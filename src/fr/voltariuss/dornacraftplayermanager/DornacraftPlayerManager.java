package fr.voltariuss.dornacraftplayermanager;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.voltariuss.dornacraftapi.DornacraftApi;
import fr.voltariuss.dornacraftapi.sql.SQLConnection;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.cmd.CmdPrefix;
import fr.voltariuss.dornacraftplayermanager.cmd.CmdPerm;
import fr.voltariuss.dornacraftplayermanager.cmd.CmdRank;
import fr.voltariuss.dornacraftplayermanager.cmd.CmdSubRank;
import fr.voltariuss.dornacraftplayermanager.listeners.AsyncPlayerChatListener;
import fr.voltariuss.dornacraftplayermanager.listeners.InventoryInteractListener;
import fr.voltariuss.dornacraftplayermanager.listeners.PlayerConnectionListener;
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
	
	public static final String cmdRankLabel = "rank";
	public static final String cmdSubRankLabel = "subrank";
	public static final String cmdPermLabel = "perm";
	public static final String cmdLevelLabel = "level";
//	public static final String cmdPrefixLabel = "prefix";
	
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
		pm.registerEvents(new InventoryInteractListener(), this);
		pm.registerEvents(new AsyncPlayerChatListener(), this);
		
		getCommand(cmdRankLabel).setExecutor(new CmdRank(cmdRankLabel));
		getCommand(cmdSubRankLabel).setExecutor(new CmdSubRank(cmdSubRankLabel));
		getCommand(cmdPermLabel).setExecutor(new CmdPerm(cmdPermLabel));
		getCommand(cmdLevelLabel).setExecutor(new CmdPrefix(cmdLevelLabel));
//		getCommand(cmdPrefixLabel).setExecutor(Cmds.getInstance());
		
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		
		for(Player player : players) {
			connectPlayer(player);
		}
		
		saveDefaultConfig();
		
		Utils.sendActivationMessage(pluginName, true);
	}
	
	public void onDisable() {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		
		for(Player player : players) {
			disconnectPlayer(player);
		}
		Utils.sendActivationMessage(pluginName, false);
	}
	
	public void connectPlayer(Player player) {
		try {
			SQLConnection sqlConnection = DornacraftApi.getSqlConnection();
			sqlConnection.refresh();
			
			this.getSQLAccount().checkAccount(player);
			this.getPlayerCacheManager().loadPlayerCache(player);
			this.getPermissionManager().setPermissions(player);
		} catch (Exception e) {
			e.printStackTrace();
			player.kickPlayer("§cImpossible de se connecter au serveur : une erreur interne est survenue. Veuillez réessayer."
					+ "\n\nSi le problème persiste, contactez le staff dans les plus brefs délais via notre forum (forum.dornacraft.fr) ou notre discord (discord.dornacraft.fr).");
		}
	}
	
	public void disconnectPlayer(Player player) {
		UUID uuid = player.getUniqueId();		
		
		if(this.getPermissionAttachmentMap().containsKey(uuid)) {
			try {
				this.getPermissionAttachmentMap().remove(uuid);
				this.getSQLAccount().updateLastLogin(player);
				this.getPlayerCacheManager().unloadPlayerCache(player);
			} catch(Exception e) {
				e.printStackTrace();
			}			
		}
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
}
