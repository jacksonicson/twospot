package org.prot.controller.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.prot.util.ObjectSerializer;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.JobState;
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
	public JobState execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		if (!zooHelper.isConnected())
			return JobState.FAILED;

		ZooKeeper zk = zooHelper.getZooKeeper();
		String appPath = ZNodes.ZNODE_APPS + "/" + appId;

		// Create a new entry
		AppEntry entry = new AppEntry(appId);
		ObjectSerializer serializer = new ObjectSerializer();
		byte[] entryData = serializer.serialize(entry);

		try
		{
			// Persist the AppEntry
			zk.create(appPath, entryData, zooHelper.getACL(), CreateMode.PERSISTENT);
			return JobState.OK;

		} catch (KeeperException e)
		{
			switch (e.code())
			{
			case NODEEXISTS:
				break;

			default:
				throw e;
			}
		}

		// Update node data
		zk.setData(appPath, entryData, -1);
		return JobState.OK;
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
