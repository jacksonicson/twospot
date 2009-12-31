/**********************************************************************
Copyright (c) 2009 Erik Bengtson and others. All rights reserved.
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

import java.util.Map;

import org.apache.log4j.Logger;
import org.datanucleus.OMFContext;
import org.datanucleus.ObjectManager;
import org.datanucleus.store.connection.AbstractConnectionFactory;
import org.datanucleus.store.connection.ManagedConnection;

public class ConnectionFactoryImpl extends AbstractConnectionFactory
{
	private static final Logger logger = Logger.getLogger(ConnectionFactoryImpl.class);

	private StorageManagedConnection connection;

	public ConnectionFactoryImpl(OMFContext omfContext, String resourceType)
	{
		super(omfContext, resourceType);

		// Create a new managed connection
		this.connection = new StorageManagedConnection();
	}

	@Override
	public ManagedConnection createManagedConnection(ObjectManager om, Map options)
	{
		this.connection.incrementReferenceCount();
		return this.connection;
	}
}