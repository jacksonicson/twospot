package org.prot.storage.connection;

import org.apache.hadoop.hbase.HBaseConfiguration;

public class ConnectionFactory
{
	private HBaseConnectionPool connectionPool;

	private HBaseConfiguration hbaseConfig;

	public ConnectionFactory()
	{
		// Crate a new connection pool
		connectionPool = new HBaseConnectionPool();

		// Hbase configuration
		hbaseConfig = new HBaseConfiguration();
	}

	public HBaseConfiguration getHBaseConfiguration()
	{
		return this.hbaseConfig;
	}

	public HBaseManagedConnection createManagedConnection()
	{
		// Get a connection from the connection pool (if there is one)
		HBaseManagedConnection managedConnection = connectionPool.getPooledConnection();
		if (managedConnection == null)
		{
			// There is no connection in the pool, so create a new one
			managedConnection = new HBaseManagedConnection(hbaseConfig);
			managedConnection.setIdleTimeoutMills(15 * 1000);

			// Register the new connection with the pool
			connectionPool.registerConnection(managedConnection);
		}

		// Increment the reference counter for the connection
		managedConnection.incrementReferenceCount();
		return managedConnection;
	}
}
