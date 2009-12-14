package org.prot.util.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.prot.util.zookeeper.jobs.CreateZNodeStructure;
import org.prot.util.zookeeper.jobs.Reconnect;

public class JobQueue implements Runnable
{
	private static final Logger logger = Logger.getLogger(JobQueue.class);

	private ZooHelper zooHelper;

	private List<Job> jobQueue = new ArrayList<Job>();

	public JobQueue(ZooHelper zooHelper)
	{
		this.zooHelper = zooHelper;

		insert(new CreateZNodeStructure());
	}

	public boolean insertAndWait(Job job)
	{
		// Cannot retry blocking jobs
		assert (job.isRetryable() == false);

		return execute(job);
	}

	public void insert(Job job)
	{
		synchronized (jobQueue)
		{
			jobQueue.add(job);
			jobQueue.notify();
		}

		run();
	}

	private void reconnect()
	{
		Reconnect reconnect = new Reconnect();
		jobQueue.add(0, reconnect);
	}

	public void run()
	{
		// while (true)
		{

			Job todo = null;

			synchronized (jobQueue)
			{
				// while (jobQueue.isEmpty())
				// {
				// try
				// {
				// jobQueue.wait();
				// } catch (InterruptedException e)
				// {
				// logger.error("ZooKeeper JobQueue failed", e);
				// System.exit(1);
				// }
				// }

				todo = jobQueue.get(0);
				jobQueue.remove(0);
			}

			boolean deleteJob = execute(todo);

			if (!deleteJob)
			{
				synchronized (jobQueue)
				{
					jobQueue.add(todo);
				}
			}
		}
	}

	private boolean execute(Job job)
	{
		logger.info("processing job");

		// Setup the job
		job.init(zooHelper);

		// Retry this job?
		boolean deleteJob = false;
		try
		{
			// Execute the job
			deleteJob = job.execute(zooHelper);

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

		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// Check if job is retryable
		if (!deleteJob && !job.isRetryable())
			deleteJob = true;

		return deleteJob;
	}
}
