package me.tommycake50.lightcycle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class WorldeditUtil{

	public Location getSelectionMinPoint(Player p){
		LocalSession session = WorldEdit.getInstance().getSession(p.getName());
		WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		Selection selection = worldEdit.getSelection(p);
		if(session != null){
			return new Location(selection.getMinimumPoint().getWorld(), selection.getMinimumPoint().getBlockX(), selection.getMinimumPoint().getBlockY(), selection.getMinimumPoint().getBlockZ());
		}else{
			return null;
		}
	}
	
	public Location getSelectionMaxPoint(Player p){
		LocalSession session = WorldEdit.getInstance().getSession(p.getName());
		WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		Selection selection = worldEdit.getSelection(p);
		if(session != null){
			return new Location(selection.getMaximumPoint().getWorld(), selection.getMaximumPoint().getBlockX(), selection.getMaximumPoint().getBlockY(), selection.getMaximumPoint().getBlockZ());
		}else{
			return null;
		}
	}
}
