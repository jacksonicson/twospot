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

	public StorageManagedConnection()
	{
		super();
	}

	@Override
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

			// Create the instance of the storage
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
}