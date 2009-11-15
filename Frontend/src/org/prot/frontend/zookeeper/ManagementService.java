package org.prot.frontend.zookeeper;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.prot.util.zookeeper.ZooHelper;

public class ManagementService
{
	private static final Logger logger = Logger.getLogger(ManagementService.class);

	private ZooHelper zooHelper;

	public void init()
	{
		MasterWatcher masterWatcher = new MasterWatcher(zooHelper);
		zooHelper.addWatcher(masterWatcher);
		try
		{
			masterWatcher.lookupMaster();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (KeeperException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
	}

	public void setZooHelper(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}
}
