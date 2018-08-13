package fr.voltariuss.dornacraftplayermanager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
	
	private static DornacraftPlayerManager instance;
	
	public static DornacraftPlayerManager getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new AsyncPlayerChatListener(), this);
		
		System.out.println(this.getCommand("test") == null);
		this.getCommand(CmdRank.CMD_LABEL).setExecutor(new CmdRank());
		this.getCommand(CmdSubRank.CMD_LABEL).setExecutor(new CmdSubRank());
		this.getCommand(CmdPermission.CMD_LABEL).setExecutor(new CmdPermission());
		this.getCommand(CmdLevel.CMD_LABEL).setExecutor(new CmdLevel());
		this.getCommand(CmdPrefix.CMD_LABEL).setExecutor(new CmdPrefix());
		
		this.saveDefaultConfig();
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			AccountManager.connectPlayer(player);
		}
		Utils.sendActivationMessage(PLUGIN_NAME, true);
	}
	
	@Override
	public void onDisable() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			AccountManager.disconnectPlayer(player);
		}
		Utils.sendActivationMessage(PLUGIN_NAME, false);
	}
}
