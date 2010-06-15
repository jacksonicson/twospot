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

import java.util.Map;

import org.apache.log4j.Logger;
import org.datanucleus.OMFContext;
import org.datanucleus.ObjectManager;
import org.datanucleus.store.connection.AbstractConnectionFactory;
import org.datanucleus.store.connection.ManagedConnection;

public class ConnectionFactoryImpl extends AbstractConnectionFactory
{
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ConnectionFactoryImpl.class);

	// There is only one managed connection
	private final StorageManagedConnection connection;

	public ConnectionFactoryImpl(OMFContext omfContext, String resourceType)
	{
		super(omfContext, resourceType);

		// Create a new managed connection
		connection = new StorageManagedConnection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ManagedConnection createManagedConnection(ObjectManager om, Map options)
	{
		// Return the reference to the storage service
		return this.connection;
	}
}