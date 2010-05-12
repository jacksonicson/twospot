package org.prot.util.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.prot.util.zookeeper.jobs.CreateZNodeStructure;

/**
 * Manages a queue of jobs
 * 
 * @author Andreas Wolke
 * 
 */
public class JobQueue {
	private static final Logger logger = Logger.getLogger(JobQueue.class);

	private final ZooHelper zooHelper;

	// Is this queue in the setup mode
	private boolean setup = true;

	// Used for thread synchronization
	private Boolean running = false;

	// Jobs which must be executed after a connection to the ZooKeeper server
	// has been established
	private List<Job> connectionJobs = new ArrayList<Job>();

	// Regular job queue
	private List<Job> jobQueue = new ArrayList<Job>();

	JobQueue(ZooHelper zooHelper) {
		this.zooHelper = zooHelper;

		// Register some predefined jobs
		jobQueue.add(new CreateZNodeStructure());
	}

	/**
	 * Process all registered jobs
	 */
	void proceed() {
		run();
	}

	/**
	 * Exits the setup mode
	 */
	void finishSetup() {
		this.setup = false;
	}

	/**
	 * Schedules all registered connectionJobs with the highest priority
	 */
	void reconnected() {
		synchronized (jobQueue) {
			// Add all connections jobs at the beginning of the job queue
			for (int i = 0; i < connectionJobs.size(); i++)
				jobQueue.add(i, connectionJobs.get(i));

			jobQueue.notify();
		}
	}

	/**
	 * Register a new connectionJob
	 * 
	 * @param Job
	 */
	public void insertConnectionJob(Job job) {
		assert (setup == false);
		connectionJobs.add(job);
	}

	/**
	 * Insert a new job into the job queue and executes it. The method blocks
	 * until the job has been executed.
	 * 
	 * @param job
	 * @return Returns true if the job ends with the state OK otherwise false.
	 */
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

	/**
	 * Insert a new job at the and of the current job queue. Execute the whole
	 * queue until all jobs have been processed. (Calls the
	 * <code>proceed()</code> Method)
	 * 
	 * @param job
	 */
	public void insert(Job job) {
		synchronized (jobQueue) {
			jobQueue.add(job);
			jobQueue.notify();
		}

		proceed();
	}

	/**
	 * Insert a new job after another job (dependency). Execute the whole queue
	 * until all jobs have been processed. (Calls the <code>proceed()</code>
	 * Method)
	 * 
	 * @param required
	 * @param job
	 */
	public void insert(Job required, Job job) {
		synchronized (jobQueue) {
			// Check index of the target job
			int index = jobQueue.indexOf(required);

			// Decrement the index
			index--;

			// Index must not be negative
			if (index < 0)
				index = 0;

			// Insert the required job before the target job
			jobQueue.add(index, job);
			jobQueue.notify();
		}

		proceed();
	}

	/**
	 * Executes the queue
	 */
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

	/**
	 * Execute a job
	 * 
	 * @param job
	 * @param queued
	 * @return
	 * @throws KeeperException
	 */
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
