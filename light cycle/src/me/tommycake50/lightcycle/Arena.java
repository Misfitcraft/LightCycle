package me.tommycake50.lightcycle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Arena implements Listener{
	private int minX;
	private int minZ;
	private int maxX;
	private int maxZ;
	private World world;
	HashMap<Integer, Location> spawns = new HashMap<Integer, Location>();
	private String name;
	private Location lobby;
	private File logfile;
	private boolean editMode;
	
	public Arena(int minX, int minZ, int maxX, int maxZ, World world, String name, Location lobby, File logfile){
		this.minX = minX;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxZ = maxZ;
		this.world = world;
		this.name = name;
		this.logfile = logfile;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBreakBlockEvent(BlockBreakEvent e){
		Location l = e.getBlock().getLocation();
		if(l.getBlockX() > minX && l.getBlockZ() > minZ && l.getBlockX() < maxX && l.getBlockZ() < maxZ && l.getWorld().equals(world) && !editMode){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "You cant break blocks here!");
		}
	}
	
	public void logRemove(Location l, Material m){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logfile, true));
			bw.append(l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + m.name());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void rollbackChanges(){
		try{
			BufferedReader br = new BufferedReader(new FileReader(logfile));
			String currline;
			while((currline = br.readLine()) != null){
				String[] splitline = currline.split(":");
				Location block = new Location(world, Integer.parseInt(splitline[0]), Integer.parseInt(splitline[1]), Integer.parseInt(splitline[2]));
				block.getBlock().setType(Material.valueOf(splitline[3]));
			}
			br.close();
			logfile.delete();
			logfile.createNewFile();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public String getName(){
		return name;
	}

	
	public void setName(String name){
		this.name = name;
	}
	
	public Location getSpawn(int num){
		return spawns.get(num);
	}
	
	public void setSpawn(Integer num, Location spawn){
		spawns.put(num, spawn);
	}

	public Location getLobby(){
		return lobby;
	}

	public void setLobby(Location lobby){
		this.lobby = lobby;
	}
	
	public void setEditMode(boolean enableEditMode){
		editMode = enableEditMode;
	}

	public boolean isInEditMode() {
		return editMode;
	}
}
