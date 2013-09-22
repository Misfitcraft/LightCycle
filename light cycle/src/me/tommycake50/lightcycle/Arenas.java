package me.tommycake50.lightcycle;

import java.util.ArrayList;

public class Arenas{
	private static ArrayList<Arena> arenas = new ArrayList<Arena>();
	
	public static Arena getArenaByName(String name){
		for(Arena a : arenas){
			if(a.getName().equals(name)){
				return a;
			}
		}
		return null;
	}
	
	public void addArena(Arena a){
		arenas.add(a);
	}
	
	public static void removeArena(Arena a){
		arenas.remove(a);
		LightCycle.removeData(a.getName());
	}
	
	public ArrayList<Arena> getArenas() {
		return arenas;
	}
}
