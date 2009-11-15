package org.prot.util.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.ACL;

public class ZooHelper
{
	private static final Logger logger = Logger.getLogger(ZooHelper.class);

	private static final int SESSION_TIMEOUT = 3000;

	private String host;
	private int port;

	private ZooKeeper zooKeeper;
	private ZooWatcher watcher;

	public void init()
	{
		try
		{
			this.watcher = new ZooWatcher();
			this.zooKeeper = new ZooKeeper(host + ":" + port, SESSION_TIMEOUT, watcher);
			enforceDirStructure();
		} catch (IOException e)
		{
			logger.error("Could not start the ZooKeeper client", e);
			System.exit(1);
		} catch (KeeperException e)
		{
			logger.error("KeeperException while initializing ZooKeeper", e);
			System.exit(1);
		} catch (InterruptedException e)
		{
			logger.error("InterruptedException while initializing ZooKeeper", e);
			System.exit(1);
		}
	}

	private void enforceDirStructure() throws KeeperException, InterruptedException
	{
		try
		{
			zooKeeper.create(ZNodes.ZNODE_APPS, new byte[] {}, getACL(), CreateMode.PERSISTENT);
		} catch (NodeExistsException e)
		{
			// Do nothing
		}

		try
		{
			zooKeeper.create(ZNodes.ZNODE_CONTROLLER, new byte[] {}, getACL(), CreateMode.PERSISTENT);
		} catch (NodeExistsException e)
		{
			// Do nothing
		}
	}

	public List<ACL> getACL()
	{
		List<ACL> acl = new ArrayList<ACL>();

		ACL all = new ACL();
		all.setId(ZooDefs.Ids.ANYONE_ID_UNSAFE);
		all.setPerms(ZooDefs.Perms.ALL);

		acl.add(all);

		return acl;
	}

	public void addWatcher(FilteredWatcher watcher)
	{
		this.watcher.addWatcher(watcher);
	}

	public ZooKeeper getZooKeeper()
	{
		return zooKeeper;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
}
