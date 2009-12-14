package org.prot.controller.zookeeper;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class DeployApp implements Job
{
	private static final Logger logger = Logger.getLogger(DeployApp.class);

	private final String appId;

	public DeployApp(String appId)
	{
		this.appId = appId;
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();

		String path = ZNodes.ZNODE_APPS + "/" + appId;
		Stat stat = null;
		try
		{
			stat = zk.exists(path, true);
			if (stat == null)
			{
				logger.fatal("ZooKeeper has no such app - creating node first: " + appId);
				zooHelper.getQueue().insert(new RegisterApp(appId));
				return false;
			}

			zk.setData(path, appId.getBytes(), stat.getVersion());
			logger.debug("App deployed under: " + path);

		} catch (KeeperException e)
		{
			// Retry
			return false;
		} catch (InterruptedException e)
		{
			// Retry
			return false;
		} catch (IllegalArgumentException e)
		{
			logger.error("Could not update deployment data in ZooKeeper for: " + appId);
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
