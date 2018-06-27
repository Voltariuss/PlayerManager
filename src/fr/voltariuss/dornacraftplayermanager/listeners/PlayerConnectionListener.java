package fr.voltariuss.dornacraftplayermanager.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import fr.voltariuss.dornacraftapi.DornacraftApi;
import fr.voltariuss.dornacraftapi.sql.SQLConnection;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.perm.PermissionManager;
import fr.voltariuss.dornacraftplayermanager.playercache.PlayerCacheManager;
import fr.voltariuss.dornacraftplayermanager.sql.SQLAccount;

public class PlayerConnectionListener implements Listener {
	
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
	private PlayerCacheManager playerCacheManager = main.getPlayerCacheManager();
	private PermissionManager permissionManager = main.getPermissionManager();
	private SQLAccount sqlAccount = main.getSQLAccount();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		event.setJoinMessage(" §6[§a+§6] §e" + player.getPlayerListName());
		
		try {
			SQLConnection sqlConnection = DornacraftApi.getSqlConnection();
			sqlConnection.disconnect();
			
			if(!sqlConnection.isConnected()) {
				sqlConnection.connect();
			}
			sqlAccount.checkAccount(player);
			playerCacheManager.loadPlayerCache(player);
			permissionManager.setPermissions(player);
		} catch (Exception e) {
			e.printStackTrace();
			player.kickPlayer("§cImpossible de se connecter au serveur : une erreur interne est survenue. Veuillez réessayer."
					+ "\n\nSi le problème persiste, contactez le staff dans les plus brefs délais via notre forum (forum.dornacraft.fr) ou notre discord (discord.dornacraft.fr).");
		}
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		HashMap<UUID, PermissionAttachment> permissionAttachmentMap = main.getPermissionAttachmentMap();
		
		event.setQuitMessage(" §6[§c-§6] §e" + player.getPlayerListName());
		
		try {
			permissionAttachmentMap.remove(uuid);
			sqlAccount.updateLastLogin(player);
			playerCacheManager.unloadPlayerCache(player);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
