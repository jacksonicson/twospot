package org.prot.controller.manager;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.util.thread.ThreadPool;

class AppMonitor implements Runnable
{
	private static final Logger logger = Logger.getLogger(AppMonitor.class);

	// Thread pool
	private ThreadPool threadPool;

	// State of the monitor thread
	boolean running = false;

	// Manages all AppProcess-Objects
	private Map<AppInfo, AppProcess> processList = new ConcurrentHashMap<AppInfo, AppProcess>();

	// Worker queue
	private Queue<AppProcess> jobQueue = new LinkedBlockingQueue<AppProcess>();

	public AppMonitor(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}

	private synchronized void startWorkerThread()
	{
		// Start the thread only if it is not running right now
		if (!running)
			running = threadPool.dispatch(this);
	}

	void scheduleKillProcess(Set<AppInfo> deadApps)
	{
		startWorkerThread();

		synchronized (jobQueue)
		{
			synchronized (processList)
			{
				for (AppInfo info : deadApps)
				{
					logger.debug("Stopping AppId: " + info.getAppId());

					AppProcess process = this.processList.get(info);
					if (process == null)
						continue;

					this.processList.remove(info);
					jobQueue.add(process);
				}

			}

			jobQueue.notifyAll();
		}
	}

	void scheduleStartProcess(AppInfo info)
	{
		startWorkerThread();

		synchronized (jobQueue)
		{
			logger.debug("Starting AppId: " + info.getAppId());

			synchronized (processList)
			{
				AppProcess process = this.processList.get(info);
				if (process == null)
				{
					process = new AppProcess(info);
					this.processList.put(info, process);
				}

				jobQueue.add(process);
			}

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
		while (running)
		{
			// References the process to be started
			AppProcess toProcess = null;

			// Get the next process from the worker queue
			synchronized (jobQueue)
			{
				// Spin lock until the jobQueue is not empty
				while (jobQueue.isEmpty())
				{
					try
					{
						jobQueue.wait();
					} catch (InterruptedException e)
					{
						logger.error(e);
					}
				}

				// Get and remove the next job from the queue
				toProcess = jobQueue.poll();
			}

			// Start
			toProcess.execute();

			// Resume all Continuations
			toProcess.getAppInfo().resume();
		}
	}
}
