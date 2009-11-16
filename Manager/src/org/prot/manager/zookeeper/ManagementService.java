package org.prot.manager.zookeeper;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.util.zookeeper.Job;
import org.prot.util.zookeeper.ZooHelper;

public class ManagementService
{
	private static final Logger logger = Logger.getLogger(ManagementService.class);

	// ZooKeeper helper
	private ZooHelper zooHelper;

	// list with ZooKeeper-Jobsd
	private List<Job> jobs;

	public void init()
	{
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
}
