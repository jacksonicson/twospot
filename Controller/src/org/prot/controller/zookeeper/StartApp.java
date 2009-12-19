package org.prot.controller.zookeeper;

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
		logger.debug("StartApp");
		
		ZooKeeper zk = zooHelper.getZooKeeper();
		String path = ZNodes.ZNODE_APPS + "/" + appId + "/" + Configuration.getConfiguration().getUID();
		
		try
		{
			Stat stat = zk.exists(path, false);
			if (stat != null)
			{
				zk.create(path, new byte[0], zooHelper.getACL(), CreateMode.EPHEMERAL);
				return true;
			}
		} catch (KeeperException e)
		{
			switch (e.code())
			{
			case BADVERSION:
				return false;
			case NONODE:
				logger.error("Could not register AppServer in ZooKepper - NONODE", e);
				return false;
			}

			logger.error(e);
			return false;
		}

		return true;
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
