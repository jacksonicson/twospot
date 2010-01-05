package org.prot.controller.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.Code;
import org.prot.util.ObjectSerializer;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;
import org.prot.util.zookeeper.data.AppEntry;

public class RegisterApp implements Job
{
	private static final Logger logger = Logger.getLogger(RegisterApp.class);

	private final String appId;

	public RegisterApp(String appId)
	{
		this.appId = appId;
	}

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();
		String appPath = ZNodes.ZNODE_APPS + "/" + appId;

		// Create a new AppEntry object which serialized version is saved to the
		// ZooKeeper
		AppEntry entry = new AppEntry(appId);
		ObjectSerializer serializer = new ObjectSerializer();
		byte[] entryData = serializer.serialize(entry);

		try
		{
			// Persist the AppEntry
			String createdPath = zk.create(appPath, entryData, zooHelper.getACL(), CreateMode.PERSISTENT);
			logger.info("Application written to ZooKeeper: " + createdPath);
			return true;

		} catch (KeeperException e)
		{
			if (e.code() == Code.NODEEXISTS)
				return true;

			logger.error("Could not register within ZooKeeper. Registration path already exists: " + appPath);
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
