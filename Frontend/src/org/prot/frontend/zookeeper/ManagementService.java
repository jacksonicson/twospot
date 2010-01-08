package org.prot.frontend.zookeeper;

import org.apache.log4j.Logger;
import org.prot.frontend.zookeeper.jobs.WatchMaster;
import org.prot.util.zookeeper.ZooHelper;

public class ManagementService
{
	private static final Logger logger = Logger.getLogger(ManagementService.class);

	private ZooHelper zooHelper;

	public void init()
	{
		// No connection jobs
		zooHelper.setup();
		zooHelper.getQueue().insert(new WatchMaster());
	}

	public void setZooHelper(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}
}
