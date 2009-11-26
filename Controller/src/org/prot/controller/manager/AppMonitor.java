package org.prot.controller.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
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
	private Map<AppInfo, AppProcess> processList = new HashMap<AppInfo, AppProcess>();

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

	public void killProcess(Set<AppInfo> idleApps)
	{
		synchronized (jobQueue)
		{
			for (AppInfo info : idleApps)
			{
				logger.debug("Kill AppServer-Job: " + info.getAppId());

				AppProcess process = this.processList.get(info);
				if (process != null)
				{
					this.processList.remove(info);
					jobQueue.add(process);
				}
			}
			
			jobQueue.notifyAll();
		}
	}

	public void startProcess(AppInfo info)
	{
		startWorkerThread();

		synchronized (jobQueue)
		{
			logger.debug("New AppServer-Job: " + info.getAppId());

			AppProcess process = this.processList.get(info);
			if (process == null)
			{
				process = new AppProcess(info);
				this.processList.put(info, process);
			}

			jobQueue.add(process);
			jobQueue.notifyAll();
		}
	}

	public boolean waitForApplication(AppInfo appInfo)
	{
		synchronized (appInfo)
		{
			// If the AppServer is online - don't use a Continuation
			if (appInfo.getStatus() == AppState.ONLINE)
				return false;

			// Get the continuation
			HttpConnection con = HttpConnection.getCurrentConnection();
			Continuation continuation = ContinuationSupport.getContinuation(con.getRequest());

			// Register the continuation
			logger.debug("suspending the request for: " + appInfo.getAppId());
			appInfo.addContinuation(continuation);
			continuation.suspend();

			// Continuation used
			return true;
		}
	}

	public void run()
	{
		// Run forever (Controller doesn't have a shutdown sequence)
		while (true)
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
						logger.error("Interruption in AppStarter-Thread", e);
					}
				}

				// Get and remove the next job from the queue
				toProcess = jobQueue.poll();
			}

			logger.debug("monitor processes job");

			// Start
			try
			{
				// Start the process
				toProcess.startOrRestart();
			} catch (IOException e)
			{
				// Update the AppServer state
				toProcess.getAppInfo().setStatus(AppState.FAILED);
			}

			// Resume all Continuations
			toProcess.getAppInfo().resume();
		}
	}
}
