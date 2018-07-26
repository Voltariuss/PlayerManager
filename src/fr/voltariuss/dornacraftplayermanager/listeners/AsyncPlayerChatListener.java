package fr.voltariuss.dornacraftplayermanager.listeners;

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

import fr.voltariuss.dornacraftapi.utils.Utils;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.cache.playercache.PlayerCache;
import fr.voltariuss.dornacraftplayermanager.features.prefix.Prefix;
import fr.voltariuss.dornacraftplayermanager.features.rank.Rank;
import fr.voltariuss.dornacraftplayermanager.features.subrank.SubRank;

public class AsyncPlayerChatListener implements Listener {
	
	private Player player;
	private ArrayList<SubRank> subRanks = new ArrayList<>();
	private Rank rank;
	private Prefix prefix;
	private int level;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		player = event.getPlayer();
		String message = event.getMessage().replaceAll("%", "%%");
		
		try {
			UUID uuid = player.getUniqueId();
			HashMap<UUID,PlayerCache> playerCacheMap = DornacraftPlayerManager.getInstance().getPlayerCacheMap();	
			PlayerCache playerCache = playerCacheMap.get(uuid);
			
			rank = playerCache.getRank();
			subRanks = playerCache.getSubRanks();
			level = playerCache.getLevel();
			prefix = playerCache.getPrefix();
			
			if(rank.getPower() <= 2) {
				event.setCancelled(true);
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					MPlayer mPlayer = MPlayer.get(p);
					p.sendMessage(getFactionPrefix(mPlayer) + getLevelPrefix(level) + getDisplayName() + " §8» " + getMessage(message));
				}
			} else {
				event.setFormat(Utils.getStaffPrefix() + getDisplayName() + " §8» " + getMessage(message));
			}
		} catch(Exception e) {
			e.printStackTrace();
			player.sendMessage(Utils.getExceptionMessage());
		}
	}
	
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
	
	public String getLevelPrefix(int level) {
		String lvl = Integer.toString(level);
		
		if(level < 10) {
			lvl = "0" + lvl;
		}
		
		return "§7[§e" + (level >= 70 ? "§l" : "") + lvl + "§7] ";
	}
	
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
	
	public String getDisplayName() throws Exception {
		String displayName = this.getPrefix();
		
		if(rank == Rank.JOUEUR && (subRanks.contains(SubRank.VIP) || subRanks.contains(SubRank.VIP_PLUS))) {
			displayName = displayName + (subRanks.contains(SubRank.VIP_PLUS) ? SubRank.VIP_PLUS.getPseudoColor() : SubRank.VIP.getPseudoColor()) + player.getPlayerListName();
		} else {
			displayName = displayName + rank.getRankColor() + player.getPlayerListName();
		}
		return displayName;
	}
	
	public String getMessage(String message) {
		String msg = message.toString();
		
		if(player.hasPermission("dornacraft.chat.couleur")) {
			msg = msg.replaceAll("&&", "§§").replaceAll("&", "§").replaceAll("§§", "&&");
		}
		if(rank == Rank.JOUEUR && !subRanks.isEmpty()) {
			msg = SubRank.getMsgColor() + msg;
		} else {
			msg = rank.getMessageColor() + msg;			
		}
		
		return msg;
	}
}
