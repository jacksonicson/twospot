package org.prot.manager.zookeeper;

import org.apache.log4j.Logger;
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
