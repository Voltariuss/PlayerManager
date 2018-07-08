package fr.voltariuss.dornacraftplayermanager.listeners;

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
import fr.voltariuss.dornacraftplayermanager.Rank;
import fr.voltariuss.dornacraftplayermanager.playercache.PlayerCache;

public class AsyncPlayerChatListener implements Listener {
	
	private Player player;
	private Rank rank;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		player = event.getPlayer();
		String message = event.getMessage().replaceAll("%", "%%");
		
		try {
			UUID uuid = player.getUniqueId();
			HashMap<UUID,PlayerCache> playerCacheMap = DornacraftPlayerManager.getInstance().getPlayerCacheMap();	
			PlayerCache playerCache = playerCacheMap.get(uuid);
			
			rank = playerCache.getRank();
			int level = playerCache.getLevel();
			String prefix = playerCache.getPrefix().getPrefix();
			
			if(rank.getPower() <= 2) {
				event.setCancelled(true);
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					MPlayer mPlayer = MPlayer.get(p);
					p.sendMessage(getFactionPrefix(mPlayer) + getLevelPrefix(level) + getDisplayName(prefix) + " §8» " + getMessage(message));
				}
			} else {
				event.setFormat(Utils.getStaffPrefix() + getDisplayName(prefix) + " §8» " + getMessage(message));
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
		
		return "§7[§e" + lvl + "§7] ";
	}
	
	public String getPrefix() {
		
		return null;
	}
	
	public String getDisplayName(String prefix) throws Exception {
		return prefix + rank.getRankColor() + player.getPlayerListName();
	}
	
	public String getMessage(String message) {
		String msg = message.toString();
		
		if(player.hasPermission("dornacraft.chat.couleur")) {
			msg = msg.replaceAll("&&", "§§").replaceAll("&", "§").replaceAll("§§", "&&");
		}
		msg = rank.getMessageColor() + msg;
		
		return msg;
	}
}
