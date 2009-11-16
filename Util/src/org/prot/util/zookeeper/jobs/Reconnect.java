package org.prot.util.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZooHelper;

public class Reconnect implements Job
{
	private static final Logger logger = Logger.getLogger(Reconnect.class);

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException
	{
		try
		{
			logger.info("reconnecting with zookeeper");
			zooHelper.connect();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean isRetryable()
	{
		return false;
	}

	@Override
	public void init(ZooHelper zooHelper)
	{
	}
}
