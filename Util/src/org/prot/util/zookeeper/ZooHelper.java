package org.prot.util.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.ACL;

public class ZooHelper implements Watcher
{
	private static final Logger logger = Logger.getLogger(ZooHelper.class);

	private static final int SESSION_TIMEOUT = 3000;

	// ZooKeeper host
	private String host;

	// ZooKeeper port
	private int port;

	// Connection with the ZooKeeper
	private ZooKeeper zooKeeper;

	// Job-Queue which is used to communicate with the ZoopKeeper
	private JobQueue queue;

	public ZooHelper(String host, int port)
	{
		this.host = host;
		this.port = port;

		queue = new JobQueue(this);
	}

	final public JobQueue getQueue()
	{
		return queue;
	}

	final public void connect() throws InterruptedException, IOException
	{
		if (zooKeeper != null)
		{
			if (zooKeeper.getState() == States.CONNECTED)
				return;
			else
			{
				zooKeeper.close();
				zooKeeper = null;
			}
		}

		zooKeeper = new ZooKeeper(host + ":" + port, SESSION_TIMEOUT, this);
	}

	final public List<ACL> getACL()
	{
		List<ACL> acl = new ArrayList<ACL>();

		ACL all = new ACL();
		all.setId(ZooDefs.Ids.ANYONE_ID_UNSAFE);
		all.setPerms(ZooDefs.Perms.ALL);

		acl.add(all);

		return acl;
	}

	final public ZooKeeper getZooKeeper() throws InterruptedException, IOException
	{
		connect();
		return zooKeeper;
	}

	@Override
	public void process(WatchedEvent event)
	{
		// Do nothing
	}
}
