package org.prot.controller.zookeeper.jobs;

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
		String appPath = ZNodes.ZNODE_APPS + "/" + appId;
		try
		{
			// Check if a path for the AppId exists
			Stat stat = zk.exists(appPath, false);

			// Path does not exist
			if (stat == null)
			{
				// First we need to register the ZooKeeper-Node for the AppId
				zooHelper.getQueue().requires(this, new RegisterApp(appId));

				// We are not done with this task
				return false;
			}

			// Read the current node data
			byte[] data = zk.getData(appPath, false, stat);

			// Update the data of the node
			zk.setData(appPath, data, stat.getVersion());

			// Node data has been saved
			return true;

		} catch (KeeperException e)
		{
			logger.error("KeeperError", e);

			// Retry
			return false;

		} catch (InterruptedException e)
		{
			logger.error(e);

			// Retry
			return false;

		} catch (IllegalArgumentException e)
		{
			logger.fatal(e);
			logger.fatal("Shutting down...");
			System.exit(1);
		}

		return false;
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
