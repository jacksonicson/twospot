package org.prot.util.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.prot.util.zookeeper.jobs.CreateZNodeStructure;
import org.prot.util.zookeeper.jobs.Reconnect;

public class JobQueue
{
	private static final Logger logger = Logger.getLogger(JobQueue.class);

	private ZooHelper zooHelper;

	private List<Job> jobQueue = new ArrayList<Job>();

	public JobQueue(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;

		insert(new CreateZNodeStructure());
	}

	public void insert(Job job)
	{
		jobQueue.add(job);
	}

	private void reconnect()
	{
		Reconnect reconnect = new Reconnect();
		jobQueue.add(0, reconnect);
	}

	public void run() throws KeeperException, InterruptedException, IOException
	{
		while (!jobQueue.isEmpty())
		{
			logger.info("processing job");

			// Get the next job
			Job job = jobQueue.get(0);

			// Retry this job?
			boolean retry = true;
			try
			{
				// Execute the job
				retry = job.execute(zooHelper);
			} catch (KeeperException e)
			{
				// Handle ZooKeeper errors
				switch (e.code())
				{
				case CONNECTIONLOSS:
				case SESSIONEXPIRED:
				case SESSIONMOVED:
					reconnect();
					break;
				}
				
				throw e; 
				
			} catch (InterruptedException e)
			{
				throw e; 
			} catch (IOException e)
			{
				throw e;
			}

			// Delete the job from the queue
			if (!retry)
				jobQueue.remove(job);
		}
	}
}
