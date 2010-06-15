/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.frontend.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.prot.frontend.config.Configuration;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.JobState;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;

public class WatchMaster implements Watcher, Job
{
	private static final Logger logger = Logger.getLogger(WatchMaster.class);
	
	private ZooHelper zooHelper;
	
	@Override
	public void process(WatchedEvent event)
	{
		logger.info("Processing Watcher: " + event.getPath()); 
		zooHelper.getQueue().insert(this);
	}

	@Override
	public JobState execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		ZooKeeper zk = zooHelper.getZooKeeper();

		Stat statMaster = zk.exists(ZNodes.ZNODE_MASTER, false);
		if (statMaster == null)
		{
			logger.info("Could not find " + ZNodes.ZNODE_MASTER + " in the ZooKeeper. Waiting for the ZNode");

			// Update the configuration
			Configuration.getConfiguration().setManagerAddress(null);
		} else
		{
			logger.info("Updating configuration with the Manager"); 
			
			// Update the configuration
			byte[] data = zk.getData(ZNodes.ZNODE_MASTER, this, statMaster);
			Configuration.getConfiguration().setManagerAddress(new String(data));
		}

		// Install a watcher
		zk.exists(ZNodes.ZNODE_MASTER, this);
		
		return JobState.OK;
	}

	@Override
	public boolean isRetryable()
	{
		return true;
	}

	@Override
	public void init(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper; 
	}
}
