package org.prot.controller.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.controller.config.Configuration;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class StopApp implements Job
{
	private static final Logger logger = Logger.getLogger(StopApp.class);

	private final String appId;

	public StopApp(String appId)
	{
		this.appId = appId;
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();

		String appNode = ZNodes.ZNODE_APPS + "/" + appId;
		String instanceNode = appNode + "/" + Configuration.getConfiguration().getUID();

		try
		{
			Stat stat = zk.exists(instanceNode, false);
			if (stat != null)
			{
				// Delete the instance node
				zk.delete(instanceNode, stat.getVersion());
				return true;
			}

			// Something went wrong - retry
			return false;

		} catch (KeeperException e)
		{
			switch (e.code())
			{
			case NONODE:
				break;
			default:
				logger.error("KeeperException", e);
				return false;
			}
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
