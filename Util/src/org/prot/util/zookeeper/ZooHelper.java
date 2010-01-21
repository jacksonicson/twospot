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

	// Listeners
	private List<SynchronizationListener> listeners = new ArrayList<SynchronizationListener>();

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

		// Connect
		reconnect();
	}

	public void addListener(SynchronizationListener listener)
	{
		synchronized (listeners)
		{
			listeners.add(listener);
		}
	}

	private void listenerReconnect()
	{
		ArrayList<SynchronizationListener> copy = new ArrayList<SynchronizationListener>();
		synchronized (listeners)
		{
			copy.addAll(listeners);
		}

		for (SynchronizationListener listener : copy)
			listener.reconnected(this);
	}

	@Override
	public void process(WatchedEvent event)
	{
		logger.info("ZooKeeper event");
		switch (event.getState())
		{
		case Expired:
			logger.info("ZooKeeper session expired");
			break;
		case Disconnected:
			logger.info("ZooKeeper disconnected");
			break;
		default:
			logger.debug("Unhandled ZooKeeper event");
			return;
		}

		reconnect();
	}

	final synchronized void reconnect()
	{
		if (isConnected())
			return;

		zooKeeper = null;
		connect();
		listenerReconnect();
		queue.reconnected();
		queue.proceed();
	}

	private synchronized boolean isConnected()
	{
		if (zooKeeper == null)
			return false;

		return zooKeeper.getState() == States.CONNECTED;
	}

	public boolean checkConnection()
	{
		boolean connected = isConnected();

		if (!connected)
			reconnect();

		return connected;
	}

	private synchronized final void connect()
	{
		try
		{
			while (zooKeeper == null)
			{
				logger.info("ZooKeeper is connecting...");

				// Create a new ZooKeeper instance
				ZooKeeper connection = new ZooKeeper(host + ":" + port, SESSION_TIMEOUT, this);

				// Wait while ZooKeeper is CONNECTING
				while (connection.getState() == States.CONNECTING)
				{
					logger.debug("ZooKeeper is waiting for connection ...");
					Thread.sleep(1000);
				}

				// Check if it is connected
				if (connection.getState() == States.CONNECTED)
				{
					logger.info("Connected with ZooKeeper");
					zooKeeper = connection;
				} else
				{
					logger.warn("Could not connect with ZooKeeper...");
					Thread.sleep(3000);
				}
			}
		} catch (InterruptedException e)
		{
			logger.error("InterruptedException", e);
			System.exit(1);
		} catch (IOException e)
		{
			logger.error("IOException", e);
			System.exit(1);
		}
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

	public final JobQueue getQueue()
	{
		return queue;
	}

	public final ZooKeeper getZooKeeper()
	{
		return zooKeeper;
	}
}
