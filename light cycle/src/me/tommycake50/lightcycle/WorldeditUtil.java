package me.tommycake50.lightcycle;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;

public class WorldeditUtil{

	public Vector getSelectionMinPoint(Player p){
		LocalSession session = WorldEdit.getInstance().getSession(p.getName());
		if(session != null){
			try{
				return session.getSelection(session.getSelectionWorld()).getMinimumPoint();
			}catch (IncompleteRegionException e){
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
	
	public Vector getSelectionMaxPoint(Player p){
		LocalSession session = WorldEdit.getInstance().getSession(p.getName());
		if(session != null){
			try{
				return session.getSelection(session.getSelectionWorld()).getMaximumPoint();
			}catch (IncompleteRegionException e){
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}

	public LocalWorld getSelectionWorld(Player sender) {
		return WorldEdit.getInstance().getSession(sender.getName()).getSelectionWorld();
	}
}
