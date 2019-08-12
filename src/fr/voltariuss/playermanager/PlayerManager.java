package fr.voltariuss.playermanager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.voltariuss.playermanager.features.level.CmdAdminLevel;
import fr.voltariuss.playermanager.features.level.CmdLevel;
import fr.voltariuss.playermanager.features.permission.CmdHasPermission;
import fr.voltariuss.playermanager.features.permission.CmdPermission;
import fr.voltariuss.playermanager.features.prefix.CmdPrefix;
import fr.voltariuss.playermanager.features.rank.CmdRank;
import fr.voltariuss.playermanager.features.subrank.CmdSubRank;
import fr.voltariuss.playermanager.listeners.AsyncPlayerChatListener;
import fr.voltariuss.playermanager.listeners.PlayerConnectionListener;
import fr.voltariuss.playermanager.sql.SQLManager;
import fr.voltariuss.simpledevapi.UtilsAPI;

public final class PlayerManager extends JavaPlugin {

	@Override
	public void onEnable() {
		SQLManager.checkDatabase();
		activateListeners();
		activateCommands();
		AccountManager.connectPlayers();
		UtilsAPI.sendActivationMessage(this.getClass(), true);
	}

	@Override
	public void onDisable() {
		AccountManager.disconnectPlayers();
		UtilsAPI.sendActivationMessage(this.getClass(), false);
	}

	public void activateListeners() {
		Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
	}

	public void activateCommands() {
		this.getCommand(CmdRank.CMD_LABEL).setExecutor(new CmdRank());
		this.getCommand(CmdSubRank.CMD_LABEL).setExecutor(new CmdSubRank());
		this.getCommand(CmdPermission.CMD_LABEL).setExecutor(new CmdPermission());
		this.getCommand(CmdHasPermission.CMD_LABEL).setExecutor(new CmdHasPermission());
		this.getCommand(CmdLevel.CMD_LABEL).setExecutor(new CmdLevel());
		this.getCommand(CmdAdminLevel.CMD_LABEL).setExecutor(new CmdAdminLevel());
		this.getCommand(CmdPrefix.CMD_LABEL).setExecutor(new CmdPrefix());
	}
}
