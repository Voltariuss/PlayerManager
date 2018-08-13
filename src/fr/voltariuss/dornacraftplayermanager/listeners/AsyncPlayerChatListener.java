package fr.voltariuss.dornacraftplayermanager.listeners;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.massivecraft.factions.entity.MPlayer;

import fr.dornacraft.cache.PlayerCache;
import fr.dornacraft.cache.PlayerCacheManager;
import fr.voltariuss.dornacraftapi.utils.ErrorMessage;
import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.features.prefix.Prefix;
import fr.voltariuss.dornacraftplayermanager.features.rank.Rank;
import fr.voltariuss.dornacraftplayermanager.features.subrank.SubRank;

public class AsyncPlayerChatListener implements Listener {
	
	private Player player;
	private ArrayList<SubRank> subRanks = new ArrayList<>();
	private Rank rank;
	private Prefix prefix;
	private int level;
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		player = event.getPlayer();
		String message = event.getMessage().replaceAll("%", "%%");
		
		try {
			UUID uuid = player.getUniqueId();
			HashMap<UUID,PlayerCache> playerCacheMap = PlayerCacheManager.getPlayerCacheMap();	
			PlayerCache playerCache = playerCacheMap.get(uuid);
			
			rank = playerCache.getRank();
			subRanks = playerCache.getSubRanks();
			level = playerCache.getLevel();
			prefix = playerCache.getPrefix();
			
			if(rank.getPower() <= 2) {
				event.setCancelled(true);
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					MPlayer mPlayer = MPlayer.get(p);
					String format = getFactionPrefix(mPlayer) + getLevelPrefix(level) + getDisplayName() + " §8» " + getMessage(message);
					p.sendMessage(format);
					event.setFormat(format);
				}
			} else {
				event.setFormat(Utils.PREFIX_STAFF + getDisplayName() + " §8» " + getMessage(message));
			}
		} catch(SQLException e) {
			e.printStackTrace();
			Utils.sendErrorMessage(player, ErrorMessage.EXCEPTION_MESSAGE);
		}
	}
	
	/**
	 * Retourne le préfixe de la faction du joueur ciblé.
	 * 
	 * @param mPlayerReceiver Le joueur ciblé, non null
	 * @return Le préfixe de la faction du joueur ciblé, non null
	 */
	public String getFactionPrefix(MPlayer mPlayerReceiver) {
		MPlayer mPlayerSender = MPlayer.get(player);
		String factionPrefix = "";
		
		if(!mPlayerSender.getFaction().isNone()) {
			String role = "";
			String color = "§r";
			
			switch(mPlayerSender.getRole()) {
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
			
			if(!mPlayerReceiver.getFaction().isNone()) {
				if(mPlayerReceiver.getFaction().equals(mPlayerSender.getFaction())) {
					color = "§a";
				} else
					switch(mPlayerReceiver.getFaction().getRelationTo(mPlayerSender.getFaction())) {
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
	 * @param level Le niveau du joueur ciblé
	 * @return Le préfixe du niveau du joueur ciblé, non null
	 */
	public String getLevelPrefix(int level) {
		String lvl = Integer.toString(level);
		
		if(level < 10) {
			lvl = "0" + lvl;
		}
		
		return "§7[§e" + (level >= 70 ? "§l" : "") + lvl + "§7] ";
	}
	
	/**
	 * Retourne le préfixe du joueur ciblé.
	 * 
	 * @return Le préfixe du joueur ciblé, non null
	 */
	public String getPrefix() {
		String strPrefix = rank.getPrefix().toString();
		
		if(rank == Rank.JOUEUR || rank == Rank.GUIDE) {
			strPrefix = prefix.toString();
			
			if(rank == Rank.GUIDE) {
				strPrefix = rank.getPrefix() + strPrefix;
			}
		} else if(rank == Rank.ADMINISTRATEUR) {
			if(player.getName().equalsIgnoreCase("Voltariuss")) {
				strPrefix = Prefix.FONDATEUR.toString();
			} else if(player.getName().equalsIgnoreCase("Glynix")) {
				strPrefix = Prefix.CO_FONDATEUR.toString();
			}
		}
		return strPrefix;
	}
	
	/**
	 * Retoune le nom complet du joueur ciblé.
	 * 
	 * @return Le nom complet du joueur ciblé, non null
	 * @throws SQLException
	 */
	public String getDisplayName() throws SQLException {
		String displayName = this.getPrefix();
		
		if(rank == Rank.JOUEUR && (subRanks.contains(SubRank.VIP) || subRanks.contains(SubRank.VIP_PLUS))) {
			displayName = displayName + (subRanks.contains(SubRank.VIP_PLUS) ? SubRank.VIP_PLUS.getPseudoColor() : SubRank.VIP.getPseudoColor()) + player.getPlayerListName();
		} else {
			displayName = displayName + rank.getColor() + player.getPlayerListName();
		}
		return displayName;
	}
	
	/**
	 * Retourne le message envoyé par le joueur ciblé modifié.
	 * 
	 * @param message Le message envoyé par le joueur ciblé.
	 * @return Le message envoyé par le joueur ciblé modifié.
	 */
	public String getMessage(String message) {
		String msg = message.toString();
		
		if(player.hasPermission("dornacraft.chat.couleur")) {
			msg = msg.replaceAll("&&", "§§").replaceAll("&", "§").replaceAll("§§", "&&");
		}
		if(rank == Rank.JOUEUR && !subRanks.isEmpty()) {
			msg = SubRank.getMsgColor() + msg;
		} else {
			msg = rank.getMsgColor() + msg;			
		}
		return msg;
	}
}
