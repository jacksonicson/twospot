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

		return execute(job, false);
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

	public void requires(Job target, Job toInsert)
	{
		synchronized (jobQueue)
		{
			int index = jobQueue.indexOf(target);
			index--;
			if (index < 0)
				index = 0;

			jobQueue.add(index, toInsert);
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
			}

			boolean deleteJob = execute(todo, true);

			if (deleteJob)
			{
				synchronized (jobQueue)
				{
					jobQueue.remove(todo);
				}
			}
		}
	}

	private boolean execute(Job job, boolean queued)
	{
		logger.info("processing job");

		// Setup the job
		job.init(zooHelper);

		// Retry this job?
		boolean jobState = false;
		try
		{
			// Execute the job
			jobState = job.execute(zooHelper);

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

		// If the job is executed without the queue
		if (queued == false)
			return jobState;

		// Check if job is retryable
		if (jobState == false && job.isRetryable() == false)
			jobState = true;

		// Return the jobState
		return jobState;
	}
}
