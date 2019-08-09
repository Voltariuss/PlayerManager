package fr.voltariuss.playermanager;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.voltariuss.simpledevapi.UtilsAPI;
import fr.voltariuss.simpledevapi.sql.SQLConnection;

public final class AccountManager {

	/**
	 * Vérifie si le joueur possède un compte sur le serveur et en créer un si ce
	 * n'est pas le cas.
	 * 
	 * @param player Le joueur concerné, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void checkAccount(Player player) throws SQLException {
		if (!SQLAccount.hasAccount(player)) {
			SQLAccount.createAccount(player);
		}
		SQLAccount.updateAccount(player);
	}

	/**
	 * Charge toutes les données relatives au joueur dans la mémoire centrale.
	 * 
	 * @param player Le joueur concerné, non null
	 */
	public static void connectPlayer(Player player) {
		try {
			SQLConnection.getInstance().refresh();
			PlayerCacheManager.loadPlayerCache(player);
		} catch (SQLException e) {
			e.printStackTrace();
			player.kickPlayer(UtilsAPI.CONNECTION_BLOCKED);
		}
	}

	/**
	 * Décharge toutes les données relatives au joueur de la mémoire centrale.
	 * 
	 * @param player Le joueur concerné, non null
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
	 * Récupère l'instance d'un joueur à partir de son uuid et le retourne.
	 * 
	 * @param playerName Le nom du joueur concerné, non null
	 * @return Le joueur correspondant au nom spécifié, peut être null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static OfflinePlayer getOfflinePlayer(String playerName) throws SQLException {
		UUID uuid = SQLAccount.getUUIDOfPlayer(playerName);
		return uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
	}
}
