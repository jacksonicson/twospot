package org.prot.controller.zookeeper.jobs;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class TryStopApp implements Job
{
	private static final Logger logger = Logger.getLogger(TryStopApp.class);

	private final String appId;

	public TryStopApp(String appId)
	{
		this.appId = appId;
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();

		String appNode = ZNodes.ZNODE_APPS + "/" + appId;
		try
		{
			// Load all childs (instance nodes) of the application node
			List<String> childs = zk.getChildren(appNode, false);

			// Count the instance nodes for this application
			int childCount = childs.size();

			// Only shutdown the instance if there are more than one instances
			// running
			boolean canShutdown = childCount > 1;

			// Return the result
			return canShutdown;

		} catch (KeeperException e)
		{
			logger.error(e);
			
			// Cannot stop the instance
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
		return false;
	}
}