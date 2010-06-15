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
package org.prot.manager.zookeeper;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZooHelper;

public class SynchronizationService
{
	private static final Logger logger = Logger.getLogger(SynchronizationService.class);

	// ZooKeeper helper
	private ZooHelper zooHelper;

	// List of connection jobs
	private List<Job> connectionJobs;

	// List of jobs
	private List<Job> jobs;

	public void init()
	{
		// Connection jobs
		for (Job job : connectionJobs)
		{
			logger.debug("Adding connection job: " + job.getClass());
			zooHelper.getQueue().insertConnectionJob(job);
		}

		// Setup
		zooHelper.setup();

		// Other jobs
		for (Job job : jobs)
			zooHelper.getQueue().insert(job);
	}

	public void setZooHelper(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;
	}

	public void setJobs(List<Job> jobs)
	{
		this.jobs = jobs;
	}

	public void setConnectionJobs(List<Job> jobs)
	{
		this.connectionJobs = jobs;
	}
}
