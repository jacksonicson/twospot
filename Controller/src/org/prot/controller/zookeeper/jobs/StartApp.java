package org.prot.controller.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.prot.controller.config.Configuration;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.JobState;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class StartApp implements Job
{
	private static final Logger logger = Logger.getLogger(StartApp.class);

	private final String appId;

	public StartApp(String appId)
	{
		this.appId = appId;
	}

	@Override
	public JobState execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		if (!zooHelper.isConnected())
			return JobState.RETRY_LATER;

		ZooKeeper zk = zooHelper.getZooKeeper();
		String instancePath = ZNodes.ZNODE_APPS + "/" + appId + "/"
				+ Configuration.getConfiguration().getUID();

		try
		{
			// The path does not exist - create it (empty node)
			zk.create(instancePath, new byte[0], zooHelper.getACL(), CreateMode.EPHEMERAL);

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
