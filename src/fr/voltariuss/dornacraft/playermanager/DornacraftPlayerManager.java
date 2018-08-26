package fr.voltariuss.dornacraft.playermanager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.voltariuss.dornacraft.api.utils.Utils;
import fr.voltariuss.dornacraft.playermanager.features.level.CmdLevel;
import fr.voltariuss.dornacraft.playermanager.features.permission.CmdPermission;
import fr.voltariuss.dornacraft.playermanager.features.prefix.CmdPrefix;
import fr.voltariuss.dornacraft.playermanager.features.rank.CmdRank;
import fr.voltariuss.dornacraft.playermanager.features.subrank.CmdSubRank;
import fr.voltariuss.dornacraft.playermanager.listeners.AsyncPlayerChatListener;

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
		
		this.getCommand(CmdRank.CMD_LABEL).setExecutor(new CmdRank());
		this.getCommand(CmdSubRank.CMD_LABEL).setExecutor(new CmdSubRank());
		this.getCommand(CmdPermission.CMD_LABEL).setExecutor(new CmdPermission());
		this.getCommand(CmdLevel.CMD_LABEL).setExecutor(new CmdLevel());
		this.getCommand(CmdPrefix.CMD_LABEL).setExecutor(new CmdPrefix());
		
		this.saveDefaultConfig();
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			AccountManager.connectPlayer(player);
		}
		Utils.sendActivationMessage(this.getClass(), true);
	}
	
	@Override
	public void onDisable() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			AccountManager.disconnectPlayer(player);
		}
		Utils.sendActivationMessage(this.getClass(), false);
	}
}
