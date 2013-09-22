package me.tommycake50.lightcycle;

import java.util.HashMap;

import me.tommycake50.countdownlib.Countdown;
import me.tommycake50.countdownlib.CountdownEndEvent;
import me.tommycake50.countdownlib.CountdownTickEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

public class GameInstance implements Listener{
	private HashMap<String, Boolean> readyplayers = new HashMap<String, Boolean>();
	LightCycle instance;
	private boolean hasStarted;
	private Arena arena;
	public int ataskID;
	private boolean isInLobby = true;
	
	public GameInstance(LightCycle instance, Arena a){
		this.instance = instance;
		this.arena = a;
		instance.getServer().getPluginManager().registerEvents(this, instance);
	}
	
	private void start(){
		startCountdown();
		hasStarted = true;
		for(String s : readyplayers.keySet()){
			instance.getServer().getPlayerExact(s).teleport(arena.getLobby());
			readyplayers.put(s, false);
		}
		isInLobby = false;
	}
	
	
	
	private boolean isOut(Player p){
		if(readyplayers.keySet().contains(p.getName())){
			return readyplayers.keySet().contains(p.getName());
		}
		return true;
	}
	
	public void leftArena(String player){
		readyplayers.remove(player);
		readyplayers.remove(player);
		teleportAway(player);
	}

	private void teleportAway(String player) {
		instance.getServer().getPlayerExact(player).teleport(instance.getServer().getWorlds().get(1).getSpawnLocation());
	}
	
	private void reset(){
		arena.rollbackChanges();
	}
	
	private void equipPlayers(){
		for(String s : readyplayers.keySet()){
			instance.getServer().getPlayerExact(s).addPotionEffect(PotionEffectType.JUMP.createEffect(Integer.MAX_VALUE, 128));
		}
	}
	
	@EventHandler
	public void onCountdownEndEvent(CountdownEndEvent e){
		start();
	}
	
	public void onCountdownTickEvent(CountdownTickEvent e){
		if(!(readyplayers.size() >= 2)){
			e.setCancelled(true);
			startCountdown();
		}
	}
	
	private void startCountdown(){
		Countdown c = new Countdown(10, 20, false, instance);
		for(String s : readyplayers.keySet()){
			instance.getServer().getPlayerExact(s).sendMessage(ChatColor.GREEN + "[LightCycle]Game starting in 10 seconds");
		}
		c.start();
	}

	public void addPlayer(Player p){
		readyplayers.put(p.getName(), false);
	}
	
	public void removePlayer(Player p){
		readyplayers.remove(p.getName());
	}
	
	@SuppressWarnings("rawtypes")
	@EventHandler
	public void onPlayerLeaveArenaEvent(PlayerLeaveArenaEvent e){
		if(e.getPlayer() instanceof String){
			leftArena((String) e.getPlayer());
		}else{
			leftArena(((Player)e.getPlayer()).getName());
		}
		if(readyplayers.size() == 1){
			GameInstanceHandler.currentgames.remove(this);
			readyplayers.clear();
			instance = null;
			arena = null;
			readyplayers = null;
			System.gc();
		}
	}
	
	public Arena getArena(){
		return arena;
	}
	
	public boolean isInLobby(){
		return isInLobby ;
	}
}
