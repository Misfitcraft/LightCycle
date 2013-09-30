package me.tommycake50.lightcycle;

import java.util.concurrent.Callable;

public class LocationCallable implements Callable<Object>{
	Object callable;
	
	public LocationCallable(int i){
		callable = i;
	}
	
	@Override
	public Object call() throws Exception{
		return callable;
	}
}
