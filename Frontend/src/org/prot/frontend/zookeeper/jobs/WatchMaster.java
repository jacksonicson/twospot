package org.prot.frontend.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.frontend.Configuration;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class WatchMaster implements Watcher, Job
{
	private static final Logger logger = Logger.getLogger(WatchMaster.class);
	
	private ZooHelper zooHelper;
	
	@Override
	public void process(WatchedEvent event)
	{
		zooHelper.getQueue().insert(this);
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
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
		zk.exists(ZNodes.ZNODE_MASTER, this);
		
		return true;
	}

	@Override
	public boolean isRetryable()
	{
		return true;
	}

	@Override
	public void init(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper; 
	}
}