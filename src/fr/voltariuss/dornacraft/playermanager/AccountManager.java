package fr.voltariuss.dornacraft.playermanager;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraft.api.msgs.DornacraftAPIMessage;
import fr.voltariuss.dornacraft.sql.SQLConnection;

/**
 * Classe de gestion du compte des joueurs
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class AccountManager {

	/**
	 * V�rifie si le joueur poss�de un compte sur le serveur et en cr�er un si ce
	 * n'est pas le cas.
	 * 
	 * @param player
	 *            Le joueur concern�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void checkAccount(Player player) throws SQLException {
		if (!SQLAccount.hasAccount(player)) {
			SQLAccount.createAccount(player);
		}
		SQLAccount.updateAccount(player);
	}

	/**
	 * Charge toutes les donn�es relatives au joueur dans la m�moire centrale.
	 * 
	 * @param player
	 *            Le joueur concern�, non null
	 */
	public static void connectPlayer(Player player) {
		try {
			SQLConnection.getInstance().refresh();
			PlayerCacheManager.loadPlayerCache(player);
		} catch (SQLException e) {
			e.printStackTrace();
			player.kickPlayer(DornacraftAPIMessage.CONNECTION_BLOCKED);
		}
	}

	/**
	 * D�charge toutes les donn�es relatives au joueur de la m�moire centrale.
	 * 
	 * @param player
	 *            Le joueur concern�, non null
	 */
	public static void disconnectPlayer(Player player) {
		try {
			SQLAccount.updateAccount(player);
			PlayerCacheManager.unloadPlayerCache(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * R�cup�re l'instance d'un joueur � partir de son uuid et le retourne.
	 * 
	 * @param playerName
	 *            Le nom du joueur concern�, non null
	 * @return Le joueur correspondant au nom sp�cifi�, peut �tre null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static OfflinePlayer getOfflinePlayer(String playerName) throws SQLException {
		UUID uuid = SQLAccount.getUUIDOfPlayer(playerName);
		return uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
	}
}
