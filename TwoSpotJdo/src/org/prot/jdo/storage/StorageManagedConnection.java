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
package org.prot.jdo.storage;

import javax.transaction.xa.XAResource;

import org.apache.log4j.Logger;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.store.connection.AbstractManagedConnection;
import org.prot.storage.Storage;
import org.prot.storage.StorageDev;
import org.prot.storage.StorageImpl;

/**
 * Implementation of a ManagedConnection.
 */
public class StorageManagedConnection extends AbstractManagedConnection
{
	private static final Logger logger = Logger.getLogger(StorageManagedConnection.class);

	// Reference to the storage implementation
	private Storage storage;

	// Counts the number of references to this connection
	private int referenceCount = 0;

	public StorageManagedConnection()
	{
		super();
	}

	public Object getConnection()
	{
		// Unsupported - use getStorage instead
		throw new NucleusDataStoreException("Unsopported Exception #getConnection() for "
				+ this.getClass().getName());
	}

	public Storage getStorage()
	{
		if (storage == null)
		{
			logger.debug("Creating storage");
			
			// Create the storage
			if (StorageHelper.isDevMode())
			{
				logger.info("Storage is running in dev mode");
				this.storage = new StorageDev();
			} else
			{
				this.storage = new StorageImpl();
			}
		}

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
	}

	public void release()
	{
		referenceCount--;

		if (referenceCount < 0)
			logger.warn("Too many calls to release()");
	}
}