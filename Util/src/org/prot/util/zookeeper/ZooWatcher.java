package org.prot.util.zookeeper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ZooWatcher implements Watcher
{
	private static final Logger logger = Logger.getLogger(ZooWatcher.class);
	
	private List<FilteredWatcher> watchers = new ArrayList<FilteredWatcher>();
	
	public void addWatcher(FilteredWatcher watcher)
	{
		synchronized(watchers) {
			this.watchers.add(watcher);
		}
	}
	
	@Override
	public void process(WatchedEvent event)
	{
		for(FilteredWatcher watcher : watchers) 
		{
			if(watcher.matches(event))
				watcher.process(event); 
		}
	}
}
