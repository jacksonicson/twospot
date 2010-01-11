package org.prot.controller.zookeeper;

import org.prot.controller.zookeeper.jobs.DeployApp;
import org.prot.controller.zookeeper.jobs.RegisterApp;
import org.prot.controller.zookeeper.jobs.RegisterController;
import org.prot.controller.zookeeper.jobs.StartApp;
import org.prot.controller.zookeeper.jobs.StopApp;
import org.prot.controller.zookeeper.jobs.TryStopApp;
import org.prot.controller.zookeeper.jobs.WatchApp;
import org.prot.util.zookeeper.ZooHelper;

public class ManagementService
{
	// ZooKeeper helper
	private ZooHelper zooHelper;

	private WatchApp watchApp = new WatchApp();

	public void init()
	{
		zooHelper.getQueue().insertConnectionJob(new RegisterController());
		zooHelper.setup();

		zooHelper.getQueue().insert(watchApp);
	}

	public boolean registerApp(String appId)
	{
		return zooHelper.getQueue().insertAndWait(new RegisterApp(appId));
	}

	public boolean stop(String appId)
	{
		return zooHelper.getQueue().insertAndWait(new StopApp(appId));
	}

	public boolean tryStop(String appId)
	{
		return zooHelper.getQueue().insertAndWait(new TryStopApp(appId));
	}

	public void start(String appId)
	{
		zooHelper.getQueue().insert(new StartApp(appId));
	}

	public void deployApp(String appId, String version)
	{
		zooHelper.getQueue().insert(new DeployApp(appId));
	}

	public void addDeploymentListener(DeploymentListener listener)
	{
		watchApp.addDeploymentListener(listener);
	}

	public void watchApp(String appId)
	{
		watchApp.watchApp(appId);
	}

	public void removeWatch(String appId)
	{
		watchApp.removeWatch(appId);
	}

	public void setZooHelper(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}
}
