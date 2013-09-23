package me.tommycake50.lightcycle;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor{
	LightCycle instance;
	
	public CommandHandler(LightCycle instance) {
		this.instance = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if((cmd.getName().equalsIgnoreCase("lc") || cmd.getName().equalsIgnoreCase("lightcycle")) && sender instanceof Player){
			if(args.length == 2){
				if(sender instanceof Player){
					if(args[0].equalsIgnoreCase("definearena")){
						saveArena(args[1], (Player)sender);
					}
					
					if(args[0].equalsIgnoreCase("setlobby")){
						setLobby(args[1], (Player)sender);
					}
					if(args[0].equalsIgnoreCase("deletearena")){
						if(Arenas.getArenaByName(args[1]) == null)return false;
						Arenas.removeArena(Arenas.getArenaByName(args[1]));
					}
					if(args[0].equalsIgnoreCase("join")){
						if(Arenas.getArenaByName(args[1]) != null){
							for(GameInstance g : GameInstanceHandler.currentgames){
								if(g.getArena().equals(Arenas.getArenaByName(args[1])) && g.isInLobby()){
									g.addPlayer((Player)sender);
								}else if(g.getArena().equals(Arenas.getArenaByName(args[1])) && !g.isInLobby()){
									sender.sendMessage(ChatColor.RED + "That arena is already ingame");
								}else{
									GameInstance g1 = instance.startMatch(Arenas.getArenaByName(args[1]));
									g1.addPlayer((Player)sender);
									g1.startCountdown();
								}
							}
						}else{
							sender.sendMessage(ChatColor.RED + "That arena does not exist!");
						}
					}
				}else{
					sender.sendMessage("Only players can use this command >:C!");
				}
			}else if(args.length == 0 || args.length >= 4){
				sendHelp(sender);
			}else if(args.length == 3){
				if(args[0].equalsIgnoreCase("setspawn")){
					setSpawn(args[1], (Player)sender, Integer.parseInt(args[2]));
				}
			}else if(args.length == 1){
				if(args[0].equalsIgnoreCase("leave")){
					instance.getServer().getPluginManager().callEvent(new PlayerLeaveArenaEvent<Player>((Player)sender));
				}
			}
		}
		return true;
	}

	private void setSpawn(String arena, Player sender, int spawnnum){
		ConfigurationSection cs = LightCycle.arenas.getConfigurationSection("arenas." + arena + ".spawns." + spawnnum);
		cs.set("x", sender.getLocation().getBlockX());
		cs.set("y", sender.getLocation().getBlockY());
		cs.set("z", sender.getLocation().getBlockZ());
		cs.set("world", sender.getLocation().getWorld().getName());
	}
	
	private void setLobby(String arena, Player sender) {
		ConfigurationSection cs = LightCycle.arenas.getConfigurationSection("arenas." + arena);
		cs.set("lobbyx", sender.getLocation().getBlockX());
		cs.set("lobbyy", sender.getLocation().getBlockY());
		cs.set("lobbyz", sender.getLocation().getBlockZ());
		cs.set("lobbyworld", sender.getLocation().getWorld().getName());
	}
	
	private void saveArena(String arenaname, Player sender) {
		if((instance.weu.getSelectionMinPoint(sender) != null) && (instance.weu.getSelectionMaxPoint(sender) != null)){
			ConfigurationSection cs = LightCycle.arenas.createSection("arenas." + arenaname);
			cs.set("minX", instance.weu.getSelectionMinPoint(sender).getX());
			cs.set("minZ", instance.weu.getSelectionMinPoint(sender).getZ());
			cs.set("maxX", instance.weu.getSelectionMaxPoint(sender).getX());
			cs.set("maxZ", instance.weu.getSelectionMaxPoint(sender).getZ());
			cs.set("world", instance.weu.getSelectionMinPoint((sender)).getWorld().getName());
			Arenas.removeArena(null);
			instance.loadArenas();
		}else{								
			sender.sendMessage(ChatColor.DARK_RED + "Please make a worldedit selection before trying to define an arena!");
		}
	}

	private void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "[LightCycle] available arguments are: ");
		sender.sendMessage(ChatColor.GOLD + "1." + ChatColor.BLUE + "definearena");
		sender.sendMessage(ChatColor.GOLD + "2." + ChatColor.BLUE + "join");
		sender.sendMessage(ChatColor.GOLD + "3." + ChatColor.BLUE + "leave");
		sender.sendMessage(ChatColor.GOLD + "4." + ChatColor.BLUE + "deletearena");
		sender.sendMessage(ChatColor.GOLD + "5." + ChatColor.BLUE + "setspawn number");
		sender.sendMessage(ChatColor.GOLD + "13." + ChatColor.BLUE + "setlobby");
	}
	
}
