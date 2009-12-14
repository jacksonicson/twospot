package org.prot.controller.zookeeper;

import org.apache.log4j.Logger;
import org.prot.util.zookeeper.ZooHelper;

public class ManagementService
{
	private static final Logger logger = Logger.getLogger(ManagementService.class);

	// ZooKeeper helper
	private ZooHelper zooHelper;

	private String networkInterface;

	private WatchApp watchApp = new WatchApp();

	public void init()
	{
		zooHelper.getQueue().insert(new Register(networkInterface));
		zooHelper.getQueue().insert(watchApp);
	}

	public void registerApp(String appId)
	{
		zooHelper.getQueue().insertAndWait(new RegisterApp(appId));
	}

	public void deployApp(String appId, String version)
	{
		zooHelper.getQueue().insert(new DeployApp(appId));
	}

	public void addDeploymentListener(DeploymentListener listener, String appId)
	{
		watchApp.addDeploymentListener(listener, appId);
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
