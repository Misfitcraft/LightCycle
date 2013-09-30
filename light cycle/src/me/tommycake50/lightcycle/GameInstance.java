package me.tommycake50.lightcycle;

import java.util.HashMap;

import me.tommycake50.countdownlib.Countdown;
import me.tommycake50.countdownlib.CountdownEndEvent;
import me.tommycake50.countdownlib.CountdownTickEvent;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
	
	public void leftArena(String player){
		removePlayer(instance.getServer().getPlayerExact(player));
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
	int iterations = 0;
	public void startCountdown(){
		Countdown c = new Countdown(10, 20, false, instance);
		if(iterations == 0 || iterations == 10|| iterations == 20 || iterations == 30 || iterations == 50 || iterations == 100){
			for(String s : readyplayers.keySet()){
				instance.getServer().getPlayerExact(s).sendMessage(ChatColor.GREEN + "[LightCycle]Game starting in 10 seconds");
			}
		}
		iterations++;
		c.start();
	}

	public void addPlayer(Player p){
		readyplayers.put(p.getName(), false);
	}
	
	@Deprecated
	public void removePlayer(Player p){
		readyplayers.remove(p.getName());
		p.removeMetadata("lctagX", instance);
		p.removeMetadata("lctagY", instance);
		p.removeMetadata("lctagZ", instance);
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
			stop();
		}
	}
	
	public void stop(){
		reset();
		for(String s : readyplayers.keySet()){
			instance.getServer().getPlayerExact(s).removeMetadata("lctagX", instance);
			for(PotionEffect p : instance.getServer().getPlayerExact(s).getActivePotionEffects()){
				instance.getServer().getPlayerExact(s).removePotionEffect(p.getType());
			}
		}
		HandlerList.unregisterAll(this);
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
				leftArena(((Player)e.getEntity()).getName());
				if(readyplayers.size() == 1){
					Player p = instance.getServer().getPlayerExact(readyplayers.keySet().iterator().next());
					p.sendMessage(ChatColor.GREEN + "[LightCycle] wow! you win, here have a cookie");
					p.getInventory().addItem(new ItemStack(Material.COOKIE, 1));
					p.updateInventory();
					p.teleport(arena.getLobby());
					stop();
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e){
		if(hasStarted){
			if(readyplayers.keySet().contains(e.getPlayer().getName())){
				if(e.getPlayer().hasMetadata("lctagX")){
					Location l = new Location(arena.getLobby().getWorld(), e.getPlayer().getMetadata("lctagX").get(0).asInt(), e.getPlayer().getMetadata("lctagY").get(0).asInt(), e.getPlayer().getMetadata("lctagZ").get(0).asInt());
					if(!(e.getFrom().getBlockX() == l.getBlockX() && e.getFrom().getBlockY() == l.getBlockY() && e.getFrom().getBlockZ() == l.getBlockZ())){
						removeLater(e.getFrom());
						e.getPlayer().setMetadata("lctagX", new LazyMetadataValue(instance, new LocationCallable(e.getFrom().getBlockX())));
						e.getPlayer().setMetadata("lctagY", new LazyMetadataValue(instance, new LocationCallable(e.getFrom().getBlockY())));
						e.getPlayer().setMetadata("lctagZ", new LazyMetadataValue(instance, new LocationCallable(e.getFrom().getBlockZ())));
					}
				}else{
					e.getPlayer().setMetadata("lctagX", new LazyMetadataValue(instance, new LocationCallable(e.getFrom().getBlockX())));
					e.getPlayer().setMetadata("lctagY", new LazyMetadataValue(instance, new LocationCallable(e.getFrom().getBlockY())));
					e.getPlayer().setMetadata("lctagZ", new LazyMetadataValue(instance, new LocationCallable(e.getFrom().getBlockZ())));
				}
			}
		}
	}

	private void removeLater(final Location from){
		new BukkitRunnable(){
			@Override
			public void run(){
				arena.logRemove(from, from.getBlock().getType());
				from.getBlock().setType(Material.AIR);
			}
		}.runTaskLater(instance, 10);
	}
	
	@Override
	protected void finalize() throws Throwable{
		System.out.println("Finalized object: " + hashCode() + "(" + toString() + ") not important but it means that the GameInstance class for LightCycle is being removed and finalized propely");
		super.finalize();
	}
}
