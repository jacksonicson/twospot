package org.prot.controller.zookeeper;

import org.apache.log4j.Logger;
import org.prot.util.zookeeper.ZooHelper;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ManagementService
{
	private static final Logger logger = Logger.getLogger(ManagementService.class);

	// ZooKeeper helper
	private ZooHelper zooHelper;

	private String networkInterface;

	public void init()
	{
		zooHelper.getQueue().insert(new Register(networkInterface));
	}

	public void registerApp(String appId)
	{
		throw new NotImplementedException();
	}

	public void deleteApp(String appId)
	{
		throw new NotImplementedException();
	}

	public void setZooHelper(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}

	public void setNetworkInterface(String networkInterface)
	{
		this.networkInterface = networkInterface;
	}
}
