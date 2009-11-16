package org.prot.manager.zookeeper;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.prot.manager.zookeeper.jobs.RegisterMaster;
import org.prot.util.zookeeper.ZooHelper;

public class ManagementService
{
	private static final Logger logger = Logger.getLogger(ManagementService.class);

	// ZooKeeper helper
	private ZooHelper zooHelper;

	// Hostname of the master
	private String host;

	// Port of the master
	private int port;

	public void init()
	{
		zooHelper.getQueue().insert(new RegisterMaster(host, port));
		try
		{
			zooHelper.getQueue().run();
		} catch (KeeperException e)
		{
			// Do nothing
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void setZooHelper(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
}
