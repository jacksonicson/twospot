/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.controller.app;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.util.thread.ThreadPool;

class ProcessWorker implements Runnable {
	private static final Logger logger = Logger.getLogger(ProcessWorker.class);

	// Thread pool
	private ThreadPool threadPool;

	// State of the monitor thread
	boolean running = false;

	// Reference to the process handler (does the dirty work)
	private ProcessHandler processHandler;

	// Worker queue
	private LinkedList<ProcessJob> jobQueue = new LinkedList<ProcessJob>();

	class ProcessJob {
		static final int START = 0;
		static final int STOP = 1;

		private final int type;
		private final AppInfo appInfo;

		ProcessJob(AppInfo appInfo, int type) {
			this.appInfo = appInfo;
			this.type = type;
		}

		AppInfo getAppInfo() {
			return appInfo;
		}

		int getType() {
			return type;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this)
				return true;

			if (!(o instanceof ProcessJob))
				return false;

			ProcessJob test = (ProcessJob) o;
			return test.getAppInfo().equals(this.getAppInfo());
		}
	}

	public void init() {
		if (!running)
			running = threadPool.dispatch(this);

		logger.debug("ProcessWorker thread running: " + running);
	}

	void scheduleKillProcess(Collection<AppInfo> deadApps) {
		logger.debug("Scheduling a STOP-Job");

		synchronized (jobQueue) {
			for (AppInfo info : deadApps) {
				jobQueue.addLast(new ProcessJob(info, ProcessJob.STOP));
			}
			jobQueue.notify();
		}
	}

	void scheduleStartProcess(AppInfo info) {
		logger.debug("Scheduling a START-Job");

		init();

		synchronized (jobQueue) {
			jobQueue.addLast(new ProcessJob(info, ProcessJob.START));
			jobQueue.notify();
		}
	}

	boolean waitForApplication(AppInfo appInfo) {
		// Get a continuation for connection of the current thread
		HttpConnection con = HttpConnection.getCurrentConnection();
		Continuation continuation = ContinuationSupport.getContinuation(con.getRequest());

		// TODO: Configuration
		continuation.setTimeout(60000);

		// Register the continuation and suspend it
		if (appInfo.addContinuation(continuation)) {
			continuation.suspend();
			return true;
		}

		// Could not use a continuation
		return false;
	}

	public void run() {
		logger.debug("Starting AppMonitor worker thread...");
		while (true) {
			// Next job
			ProcessJob job;

			// Get the next process from the worker queue
			synchronized (jobQueue) {
				// Spin lock until the jobQueue is not empty
				while (jobQueue.isEmpty()) {
					try {
						logger.debug("Waiting for process jobs...");
						jobQueue.wait();
					} catch (InterruptedException e) {
						logger.trace(e);
					}
				}

				// Get and remove the next job from the queue
				job = jobQueue.getFirst();
				jobQueue.removeFirst();
			}

			switch (job.getType()) {
			case ProcessJob.START:
				startProcess(job.getAppInfo());
				break;
			case ProcessJob.STOP:
				stopProcess(job.getAppInfo());
				break;
			}
		}
	}

	private void stopProcess(AppInfo appInfo) {
		// Stop the AppServer
		logger.debug("Stopping AppServer...");
		processHandler.stop(appInfo, appInfo.getAppProcess());

		// Finish all continuations waiting for the killed AppServer
		appInfo.finishContinuations();

		// Update state
		appInfo.setState(AppState.DEAD);
	}

	private void startProcess(AppInfo appInfo) {
		// Start the AppServer
		logger.debug("Starting AppServer...");
		boolean success = processHandler.execute(appInfo, appInfo.getAppProcess());

		// Update state
		if (success) {
			appInfo.setState(AppState.ONLINE);

			// Resume all continuations
			logger.debug("Resuming all continuations waiting in the AppInfo");
			appInfo.resumeContinuations();
		} else {
			appInfo.setState(AppState.KILLED);

			// Complete all continuations
			logger.debug("Could not start AppServer - finishing all continuations");
			appInfo.completeContinuations();
		}
	}

	public void setThreadPool(ThreadPool threadPool) {
		this.threadPool = threadPool;
	}

	public void setProcessHandler(ProcessHandler processHandler) {
		this.processHandler = processHandler;
	}
}
