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
package org.prot.storage.connection;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

public class HBaseConnectionPool
{
	private static final Logger logger = Logger.getLogger(HBaseConnectionPool.class);

	// List of all available connections
	private final List<HBaseManagedConnection> connections;

	// A threadlocal variable which contains a weak reference to a connection
	// (garbage collector can remove this)
	private final ThreadLocal<WeakReference<HBaseManagedConnection>> connectionForCurrentThread;

	// Timer which cleans up connections
	private final Timer evictorThread;

	// Time between the evictorThread executions
	private final int timeBetweenEvictionRunsMillis = 15 * 1000; // default, 15

	// secs

	public HBaseConnectionPool()
	{
		connectionForCurrentThread = new ThreadLocal<WeakReference<HBaseManagedConnection>>();
		connections = new CopyOnWriteArrayList<HBaseManagedConnection>();

		// Create and start the evictorThread
		evictorThread = new Timer("HBase Connection Evictor", true);
		startConnectionEvictorThread(evictorThread);
	}

	public void registerConnection(HBaseManagedConnection managedConnection)
	{
		connections.add(managedConnection);
		connectionForCurrentThread.set(new WeakReference<HBaseManagedConnection>(managedConnection));
	}

	public HBaseManagedConnection getPooledConnection()
	{
		// Check fi there is a WeakReference-Object for the curren thread
		WeakReference<HBaseManagedConnection> ref = connectionForCurrentThread.get();

		// No theres is no WeakReference-Object
		if (ref == null)
		{
			return null;
		} else
		{
			// Get the value of the WeakReference (Connection)
			HBaseManagedConnection managedConnection = ref.get();

			// Check if the connection is still valid
			if (managedConnection != null && !managedConnection.isDisposed())
			{
				return managedConnection;
			} else
			{
				// Connection is not valid - it will be evicted soon
				return null;
			}
		}
	}

	private void disposeTimedOutConnections()
	{
		for (Iterator<HBaseManagedConnection> it = connections.iterator(); it.hasNext();)
		{
			HBaseManagedConnection managedConnection = it.next();
			if (managedConnection.isExpired())
			{
				logger.debug("Removing expired connection");
				managedConnection.dispose();
				it.remove();
			}
		}
	}

	private void startConnectionEvictorThread(Timer connectionTimeoutThread)
	{
		TimerTask timeoutTask = new TimerTask()
		{
			public void run()
			{
				disposeTimedOutConnections();
			}
		};

		// Schedule the timer task with the given time
		evictorThread.schedule(timeoutTask, timeBetweenEvictionRunsMillis, timeBetweenEvictionRunsMillis);
	}

}
