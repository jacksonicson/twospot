package org.prot.util.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.prot.util.zookeeper.jobs.CreateZNodeStructure;

/**
 * 
 * @author Andreas Wolke
 * 
 */
public class JobQueue {
	private static final Logger logger = Logger.getLogger(JobQueue.class);

	private final ZooHelper zooHelper;

	private boolean setup = true;

	private Boolean running = false;

	private List<Job> connectionJobs = new ArrayList<Job>();

	private List<Job> jobQueue = new ArrayList<Job>();

	JobQueue(ZooHelper zooHelper) {
		this.zooHelper = zooHelper;
		jobQueue.add(new CreateZNodeStructure());
	}

	void proceed() {
		run();
	}

	void finishSetup() {
		this.setup = false;
	}

	void reconnected() {
		synchronized (jobQueue) {
			// Add all connections jobs at the beginning of the job queue
			for (int i = 0; i < connectionJobs.size(); i++)
				jobQueue.add(i, connectionJobs.get(i));

			jobQueue.notify();
		}
	}

	public void insertConnectionJob(Job job) {
		assert (setup == false);
		connectionJobs.add(job);
	}

	public boolean insertAndWait(Job job) {
		// Jobs must not be retryable
		assert (job.isRetryable() == false);

		// Execute a job without a queue
		try {
			return execute(job, false) == JobState.OK;
		} catch (KeeperException e) {
			return false;
		}
	}

	public void insert(Job job) {
		synchronized (jobQueue) {
			jobQueue.add(job);
			jobQueue.notify();
		}

		proceed();
	}

	public void requires(Job target, Job toInsert) {
		synchronized (jobQueue) {
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

		proceed();
	}

	public void run() {
		synchronized (running) {
			while (!jobQueue.isEmpty()) {
				Job todo = null;
				synchronized (jobQueue) {
					if (jobQueue.isEmpty())
						return;

					todo = jobQueue.get(0);
				}

				JobState jobState = JobState.FAILED;
				try {
					jobState = execute(todo, true);
				} catch (KeeperException e) {
					switch (e.code()) {
					case SESSIONEXPIRED:
					case SESSIONMOVED:
						logger.warn("Stopping job queue and waiting for reconnect");
						return;
					default:
						jobState = JobState.RETRY_LATER;
					}
				}

				synchronized (jobQueue) {
					jobQueue.remove(todo);
					if (jobState == JobState.RETRY)
						jobQueue.add(0, todo);
					else if (jobState == JobState.RETRY_LATER)
						jobQueue.add(0, todo);
				}
			}
		}
	}

	private JobState execute(Job job, boolean queued) throws KeeperException {
		logger.info("processing job: " + job.getClass().getName());

		// Setup the job
		job.init(zooHelper);

		// Retry this job?
		JobState jobState = null;
		try {
			// Execute the job
			jobState = job.execute(zooHelper);
		} catch (KeeperException e) {
			throw e;
		} catch (InterruptedException e) {
			logger.error("InterruptedException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} catch (Exception e) {
			logger.error("Exception", e);
			System.exit(1);
		}

		// If the job is executed without the queue
		if (queued == false)
			return jobState;

		// Check if job is retryable
		if (jobState == JobState.RETRY && job.isRetryable() == false) {
			jobState = JobState.FAILED;
			logger.debug("ZooKeeper job failed and is not retryable: " + job.getClass().getName());
		}

		// Return the jobState
		return jobState;
	}
}
