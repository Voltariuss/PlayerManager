package fr.voltariuss.dornacraft.playermanager.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.voltariuss.dornacraft.playermanager.AccountManager;

/**
 * Classe de gestion des connexion et déconnexion des joueurs
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class PlayerConnectionListener implements Listener {
	
	/**
	 * Charge les données du joueur dans la mémoire cache.
	 * 
	 * @param event L'event de connexion d'un joueur, non null
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		AccountManager.connectPlayer(event.getPlayer());
	}
	
	/**
	 * Sauvegarde les données du joueur dans la base de données.
	 * 
	 * @param event L'event de déconnexion d'un joueur, non null
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		AccountManager.disconnectPlayer(event.getPlayer());
	}
}
