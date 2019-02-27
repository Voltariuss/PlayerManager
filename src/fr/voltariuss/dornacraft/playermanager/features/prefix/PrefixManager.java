package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.dornacraft.cache.PlayerCacheManager;

/**
 * Classe de gestion du pr�fixe des joueurs
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class PrefixManager {

	/**
	 * R�cup�re le pr�fixe du joueur dans la m�moire centrale si il est connect�,
	 * dans la base de donn�es sinon.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return Le pr�fixe du joueur, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
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
	 * Modifie le type de pr�fixe du joueur cibl�.
	 * 
	 * @param sender
	 *            L'�metteur de la requ�te, peut �tre null
	 * @param target
	 *            Le joueur cibl�, non null
	 * @param prefixType
	 *            Le nouveau type de pr�fixe du joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
	 */
	public static void setPrefixType(CommandSender sender, OfflinePlayer target, String prefixType)
			throws SQLException {
		SQLPrefixType.setPrefixType(target, prefixType);
		// Actualise le type de pr�fixe du joueur cibl� dans la m�moire centrale
		if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
			PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setPrefixType(prefixType);
		}

		if (sender != null) {
			sender.sendMessage("�aPr�fixe modifi� avec succ�s !");
			sender.sendMessage("�ePr�fixe actuel : " + Prefix.fromPlayer(target).toString());
		}
	}
}
