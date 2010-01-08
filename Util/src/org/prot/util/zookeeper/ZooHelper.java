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

	private static final int SESSION_TIMEOUT = 4000;

	// ZooKeeper host and port
	private String host;
	private int port;

	// ZooKeeper connection
	private ZooKeeper zooKeeper;

	// Job queue which manages the execution of all ZooKeeper jobs
	private JobQueue queue;

	public ZooHelper(String host, int port)
	{
		this.host = host;
		this.port = port;

		// Create a new job queue
		queue = new JobQueue(this);
	}

	public void setup()
	{
		// Prevent queue from accepting further connection jobs
		queue.finishSetup();

		// Process all connection jobs
		queue.connectionProcess(null);
	}

	public final JobQueue getQueue()
	{
		return queue;
	}

	public final void connect() throws InterruptedException, IOException
	{
		// Check if there is already an instance of ZooKeeper
		if (zooKeeper != null)
		{
			// Check the state of the ZooKeeper instance
			if (zooKeeper.getState() == States.CONNECTED)
			{
				// Everythin is ok
				return;
			} else
			{
				// We need to reconnect with ZooKeeper
				zooKeeper.close();
				zooKeeper = null;
			}
		}

		// Create a new ZooKeeper instance
		zooKeeper = new ZooKeeper(host + ":" + port, SESSION_TIMEOUT, this);

		// Wait while ZooKeeper is CONNECTING
		while (zooKeeper.getState() == States.CONNECTING)
		{
			logger.debug("ZooKeeper is connecting...");
			Thread.sleep(1000);
		}

		// Log the current connection state
		logger.info("ZooKeeper connection state: " + zooKeeper.getState());
	}

	public final List<ACL> getACL()
	{
		List<ACL> acl = new ArrayList<ACL>();

		ACL all = new ACL();
		all.setId(ZooDefs.Ids.ANYONE_ID_UNSAFE);
		all.setPerms(ZooDefs.Perms.ALL);

		acl.add(all);

		return acl;
	}

	public final ZooKeeper getZooKeeper()
	{
		return zooKeeper;
	}

	@Override
	public void process(WatchedEvent event)
	{
		logger.debug("ZooKeeper connection event: " + event);
		queue.connectionProcess(event);
	}
}
