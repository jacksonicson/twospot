package org.prot.controller.manager;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

class AppMonitor extends Thread
{
	private static final Logger logger = Logger.getLogger(AppMonitor.class);

	// Manages all AppProcess-Objects
	private Map<AppInfo, AppProcess> processList = new HashMap<AppInfo, AppProcess>();

	// Worker queue
	private Queue<AppProcess> startQueue = new LinkedBlockingQueue<AppProcess>();
	
	// Lock is used to communicated between the worker thread and the request threads
	private Object startLock = new Object();

	public AppMonitor()
	{
		this.start();
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

	public void startProcess(AppProcess process)
	{
		// Enqueue the AppProcess for starting
		synchronized (startQueue)
		{
			if (startQueue.contains(process))
				return;

			if (process.getOwner().getStatus() != AppState.STARTING)
			{
				logger.warn("Trying to start a process in the wrong state");
				return;
			}

			startQueue.add(process);
			startQueue.notify();
		}
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
				// Wait until new jobs are in the worker queue
				while (startQueue.isEmpty())
				{
					try
					{
						startQueue.wait();
					} catch (InterruptedException e)
					{
						logger.error("Interruped while waiting for AppServers to start", e); 
					}
				}
				
				// Get the next item in the queue and delete this item
				toStart = startQueue.poll();
			}

			// Start the AppServer (blocks until the AppServer is running)
			toStart.startOrRestart();

			// Inform all Threads which are waiting for an AppServer
			synchronized (startLock)
			{
				startLock.notifyAll();
			}
		}
	}
}
