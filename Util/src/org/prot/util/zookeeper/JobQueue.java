package org.prot.util.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.prot.util.zookeeper.jobs.CreateZNodeStructure;
import org.prot.util.zookeeper.jobs.Reconnect;

public class JobQueue
{
	private static final Logger logger = Logger.getLogger(JobQueue.class);

	private final ZooHelper zooHelper;

	private boolean setup = true;

	private List<Job> connectionJobs = new ArrayList<Job>();

	private List<Job> jobQueue = new ArrayList<Job>();

	JobQueue(ZooHelper zooHelper)
	{
		// ZooHelper reference
		this.zooHelper = zooHelper;

		// Add default connections jobs
		connectionJobs.add(new Reconnect());

		// Add default jobs
		jobQueue.add(new CreateZNodeStructure());
	}

	/**
	 * Processes all connection jobs
	 * 
	 * @param event
	 *            may be null
	 */
	void connectionProcess(WatchedEvent event)
	{
		synchronized (jobQueue)
		{
			// Add all connections jobs at the beginning of the job queue
			for (int i = 0; i < connectionJobs.size(); i++)
				jobQueue.add(i, connectionJobs.get(i));
		}

		// Run the job queue
		run();
	}

	/**
	 * Adds a new connection job. Each connection job is executed if the
	 * ZooKeeper connection watcher is called.
	 * 
	 * @param job
	 */
	public void insertConnectionJob(Job job)
	{
		assert (setup == false);
		connectionJobs.add(job);
	}

	void finishSetup()
	{
		setup = false;
	}

	/**
	 * Insert a new job and execute it. The method call blocks until the job ad
	 * all required jobs have been processed
	 * 
	 * @param job
	 * @return
	 */
	public boolean insertAndWait(Job job)
	{
		// Jobs must not be retryable
		assert (job.isRetryable() == false);

		// Execute a job without a queue
		return execute(job, false);
	}

	/**
	 * Insert a new job into the queue.
	 * 
	 * @param job
	 */
	public void insert(Job job)
	{
		synchronized (jobQueue)
		{
			jobQueue.add(job);
			jobQueue.notify();
		}

		// Run the job queue
		run();
	}

	public void requires(Job target, Job toInsert)
	{
		synchronized (jobQueue)
		{
			// Check inde of the job
			int index = jobQueue.indexOf(target);

			// Decrement the index
			index--;

			// Index must not be negative
			if (index < 0)
				index = 0;

			// Insert the required job before the target job
			jobQueue.add(index, toInsert);
			jobQueue.notify();
		}

		// Run the job queue
		run();
	}

	private Boolean isRunning = false;

	public void run()
	{
		synchronized (isRunning)
		{
			isRunning = true;

			while (!jobQueue.isEmpty())
			{
				Job todo = null;
				synchronized (jobQueue)
				{
					if (jobQueue.isEmpty())
						return;

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

			isRunning = false;
		}
	}

	private synchronized boolean execute(Job job, boolean queued)
	{
		logger.info("processing job: " + job.getClass().getName());

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
			logger.error("Keeper exception: " + e.code());
			logger.trace("Keeper exception", e);
		} catch (InterruptedException e)
		{
			logger.error("InterruptedException", e);
		} catch (IOException e)
		{
			logger.error("IOException", e);
		} catch (Exception e)
		{
			logger.error("Exception", e);
			System.exit(1);
		}

		// If the job is executed without the queue
		if (queued == false)
			return jobState;

		// Check if job is retryable
		if (jobState == false && job.isRetryable() == false)
		{
			jobState = true;
			logger.debug("ZooKeeper job failed and is not retryable: " + job.getClass().getName());
		}

		// Return the jobState
		return jobState;
	}
}
