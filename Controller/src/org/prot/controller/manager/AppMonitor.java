package org.prot.controller.manager;

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
	private Queue<AppProcess> startQueue = new LinkedBlockingQueue<AppProcess>();

	public AppMonitor(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}

	public void startProcess(AppInfo info)
	{
		synchronized (startQueue)
		{
			logger.info("Starting new AppServer: " + info.getAppId());
			
			AppProcess process = this.processList.get(info);
			if (process == null)
			{
				process = new AppProcess(info);
				this.processList.put(info, process);
			}

			startQueue.add(process);
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
			System.out.println("SUSPENDING !!!!");
			appInfo.conts.add(continuation);
			continuation.suspend();
			System.out.println("AFTER SUSPEND");
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
			synchronized (startQueue)
			{
				if (!startQueue.isEmpty())
					toStart = startQueue.poll();
			}

			logger.info("check"); 
			if (toStart != null)
			{
				logger.info("Thread is starting a new AppServer"); 
				
				toStart.startOrRestart();

				for (Continuation cont : toStart.getAppInfo().conts)
				{
					synchronized (toStart.getAppInfo())
					{
						cont.resume();
					}
				}
			}

			// Fetching stdout from every process
			for (AppProcess proc : processList.values())
			{
				try
				{
					proc.fetchStreams();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			logger.info("after fetch"); 

			// Sleep
			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
