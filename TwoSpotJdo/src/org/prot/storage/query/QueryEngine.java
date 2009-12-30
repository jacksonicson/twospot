package org.prot.storage.query;

import java.io.IOException;
import java.util.List;

import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;

public class QueryEngine
{
	private HBaseManagedConnection connection;

	public QueryEngine(ConnectionFactory connectionFactory)
	{
		this.connection = connectionFactory.createManagedConnection();
	}

	public List<Object> run(StorageQuery query)
	{
		try
		{
			return query.run(connection);
		} catch (IOException e)
		{
			return null;
		} catch (ClassNotFoundException e)
		{
			return null;
		}
	}
}
