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
				return true;

			case AUTH_FAILED:
				logger.error("Could not authenticate with ZooKeeper");
				System.exit(1);
				return true;

			case CLOSED:
				break;
			}
		}

		logger.info("Connecting with ZooKeeper...");
		try
		{
			// Execute the connect method in the ZooHelper
			zooHelper.connect();

			// Check the ZooKeeper state
			boolean state = zooHelper.getZooKeeper().getState() == States.CONNECTED;

			// If state is CONNECTED this job is done
			return state;

		} catch (IOException e)
		{
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
