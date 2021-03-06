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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.xa.XAResource;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.connection.AbstractManagedConnection;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;

/**
 * Implementation of a ManagedConnection.
 */
public class HBaseManagedConnection extends AbstractManagedConnection
{
	// HBase-Configuration (Required to use the HBase libs)
	private final HBaseConfiguration config;

	// Maps table names to HTable objects
	private final Map<String, HTable> tables;

	// Counts the number of references to this connection
	private int referenceCount = 0;

	// How long until this connection is idle (expirationTime = system time +
	// idle time
	private int idleTimeoutMills = 30 * 1000; // 30 secs

	// Timestamp when this connection expires (-1 for no expiry)
	private long expirationTime;

	// Is this connection disposed
	private boolean isDisposed = false;

	public HBaseManagedConnection(HBaseConfiguration config)
	{
		this.config = config;
		this.tables = new HashMap<String, HTable>();

		disableExpirationTime();
	}

	public Object getConnection()
	{
		throw new NucleusDataStoreException("Unsopported Exception #getConnection() for "
				+ this.getClass().getName());
	}

	public HTable getHTable(String tableName)
	{
		HTable table = tables.get(tableName);

		if (table == null)
		{
			try
			{
				table = new HTable(config, tableName);
				tables.put(tableName, table);
			} catch (IOException e)
			{
				throw new NucleusDataStoreException(e.getMessage(), e);
			}
		}

		return table;
	}

	public XAResource getXAResource()
	{
		return null;
	}

	public void close()
	{
		if (tables.size() == 0)
			return;

		for (ManagedConnectionResourceListener listener : listeners)
			listener.managedConnectionPreClose();

		try
		{
			closeTables(tables);
		} finally
		{
			for (ManagedConnectionResourceListener listener : listeners)
				listener.managedConnectionPostClose();
		}
	}

	void incrementReferenceCount()
	{
		referenceCount++;
		disableExpirationTime();
	}

	public void release()
	{
		referenceCount--;

		if (referenceCount == 0)
		{
			close();
			enableExpirationTime();
		} else if (referenceCount < 0)
		{
			throw new NucleusDataStoreException("Too many calls on release(): " + this);
		}
	}

	private void enableExpirationTime()
	{
		this.expirationTime = System.currentTimeMillis() + idleTimeoutMills;
	}

	private void disableExpirationTime()
	{
		this.expirationTime = -1;
	}

	public void setIdleTimeoutMills(int mills)
	{
		this.idleTimeoutMills = mills;
	}

	public boolean isExpired()
	{
		return expirationTime > 0 && expirationTime > System.currentTimeMillis();
	}

	public void dispose()
	{
		isDisposed = true;
		closeTables(tables);
	}

	public boolean isDisposed()
	{
		return isDisposed;
	}

	private void closeTables(Map<String, HTable> tables)
	{
		for (String tableName : tables.keySet())
		{
			try
			{
				HTable table = tables.get(tableName);
				table.close();
			} catch (IOException e)
			{
				throw new NucleusDataStoreException(e.getMessage(), e);
			}
		}
	}

}