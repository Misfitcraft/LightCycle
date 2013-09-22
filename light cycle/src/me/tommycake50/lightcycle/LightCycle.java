package me.tommycake50.lightcycle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LightCycle extends JavaPlugin{
	public WorldeditUtil weu;
	public static FileConfiguration arenas;
	private static File arenaSaveFile;
	private ArrayList<Arena> arenalist = new ArrayList<Arena>();
	
	@Override
	public void onEnable(){
		loadArenasFile();
		weu = new WorldeditUtil();
		CommandHandler ch = new CommandHandler(this);
		getCommand("lc").setExecutor(ch);
		getCommand("lightcycle").setExecutor(ch);
	}
	
	public GameInstance startMatch(Arena a){
		GameInstance gi = new GameInstance(this, a);
		GameInstanceHandler.currentgames.add(gi);
		return gi;
	}

	private void loadArenasFile(){
		if(!getDataFolder().exists()){
			getDataFolder().mkdir();
		}
		arenaSaveFile = new File(getDataFolder(), "arenas.yml");
		if(!arenaSaveFile.exists()){
			try {arenaSaveFile.createNewFile();}catch (IOException e){e.printStackTrace();}
		}
		arenas = YamlConfiguration.loadConfiguration(arenaSaveFile);
		if(arenas.getConfigurationSection("arenas") != null){
			arenas.createSection("arenas");
			saveArenas();
		}
	}

	private void saveArenas(){
		try{
			arenas.save(arenaSaveFile);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadArenas(){
		for(String s : arenas.getConfigurationSection("arenas").getKeys(false)){
			int minX, minZ, maxX, maxZ;
			Location lobby;
			World world;
			minX = arenas.getConfigurationSection("arenas").getConfigurationSection(s).getInt("minX");
			minZ = arenas.getConfigurationSection("arenas").getConfigurationSection(s).getInt("minZ");
			maxX = arenas.getConfigurationSection("arenas").getConfigurationSection(s).getInt("maxX");
			maxZ = arenas.getConfigurationSection("arenas").getConfigurationSection(s).getInt("maxZ");
			world = getServer().getWorld(arenas.getConfigurationSection("arenas").getConfigurationSection(s).getString("world"));
			lobby = new Location(getServer().getWorld(arenas.getConfigurationSection("arenas").getConfigurationSection(s).getString("lobbyworld")), arenas.getConfigurationSection("arenas").getConfigurationSection(s).getInt("lobbyx"), arenas.getConfigurationSection("arenas").getConfigurationSection(s).getInt("lobbyy"), arenas.getConfigurationSection("arenas").getConfigurationSection(s).getInt("lobbyz"));
			ArrayList<Location> spawns = new ArrayList<Location>();
			for(String s1 : arenas.getConfigurationSection("arenas." + s + ".spawns").getKeys(false)){
				spawns.add(new Location(getServer().getWorld(arenas.getConfigurationSection("arenas." + s + ".spawns." + s1).getString("world")), arenas.getConfigurationSection("arenas." + s + ".spawns." + s1).getDouble("x"), arenas.getConfigurationSection("arenas." + s + ".spawns." + s1).getDouble("y"), arenas.getConfigurationSection("arenas." + s + ".spawns." + s1).getDouble("z")));
			}
			File logfile = new File(getDataFolder(), s + ".blocklog");
			if(!logfile.exists()){
				logfile.getParentFile().mkdir();
				try {
					logfile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Arena add = new Arena(minX, minZ, maxX, maxZ, world, s, lobby, logfile);
			for(Location l : spawns){
				add.setSpawn(spawns.indexOf(l), l);
			}
			arenalist.add(add);
		}
	}

	public static void removeData(String name) {
		arenas.set("arenas." + name, null);
		try{
			arenas.save(arenaSaveFile);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
