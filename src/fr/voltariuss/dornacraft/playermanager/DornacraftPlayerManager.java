package fr.voltariuss.dornacraft.playermanager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.voltariuss.dornacraft.api.msgs.MessageUtils;
import fr.voltariuss.dornacraft.playermanager.features.level.CmdAdminLevel;
import fr.voltariuss.dornacraft.playermanager.features.level.CmdLevel;
import fr.voltariuss.dornacraft.playermanager.features.permission.CmdHasPermission;
import fr.voltariuss.dornacraft.playermanager.features.permission.CmdPermission;
import fr.voltariuss.dornacraft.playermanager.features.prefix.CmdPrefix;
import fr.voltariuss.dornacraft.playermanager.features.rank.CmdRank;
import fr.voltariuss.dornacraft.playermanager.features.subrank.CmdSubRank;
import fr.voltariuss.dornacraft.playermanager.listeners.AsyncPlayerChatListener;
import fr.voltariuss.dornacraft.playermanager.listeners.PlayerConnectionListener;

/**
 * Classe principale du plugin
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class DornacraftPlayerManager extends JavaPlugin {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);

		this.getCommand(CmdRank.CMD_LABEL).setExecutor(new CmdRank());
		this.getCommand(CmdSubRank.CMD_LABEL).setExecutor(new CmdSubRank());
		this.getCommand(CmdPermission.CMD_LABEL).setExecutor(new CmdPermission());
		this.getCommand(CmdHasPermission.CMD_LABEL).setExecutor(new CmdHasPermission());
		this.getCommand(CmdLevel.CMD_LABEL).setExecutor(new CmdLevel());
		this.getCommand(CmdAdminLevel.CMD_LABEL).setExecutor(new CmdAdminLevel());
		this.getCommand(CmdPrefix.CMD_LABEL).setExecutor(new CmdPrefix());

		for (Player player : Bukkit.getOnlinePlayers()) {
			AccountManager.connectPlayer(player);
		}
		MessageUtils.sendActivationMessage(this.getClass(), true);
	}

	@Override
	public void onDisable() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			AccountManager.disconnectPlayer(player);
		}
		MessageUtils.sendActivationMessage(this.getClass(), false);
	}
}
