/**********************************************************************
Copyright (c) 2009 Tatsuya Kawano and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
    ...
 ***********************************************************************/
package org.prot.stor.hbase;

import javax.transaction.xa.XAResource;

import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.connection.AbstractManagedConnection;
import org.prot.storage.Storage;

/**
 * Implementation of a ManagedConnection.
 */
public class StorageManagedConnection extends AbstractManagedConnection
{
	// Reference to the storage implementation
	private Storage storage;

	// Counts the number of references to this connection
	private int referenceCount = 0;

	// How long until this connection is idle (expirationTime = system time +
	// idle time
	private int idleTimeoutMills = 30 * 1000; // 30 secs

	// Timestamp when this connection expires (-1 for no expiry)
	private long expirationTime;

	// Is this connection disposed
	private boolean isDisposed = false;

	public StorageManagedConnection()
	{
		disableExpirationTime();
	}

	public Object getConnection()
	{
		// Unsupported - use getStorage instead
		throw new NucleusDataStoreException("Unsopported Exception #getConnection() for "
				+ this.getClass().getName());
	}

	public Storage getStorage()
	{
		return this.storage;
	}

	public XAResource getXAResource()
	{
		// Do nothing
		return null;
	}

	public void close()
	{
		// Do nothing
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
	}

	public boolean isDisposed()
	{
		return isDisposed;
	}
}