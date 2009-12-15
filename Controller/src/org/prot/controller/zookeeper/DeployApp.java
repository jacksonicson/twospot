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

		try
		{
			String path = ZNodes.ZNODE_APPS + "/" + appId;

			// Check if the path exists, don't watch the node
			Stat stat = zk.exists(path, false);
			if (stat == null)
			{
				logger.error("ZooKeeper has no such app - creating node first: " + appId);

				// Enqueue a register task before this task
				zooHelper.getQueue().insertBefore(this, new RegisterApp(appId));

				// Reschedule this task
				return false;
			}

			// Update node data
			zk.setData(path, appId.getBytes(), stat.getVersion());

			logger.debug("ZooKeeper updated: " + path);

		} catch (KeeperException e)
		{
			logger.error(e);
			return false;
		} catch (InterruptedException e)
		{
			logger.error(e);
			return false;
		} catch (IllegalArgumentException e)
		{
			logger.fatal(e);
			System.exit(1);
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
