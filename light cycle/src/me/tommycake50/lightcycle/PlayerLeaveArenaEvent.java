package me.tommycake50.lightcycle;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveArenaEvent<T> extends Event{
	private static final HandlerList handlers = new HandlerList();
	T player;
	
	public PlayerLeaveArenaEvent(T p){
		player = p;
	}

	@Override
	public HandlerList getHandlers(){
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public T getPlayer(){
		return player;
	}
}
