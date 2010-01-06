package org.prot.controller.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.controller.config.Configuration;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class StartApp implements Job
{
	private static final Logger logger = Logger.getLogger(StartApp.class);

	private final String appId;

	public StartApp(String appId)
	{
		this.appId = appId;
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();
		String instancePath = ZNodes.ZNODE_APPS + "/" + appId + "/"
				+ Configuration.getConfiguration().getUID();

		try
		{
			// Check if the path for the insatance node already exists
			Stat stat = zk.exists(instancePath, false);
			if (stat == null)
			{
				// The path does not exist - create it (empty node)
				zk.create(instancePath, new byte[0], zooHelper.getACL(), CreateMode.EPHEMERAL);
				return true;
			}

			logger.debug("Could not register instance in ZooKeeper");
			return false;

		} catch (KeeperException e)
		{
			logger.debug(e);

			switch (e.code())
			{
			case BADVERSION:
				// Retry
				return false;
			case NONODE:
				// Retry
				return false;
			}

			// Retry
			return false;
		}
	}

	@Override
	public void init(ZooHelper zooHelper)
	{
		// Do nothing
	}

	@Override
	public boolean isRetryable()
	{
		return true;
	}
}
