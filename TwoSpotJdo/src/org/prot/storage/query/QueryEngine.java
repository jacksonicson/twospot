package org.prot.storage.query;

import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;

public class QueryEngine
{
	private HBaseManagedConnection connection; 
	
	public QueryEngine(ConnectionFactory connectionFactory)
	{
		this.connection = connectionFactory.createManagedConnection();
	}
	
	public void run(StorageQuery query)
	{
		
	}
}
