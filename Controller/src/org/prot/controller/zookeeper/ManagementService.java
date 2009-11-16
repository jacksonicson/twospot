package org.prot.controller.zookeeper;

import org.apache.log4j.Logger;
import org.prot.util.zookeeper.ZooHelper;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ManagementService
{
	private static final Logger logger = Logger.getLogger(ManagementService.class);

	// ZooKeeper helper
	private ZooHelper zooHelper;

	private String host;

	private String name;

	public void init()
	{
		zooHelper.getQueue().insert(new Register(host, name));
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

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
