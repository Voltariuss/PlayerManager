package fr.voltariuss.dornacraft.playermanager.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.voltariuss.dornacraft.playermanager.AccountManager;

/**
 * Classe de gestion des connexion et d�connexion des joueurs
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class PlayerConnectionListener implements Listener {
	
	/**
	 * Charge les donn�es du joueur dans la m�moire cache.
	 * 
	 * @param event L'event de connexion d'un joueur, non null
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		AccountManager.connectPlayer(event.getPlayer());
	}
	
	/**
	 * Sauvegarde les donn�es du joueur dans la base de donn�es.
	 * 
	 * @param event L'event de d�connexion d'un joueur, non null
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		AccountManager.disconnectPlayer(event.getPlayer());
	}
}
