package org.prot.controller.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.thread.ThreadPool;

class AppMonitor implements Runnable
{
	private static final Logger logger = Logger.getLogger(AppMonitor.class);

	// Thread pool
	private ThreadPool threadPool;

	// Manages all AppProcess-Objects
	private Map<AppInfo, AppProcess> processList = new HashMap<AppInfo, AppProcess>();

	// Worker queue
	private Queue<AppProcess> startQueue = new LinkedBlockingQueue<AppProcess>();

	// Lock is used to communicated between the worker thread and the
	// request-threads
	private Object startLock = new Object();

	public AppMonitor(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}

	private void registerProcess(AppProcess process)
	{
		this.processList.put(process.getOwner(), process);
	}

	public synchronized AppProcess getProcess(AppInfo appInfo)
	{
		AppProcess process = this.processList.get(appInfo);

		if (process == null)
		{
			process = new AppProcess(appInfo);
			registerProcess(process);
		}

		return process;
	}

	boolean running = false; 
	
	public void startProcess(AppInfo info)
	{
		AppProcess process = null;
		if (this.processList.containsKey(info) == false)
		{
			process = new AppProcess(info);
			this.processList.put(info, process);
		} else
		{
			process = this.processList.get(info);
		}

		startQueue.add(process);
		
		if(!running)
			running = threadPool.dispatch(this); 
	}

	public void waitForApplication(AppInfo appInfo)
	{
		logger.info("Waiting for AppServer: " + appInfo.getAppId());
		synchronized (startLock)
		{
			// Wait until the application state is not STARTING
			while (appInfo.getStatus() == AppState.STARTING)
			{
				try
				{
					// Wait until notified
					startLock.wait();

				} catch (InterruptedException e)
				{
					logger.error("Interrupted while waiting for AppServer", e);
				}

				logger.info("Notified that AppServer is online");
			}
		}
	}

	private void shutdownIdleProcesses()
	{
		long currentTime = System.currentTimeMillis();
		long maxTime = 60 * 1000;
		for (AppProcess process : processList.values())
		{
			long lastInteraction = process.getOwner().getLastInteraction();
			long difference = currentTime - lastInteraction;
			if (difference > maxTime)
				process.kill();
		}
	}

	public void run()
	{
		// Run forever (Controller doesn't have a shutdown sequence)
		while (true)
		{
			// Shutdown all idle AppServers
			// shutdownIdleProcesses();

			// References the process to be started
			AppProcess toStart = null;

			// Get the next process from the worker queue
			synchronized (startQueue)
			{
				if (startQueue.isEmpty() == false)
				{
					// Get the next item in the queue and delete this item
					toStart = startQueue.poll();
				}
			}
			logger.info("polling"); 
			if (toStart != null)
			{
				logger.info("starting"); 
				toStart.startOrRestart();

				synchronized (startLock)
				{
					startLock.notifyAll();
				}
			}
			
			logger.info("done"); 

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
			
			logger.info("yield"); 

			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			Thread.yield();
		}
	}
}
