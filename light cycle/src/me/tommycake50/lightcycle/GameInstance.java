package me.tommycake50.lightcycle;

import java.util.HashMap;

import me.tommycake50.countdownlib.Countdown;
import me.tommycake50.countdownlib.CountdownEndEvent;
import me.tommycake50.countdownlib.CountdownTickEvent;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
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
		hasStarted = true;
		for(String s : readyplayers.keySet()){
			instance.getServer().getPlayerExact(s).teleport(arena.getLobby());
			readyplayers.put(s, false);
		}
		isInLobby = false;
		equipPlayers();
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
			instance.getServer().getPlayerExact(s).addPotionEffect(PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, 100));
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
	
	public void startCountdown(){
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
		if(readyplayers.size() == 0){
			reset();
			GameInstanceHandler.currentgames.remove(this);
			readyplayers.clear();
			instance = null;
			arena = null;
			readyplayers = null;
			System.gc();
		}
	}
	
	public void stop(){
		reset();
		GameInstanceHandler.currentgames.remove(this);
		readyplayers.clear();
		instance = null;
		arena = null;
		readyplayers = null;
		System.gc();
	}

	public Arena getArena(){
		return arena;
	}
	
	public boolean isInLobby(){
		return isInLobby ;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDamageEvent(EntityDamageEvent e){
		if(e.getEntity() instanceof Player && hasStarted){
			if(e.getCause().equals(DamageCause.LAVA)){
				((Player)e.getEntity()).teleport(arena.getLobby());
				((Player)e.getEntity()).sendMessage(ChatColor.RED + "[LightCycle]You lose!");
				readyplayers.remove(((Player)e.getEntity()).getName());
				if(readyplayers.size() == 1){
					instance.getServer().getPlayerExact(readyplayers.keySet().iterator().next()).sendMessage(ChatColor.GREEN + "[LightCycle] wow! you win, here have a cookie");
					instance.getServer().getPlayerExact(readyplayers.keySet().iterator().next()).getInventory().addItem(new ItemStack(Material.COOKIE, 1));
					instance.getServer().getPlayerExact(readyplayers.keySet().iterator().next()).updateInventory();
					instance.getServer().getPlayerExact(readyplayers.keySet().iterator().next()).teleport(arena.getLobby());
					stop();
				}
			}
		}
	}
}
