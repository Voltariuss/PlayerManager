package fr.voltariuss.dornacraftplayermanager;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraftapi.sql.SQLConnection;
import fr.voltariuss.dornacraftapi.utils.ErrorMessage;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.features.permission.PermissionManager;

public class AccountManager {

	/**
	 * V�rifie si le joueur poss�de un compte sur le serveur et en cr�er un si ce n'est pas le cas.
	 * 
	 * @param player Le joueur concern�, non null
	 * @throws SQLException
	 */
	public static void checkAccount(Player player) throws SQLException {
		if(!SQLAccount.hasAccount(player)) {
			SQLAccount.createAccount(player);
		}
		SQLAccount.updateLastLogin(player);
	}
	
	/**
	 * Charge toutes les donn�es relatives au joueur dans la m�moire centrale.
	 * 
	 * @param player Le joueur concern�, non null
	 */
	public static void connectPlayer(Player player) {
		try {
			SQLConnection.refresh();
			checkAccount(player);
			PlayerCacheManager.loadPlayerCache(player);
			PermissionManager.setPermissions(player);
		} catch (SQLException e) {
			e.printStackTrace();
			player.kickPlayer(Utils.PREFIX_ERROR + ErrorMessage.CONNECTION_IMPOSSIBLE);
		}
	}
	
	/**
	 * D�charge toutes les donn�es relatives au joueur de la m�moire centrale.
	 * 
	 * @param player Le joueur concern�, non null
	 */
	public static void disconnectPlayer(Player player) {
		UUID uuid = player.getUniqueId();		
		
		try {
			PermissionManager.getPermissionAttachmentMap().remove(uuid);
			SQLAccount.updateLastLogin(player);
			PlayerCacheManager.unloadPlayerCache(player);
		} catch(Exception e) {
			e.printStackTrace();
		}			
	}
	
	/**
	 * R�cup�re l'instance d'un joueur � partir de son uuid et le retourne.
	 * 
	 * @param playerName Le nom du joueur concern�, non null
	 * @return Le joueurayant comme nom celui entr� en param�tres, peut �tre null
	 * @throws SQLException
	 */
	public static OfflinePlayer getOfflinePlayer(String playerName) throws SQLException {
		UUID uuid = SQLAccount.getUUIDOfPlayer(playerName);
		return uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
	}
}
