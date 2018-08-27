package fr.voltariuss.dornacraft.playermanager.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.voltariuss.dornacraft.playermanager.AccountManager;

public final class PlayerConnectionListener implements Listener {
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		AccountManager.connectPlayer(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		AccountManager.disconnectPlayer(event.getPlayer());
	}
}
