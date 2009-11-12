package org.prot.controller.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
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

	public void startProcess(AppInfo info)
	{
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

		synchronized (this)
		{
			if (!running)
				running = threadPool.dispatch(this);
		}
	}

	public boolean waitForApplication(AppInfo appInfo)
	{
		synchronized (appInfo)
		{
			if (appInfo.getStatus() == AppState.ONLINE)
				return false;

			HttpConnection con = HttpConnection.getCurrentConnection();
			Continuation continuation = ContinuationSupport.getContinuation(con.getRequest());

			logger.debug("Suspending the request");
			appInfo.addContinuation(continuation);
			continuation.suspend();

			return true;
		}
	}

	public void run()
	{
		// Run forever (Controller doesn't have a shutdown sequence)
		while (true)
		{
			// References the process to be started
			AppProcess toStart = null;

			// Get the next process from the worker queue
			synchronized (jobQueue)
			{
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
				
				toStart = jobQueue.poll();
			}

			if (toStart != null)
			{
				logger.debug("Starting new AppServer");

				// Start
				try
				{
					toStart.startOrRestart();
				} catch (IOException e)
				{
					toStart.getAppInfo().setStatus(AppState.FAILED);
				}

				// Resume all Continuations 
				toStart.getAppInfo().resume(); 
			}
		}
	}
}
