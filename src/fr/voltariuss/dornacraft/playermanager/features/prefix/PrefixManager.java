package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.dornacraft.cache.PlayerCacheManager;

/**
 * Classe de gestion du préfixe des joueurs
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class PrefixManager {

	/**
	 * Récupère le préfixe du joueur dans la mémoire centrale si il est connecté,
	 * dans la base de données sinon.
	 * 
	 * @param target
	 *            Le joueur ciblé, non null
	 * @return Le préfixe du joueur, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static String getPrefixType(OfflinePlayer target) throws SQLException {
		String prefixType = Prefix.getDefault();

		if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
			prefixType = PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).getPrefixType();
		} else {
			prefixType = SQLPrefixType.getPrefixType(target);
		}
		return prefixType;
	}

	/**
	 * Modifie le type de préfixe du joueur ciblé.
	 * 
	 * @param sender
	 *            L'émetteur de la requête, peut être null
	 * @param target
	 *            Le joueur ciblé, non null
	 * @param prefixType
	 *            Le nouveau type de préfixe du joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static void setPrefixType(CommandSender sender, OfflinePlayer target, String prefixType)
			throws SQLException {
		SQLPrefixType.setPrefixType(target, prefixType);
		// Actualise le type de préfixe du joueur ciblé dans la mémoire centrale
		if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
			PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setPrefixType(prefixType);
		}

		if (sender != null) {
			sender.sendMessage("§aPréfixe modifié avec succès !");
			sender.sendMessage("§ePréfixe actuel : " + Prefix.fromPlayer(target).toString());
		}
	}
}
