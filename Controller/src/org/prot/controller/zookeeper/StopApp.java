package org.prot.controller.zookeeper;

import java.io.IOException;
import java.util.List;

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

	private final boolean updateZooKeeper;

	public StopApp(String appId, boolean updateZooKeeper)
	{
		this.appId = appId;
		this.updateZooKeeper = updateZooKeeper;
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();

		String appNode = ZNodes.ZNODE_APPS + "/" + appId;
		String instanceNode = appNode + "/" + Configuration.getConfiguration().getUID();
		try
		{
			List<String> childs = zk.getChildren(appNode, false);
			int childCount = childs.size();

			// If we should only check if there are more Controllers serving
			// this app
			if (!updateZooKeeper)
				return childCount > 0;

			Stat stat = zk.exists(instanceNode, false);
			if (stat != null)
			{
				zk.delete(instanceNode, stat.getVersion());
				return true;
			}

			return false;

		} catch (KeeperException e)
		{
			switch (e.code())
			{
			case BADVERSION:
				logger.debug("Could not update ApPEntry - BADVERSION");
				return false;
			case NONODE:
				logger.error("Could not update AppEntry in ZooKepper - NONODE", e);
				return false;
			}

			logger.error(e);
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
		return updateZooKeeper;
	}
}
