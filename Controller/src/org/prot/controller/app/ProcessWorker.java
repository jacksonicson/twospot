package org.prot.controller.app;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.util.thread.ThreadPool;

class ProcessWorker implements Runnable
{
	private static final Logger logger = Logger.getLogger(ProcessWorker.class);

	// Thread pool
	private ThreadPool threadPool;

	// State of the monitor thread
	boolean running = false;

	// Reference to the process handler (does the dirty work)
	private final ProcessHandler processHandler = new ProcessHandler();

	// Worker queue
	private Queue<ProcessJob> jobQueue = new LinkedList<ProcessJob>();

	class ProcessJob
	{
		static final int START = 0;
		static final int STOP = 1;

		private final int type;
		private final AppInfo appInfo;

		ProcessJob(AppInfo appInfo, int type)
		{
			this.appInfo = appInfo;
			this.type = type;
		}

		AppInfo getAppInfo()
		{
			return appInfo;
		}

		int getType()
		{
			return type;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
				return true;

			if (!(o instanceof ProcessJob))
				return false;

			ProcessJob test = (ProcessJob) o;
			return test.getAppInfo().equals(this.getAppInfo());
		}
	}

	public void init()
	{
		if (!running)
			running = threadPool.dispatch(this);

		logger.debug("ProcessWorker thread running: " + running);
	}

	void scheduleKillProcess(Set<AppInfo> deadApps)
	{
		logger.debug("Scheduling a STOP-Job");

		synchronized (jobQueue)
		{
			for (AppInfo info : deadApps)
			{
				jobQueue.add(new ProcessJob(info, ProcessJob.STOP));
			}
			jobQueue.notifyAll();
		}
	}

	void scheduleStartProcess(AppInfo info)
	{
		init();
		
		synchronized (jobQueue)
		{
			jobQueue.add(new ProcessJob(info, ProcessJob.START));
			jobQueue.notifyAll();
		}
	}

	boolean waitForApplication(AppInfo appInfo)
	{
		// Get the continuation
		HttpConnection con = HttpConnection.getCurrentConnection();
		Continuation continuation = ContinuationSupport.getContinuation(con.getRequest());

		// Register the continuation
		if (appInfo.addContinuation(continuation))
			continuation.suspend();

		// Continuation used
		return true;
	}

	public void run()
	{
		logger.debug("Starting AppMonitor worker thread...");
		while (true)
		{
			// Next job
			ProcessJob job;

			// Get the next process from the worker queue
			synchronized (jobQueue)
			{
				// Spin lock until the jobQueue is not empty
				while (jobQueue.isEmpty())
				{
					try
					{
						logger.debug("Waiting for process jobs...");
						jobQueue.wait();
					} catch (InterruptedException e)
					{
						logger.trace(e);
					}
				}

				// Get and remove the next job from the queue
				job = jobQueue.poll();
			}

			switch (job.getType())
			{
			case ProcessJob.START:
				startProcess(job.getAppInfo());
				break;
			case ProcessJob.STOP:
				stopProcess(job.getAppInfo());
				break;
			}
		}
	}

	private void stopProcess(AppInfo appInfo)
	{
		// Stop the AppServer
		logger.debug("Stopping AppServer...");
		processHandler.stop(appInfo.getAppProcess());

		// Update state
		appInfo.setStatus(AppState.DEAD);
	}

	private void startProcess(AppInfo appInfo)
	{
		// Start the AppServer
		logger.debug("Starting AppServer...");
		boolean success = processHandler.execute(appInfo, appInfo.getAppProcess());

		// Update state
		if (success)
			appInfo.setStatus(AppState.ONLINE);
		else
			appInfo.setStatus(AppState.KILLED);

		// Resume all continuations
		logger.debug("Resuming all continuations");
		appInfo.resumeContinuations();
	}

	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}
}
