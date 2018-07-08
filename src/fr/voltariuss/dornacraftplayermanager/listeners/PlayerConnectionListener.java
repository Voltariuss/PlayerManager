package fr.voltariuss.dornacraftplayermanager.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;

public class PlayerConnectionListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		event.setJoinMessage(" §6[§a+§6] §e" + player.getPlayerListName());
		DornacraftPlayerManager.getInstance().connectPlayer(player);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		DornacraftPlayerManager.getInstance().disconnectPlayer(player);
		event.setQuitMessage(" §6[§c-§6] §e" + player.getPlayerListName());
	}
}
