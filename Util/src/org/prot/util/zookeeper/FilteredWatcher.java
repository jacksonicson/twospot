package org.prot.util.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public interface FilteredWatcher extends Watcher
{
	public boolean matches(WatchedEvent e);
}
