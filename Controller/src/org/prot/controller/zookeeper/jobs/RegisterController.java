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
package org.prot.controller.zookeeper.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.prot.controller.config.Configuration;
import org.prot.util.ObjectSerializer;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.JobState;
import org.prot.util.zookeeper.ZNodes;
import org.prot.util.zookeeper.ZooHelper;
import org.prot.util.zookeeper.data.ControllerEntry;

public class RegisterController implements Job, Watcher
{
	private static final Logger logger = Logger.getLogger(RegisterController.class);

	private ZooHelper zooHelper;

	private String controllerPath = null;

	@Override
	public void process(WatchedEvent event)
	{
		switch (event.getType())
		{
		case NodeDeleted:
		case NodeDataChanged:
			zooHelper.getQueue().insert(this);
		}
	}

	private byte[] getControllerEntry()
	{
		// Create a new ControllerEntry object which serialized version is saved
		// to the ZooKeeper
		ControllerEntry entry = new ControllerEntry();
		entry.serviceAddress = Configuration.getConfiguration().getAddress();
		entry.address = Configuration.getConfiguration().getAddress();
		entry.port = Configuration.getConfiguration().getControllerPort();

		// Serialize the ControllerEntry object
		ObjectSerializer serializer = new ObjectSerializer();
		byte[] entryData = serializer.serialize(entry);

		return entryData;
	}

	@Override
	public JobState execute(ZooHelper zooHelper) throws KeeperException, InterruptedException, IOException
	{
		if (!zooHelper.checkConnection())
			return JobState.RETRY_LATER;

		ZooKeeper zk = zooHelper.getZooKeeper();

		// Create a controller path if necessary
		if (controllerPath == null)
			controllerPath = ZNodes.ZNODE_CONTROLLER + "/" + Configuration.getConfiguration().getUID();

		// Create a new controller entry
		byte[] entryData = getControllerEntry();

		// Try creating a new node
		try
		{
			// Created node
			String createdPath = zk.create(controllerPath, entryData, zooHelper.getACL(),
					CreateMode.EPHEMERAL);
		} catch (KeeperException e)
		{
			switch (e.code())
			{
			case NODEEXISTS:
				break;
			default:
				throw e;
			}
		}

		return JobState.OK;
	}

	@Override
	public void init(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}

	@Override
	public boolean isRetryable()
	{
		return true;
	}
}
