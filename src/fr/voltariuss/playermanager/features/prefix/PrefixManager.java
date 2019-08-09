package fr.voltariuss.playermanager.features.prefix;

import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.simpledevapi.MessageLevel;
import fr.voltariuss.simpledevapi.UtilsAPI;

public final class PrefixManager {

	/**
	 * Récupère le préfixe du joueur dans la mémoire centrale s'il est connecté,
	 * dans la base de données sinon.
	 * 
	 * @param target Le joueur ciblé, non null
	 * @return Le préfixe du joueur, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static String getPrefixType(OfflinePlayer target) throws SQLException {
		String prefixType = UtilsPlayerManager.PREFIX_DEFAULT_TYPE;

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
	 * @param sender     L'émetteur de la requête, peut être null
	 * @param target     Le joueur ciblé, non null
	 * @param prefixType Le nouveau type de préfixe du joueur ciblé, non null
	 * @throws SQLException Si une erreur avec la base de données est détectée
	 */
	public static void setPrefixType(CommandSender sender, OfflinePlayer target, String prefixType)
			throws SQLException {
		SQLPrefixType.setPrefixType(target, prefixType);
		// Actualise le type de préfixe du joueur ciblé dans la mémoire centrale
		if (PlayerCacheManager.getPlayerCacheMap().containsKey(target.getUniqueId())) {
			PlayerCacheManager.getPlayerCacheMap().get(target.getUniqueId()).setPrefixType(prefixType);
		}

		if (sender != null) {
			if (sender.getName().equals(target.getName())) {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.PREFIX_UPDATED_HIMSELF);
				UtilsAPI.sendSystemMessage(MessageLevel.INFO, sender, UtilsPlayerManager.PREFIX_CURRENT_HIMSELF,
						Prefix.fromPlayer(target));
			} else {
				UtilsAPI.sendSystemMessage(MessageLevel.SUCCESS, sender, UtilsPlayerManager.PREFIX_UPDATED,
						target.getName());
				UtilsAPI.sendSystemMessage(MessageLevel.INFO, sender, UtilsPlayerManager.PREFIX_CURRENT,
						Prefix.fromPlayer(target), target.getName());
			}
		}
	}
}
