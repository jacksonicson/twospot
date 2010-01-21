package org.prot.controller.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.JobState;
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
	public JobState execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		if (!zooHelper.checkConnection())
			return JobState.RETRY_LATER;

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
				return JobState.RETRY;
			}

			// Read the current node data
			byte[] data = zk.getData(appPath, false, stat);

			// Update the data of the node
			zk.setData(appPath, data, -1);

			// Node data has been saved
			return JobState.OK;

		} catch (KeeperException e)
		{
			throw e;
		} catch (InterruptedException e)
		{
			logger.error(e);
			return JobState.FAILED;

		} catch (IllegalArgumentException e)
		{
			logger.fatal(e);
			logger.fatal("Shutting down...");
			System.exit(1);
			return JobState.FAILED;
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
