package fr.voltariuss.dornacraftplayermanager;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.features.level.CmdLevel;
import fr.voltariuss.dornacraftplayermanager.features.permission.CmdPermission;
import fr.voltariuss.dornacraftplayermanager.features.prefix.CmdPrefix;
import fr.voltariuss.dornacraftplayermanager.features.rank.CmdRank;
import fr.voltariuss.dornacraftplayermanager.features.subrank.CmdSubRank;
import fr.voltariuss.dornacraftplayermanager.listeners.AsyncPlayerChatListener;

public class DornacraftPlayerManager extends JavaPlugin implements Listener {
		
	
	public static final String PLUGIN_NAME = "Dornacraft-PlayerManager";
	
	public static final String CMD_RANK_LABEL = "rank";
	public static final String CMD_SUBRANK_LABEL = "subrank";
	public static final String CMD_PERM_LABEL = "perm";
	public static final String CMD_ECO_LABEL = "eco";
	public static final String CMD_MONEY_LABEL = "money";
	public static final String CMD_LEVEL_LABEL = "level";
	public static final String CMD_PREFIX_LABEL = "prefix";
	
	private static DornacraftPlayerManager instance;
	
	public static DornacraftPlayerManager getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new AsyncPlayerChatListener(), this);
		
		getCommand(CMD_RANK_LABEL).setExecutor(new CmdRank(CMD_RANK_LABEL));
		getCommand(CMD_SUBRANK_LABEL).setExecutor(new CmdSubRank(CMD_SUBRANK_LABEL));
		getCommand(CMD_PERM_LABEL).setExecutor(new CmdPermission(CMD_PERM_LABEL));
		getCommand(CMD_LEVEL_LABEL).setExecutor(new CmdLevel(CMD_LEVEL_LABEL));
		getCommand(CMD_PREFIX_LABEL).setExecutor(new CmdPrefix(CMD_PREFIX_LABEL));
		
		this.saveDefaultConfig();
		Utils.sendActivationMessage(PLUGIN_NAME, true);
	}
	
	@Override
	public void onDisable() {
		Utils.sendActivationMessage(PLUGIN_NAME, false);
	}
}
