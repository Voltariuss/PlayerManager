package fr.voltariuss.dornacraftplayermanager.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SetRankInventoryInteractEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	public SetRankInventoryInteractEvent() {
		
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
