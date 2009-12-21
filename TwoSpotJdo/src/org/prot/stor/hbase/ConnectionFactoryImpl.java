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
package org.prot.stor.hbase;

import java.util.Map;

import org.datanucleus.OMFContext;
import org.datanucleus.ObjectManager;
import org.datanucleus.store.connection.AbstractConnectionFactory;
import org.datanucleus.store.connection.ManagedConnection;

public class ConnectionFactoryImpl extends AbstractConnectionFactory
{
	private HBaseConnectionPool connectionPool;

	public ConnectionFactoryImpl(OMFContext omfContext, String resourceType)
	{
		super(omfContext, resourceType);

		// Get the storemanager
		HBaseStoreManager storeManager = (HBaseStoreManager) omfContext.getStoreManager();

		// Crate a new connection pool
		connectionPool = new HBaseConnectionPool();
	}

	public ManagedConnection createManagedConnection(ObjectManager om, Map transactionOptions)
	{
		// Get the storemanager
		HBaseStoreManager storeManager = (HBaseStoreManager) om.getStoreManager();

		// Get a connection from the connection pool (if there is one)
		HBaseManagedConnection managedConnection = connectionPool.getPooledConnection();
		if (managedConnection == null)
		{
			// There is no connection in the pool, so create a new one
			managedConnection = new HBaseManagedConnection(storeManager.getHbaseConfig());
			managedConnection.setIdleTimeoutMills(storeManager.getPoolMinEvictableIdleTimeMillis());

			// Register the new connection with the pool
			connectionPool.registerConnection(managedConnection);
		}

		// Increment the reference counter for the connection
		managedConnection.incrementReferenceCount();
		return managedConnection;
	}
}