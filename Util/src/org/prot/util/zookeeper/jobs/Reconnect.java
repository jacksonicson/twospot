package org.prot.util.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper.States;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZooHelper;

public class Reconnect implements Job
{
	private static final Logger logger = Logger.getLogger(Reconnect.class);

	@Override
	public boolean execute(ZooHelper zooHelper) throws KeeperException, InterruptedException
	{
		if (zooHelper.getZooKeeper() != null)
		{
			switch (zooHelper.getZooKeeper().getState())
			{
			case ASSOCIATING:
			case CONNECTING:
			case CONNECTED:
				logger.debug("Already connected");
				return true;
			}
		}

		logger.info("Reconnecting with ZooKeeper");
		try
		{
			zooHelper.connect();
			boolean state = zooHelper.getZooKeeper().getState() == States.CONNECTED;
			logger.info("ZooKeeper connection state: " + state);
			return state;

		} catch (IOException e)
		{
			logger.error("IOException", e);
			return false;
		}
	}

	@Override
	public boolean isRetryable()
	{
		return true;
	}

	@Override
	public void init(ZooHelper zooHelper)
	{
	}
}
