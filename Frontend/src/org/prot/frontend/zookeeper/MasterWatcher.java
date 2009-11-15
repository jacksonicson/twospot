package org.prot.frontend.zookeeper;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.frontend.Configuration;
import org.prot.util.zookeeper.FilteredWatcher;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class MasterWatcher implements FilteredWatcher
{
	private static final Logger logger = Logger.getLogger(MasterWatcher.class);

	private ZooHelper zooHelper;

	public MasterWatcher(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}

	@Override
	public boolean matches(WatchedEvent e)
	{
		return e.getPath().equals(ZNodes.ZNODE_MASTER);
	}

	@Override
	public void process(WatchedEvent event)
	{
		try
		{
			lookupMaster();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (KeeperException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	void lookupMaster() throws IOException, KeeperException, InterruptedException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();

		Stat statMaster = zk.exists(ZNodes.ZNODE_MASTER, false);
		if (statMaster == null)
		{
			logger.info("Could not find " + ZNodes.ZNODE_MASTER + " in the ZooKeeper. Waiting for the ZNode");

			// Update the configuration
			Configuration.get().setManagerAddress(null);
		} else
		{
			// Update the configuration
			byte[] data = zk.getData(ZNodes.ZNODE_MASTER, true, statMaster);
			Configuration.get().setManagerAddress(new String(data));
		}

		// Install a watcher
		zk.exists(ZNodes.ZNODE_MASTER, true);
	}
}
