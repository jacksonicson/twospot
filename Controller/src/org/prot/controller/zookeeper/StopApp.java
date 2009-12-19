package org.prot.controller.zookeeper;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.util.ObjectSerializer;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;
import org.prot.util.zookeeper.data.AppEntry;

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

		String path = ZNodes.ZNODE_APPS + "/" + appId;
		try
		{
			Stat stat = new Stat();
			byte[] data = zk.getData(path, false, stat);
			if (data != null)
			{
				ObjectSerializer serializer = new ObjectSerializer();
				AppEntry entry = (AppEntry) serializer.deserialize(data);
				entry.serverInstances--;
				if (entry.serverInstances < 0)
				{
					logger.error("AppEntry serverInstances is negative: " + entry.serverInstances);
					entry.serverInstances = 0;
				}

				if (!updateZooKeeper)
					return entry.serverInstances > 0;

				if (entry.serverInstances > 0)
				{
					data = serializer.serialize(entry);
					zk.setData(path, data, stat.getVersion());
					return true;
				}

				return false;

			} else
			{
				logger.error("ZooKeeper has no such Node (App is not registered): " + path);
				return false;
			}
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
