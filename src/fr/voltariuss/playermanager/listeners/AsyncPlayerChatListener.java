package fr.voltariuss.playermanager.listeners;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.massivecraft.factions.entity.MPlayer;

import fr.voltariuss.dornacraft.api.MessageLevel;
import fr.voltariuss.dornacraft.api.UtilsAPI;
import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.playermanager.features.level.LevelManager;
import fr.voltariuss.playermanager.features.prefix.Prefix;
import fr.voltariuss.playermanager.features.rank.Rank;
import fr.voltariuss.playermanager.features.rank.RankManager;
import fr.voltariuss.playermanager.features.subrank.SubRank;
import fr.voltariuss.playermanager.features.subrank.SubRankManager;

public final class AsyncPlayerChatListener implements Listener {

	/**
	 * Définit le format du chat.
	 * 
	 * @param event
	 *            L'event d'envoie de message d'un joueur dans le chat, non null
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player sender = event.getPlayer();
		String message = event.getMessage().replaceAll("%", "%%");

		try {
			Rank rank = RankManager.getRank(sender);
			int level = LevelManager.getLevel(sender);
			ArrayList<SubRank> subRanks = SubRankManager.getSubRanks(sender);

			if (rank.getPower() <= 2) {
				event.setCancelled(true);

				for (Player p : Bukkit.getOnlinePlayers()) {
					MPlayer mPlayer = MPlayer.get(p);
					String format = getFactionPrefix(mPlayer, sender) + getLevelPrefix(level)
							+ getDisplayName(sender, rank, subRanks) + UtilsPlayerManager.CHAT_PREFIX_MESSAGE
							+ getMessage(sender, message, rank, subRanks);
					p.sendMessage(format);
				}
				Bukkit.getConsoleSender()
						.sendMessage(getFactionPrefix(MPlayer.get(Bukkit.getConsoleSender()), sender)
								+ getLevelPrefix(level) + getDisplayName(sender, rank, subRanks) + UtilsPlayerManager.CHAT_PREFIX_MESSAGE
								+ getMessage(sender, message, rank, subRanks));
			} else {
				event.setFormat(UtilsPlayerManager.CHAT_PREFIX_STAFF + getDisplayName(sender, rank, subRanks) + UtilsPlayerManager.CHAT_PREFIX_MESSAGE
						+ getMessage(sender, message, rank, subRanks));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			UtilsAPI.sendSystemMessage(MessageLevel.ERROR, sender, UtilsAPI.INTERNAL_EXCEPTION);
		}
	}

	/**
	 * Retourne le préfixe de la faction du joueur ciblé.
	 * 
	 * @param mPlayerReceiver
	 *            Le joueur ciblé, non null
	 * @param sender
	 *            Le joueur émetteur du message, non null
	 * @return Le préfixe de la faction du joueur ciblé, non null
	 */
	public String getFactionPrefix(MPlayer mPlayerReceiver, Player sender) {
		MPlayer mPlayerSender = MPlayer.get(sender);
		String factionPrefix = "";

		if (!mPlayerSender.getFaction().isNone()) {
			String role = "";
			String color = "§r";

			switch (mPlayerSender.getRole()) {
			case LEADER:
				role = "**";
				break;
			case OFFICER:
				role = "*";
				break;
			case MEMBER:
				role = "+";
				break;
			default:
				role = "-";
				break;
			}

			if (!mPlayerReceiver.getFaction().isNone()) {
				if (mPlayerReceiver.getFaction().equals(mPlayerSender.getFaction())) {
					color = "§a";
				} else
					switch (mPlayerReceiver.getFaction().getRelationTo(mPlayerSender.getFaction())) {
					case ALLY:
						color = "§5";
						break;
					case TRUCE:
						color = "§d";
						break;
					case ENEMY:
						color = "§c";
						break;
					default:
						break;
					}
			}
			factionPrefix = color + role + mPlayerSender.getFactionName() + " ";
		}

		return factionPrefix;
	}

	/**
	 * Retourne le préfixe du niveau du joueur ciblé.
	 * 
	 * @param level
	 *            Le niveau du joueur ciblé
	 * @return Le préfixe du niveau du joueur ciblé, non null
	 */
	public String getLevelPrefix(int level) {
		String lvl = Integer.toString(level);

		if (level < 10) {
			lvl = "0" + lvl;
		}

		return "§7[§e" + (level >= 70 ? "§l" : "") + lvl + "§7] ";
	}

	/**
	 * @param sender
	 *            Le joueur émetteur du message, non null
	 * @param rank
	 *            Le rang du joueur, non null
	 * @return Le préfixe du joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public String getPrefix(Player sender, Rank rank) throws SQLException {
		return Prefix.fromPlayer(sender).toString() + (rank == Rank.HELPER ? rank.getPrefix() : "");
	}

	/**
	 * @param sender
	 *            Le joueur émetteur du message, non null
	 * @param rank
	 *            Le rang du joueur, non null
	 * @param subRanks
	 *            La liste des sous-rangs du joueur, non null
	 * @return Le nom complet du joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public String getDisplayName(Player sender, Rank rank, ArrayList<SubRank> subRanks) throws SQLException {
		String displayName = this.getPrefix(sender, rank);

		if (rank == Rank.PLAYER && (subRanks.contains(SubRank.VIP) || subRanks.contains(SubRank.VIP_PLUS))) {
			displayName = displayName + (subRanks.contains(SubRank.VIP_PLUS) ? SubRank.VIP_PLUS.getPseudoColor()
					: SubRank.VIP.getPseudoColor()) + sender.getPlayerListName();
		} else {
			displayName = displayName + rank.getColor() + sender.getPlayerListName();
		}
		return displayName;
	}

	/**
	 * Retourne le message envoyé par le joueur ciblé modifié.
	 * 
	 * @param sender
	 *            Le joueur émetteur du message, non null
	 * @param message
	 *            Le message envoyé par le joueur, non null
	 * @param rank
	 *            Le rang du joueur, non null
	 * @param subRanks
	 *            La liste des sous-rangs du joueur, non null
	 * @return Le message envoyé par le joueur ciblé modifié.
	 */
	public String getMessage(Player sender, String message, Rank rank, ArrayList<SubRank> subRanks) {
		String msg = message.toString();

		if (sender.hasPermission(UtilsPlayerManager.CHAT_PERMISSION_COLOR)) {
			msg = msg.replaceAll("&&", "§§").replaceAll("&", "§").replaceAll("§§", "&&");
		}
		if (rank == Rank.PLAYER && !subRanks.isEmpty()) {
			msg = SubRank.getMessageColor() + msg;
		} else {
			msg = rank.getMessageColor() + msg;
		}
		return msg;
	}
}
