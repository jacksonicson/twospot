package org.prot.storage.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;

public class QueryEngine
{
	private static final Logger logger = Logger.getLogger(QueryEngine.class);

	private HBaseManagedConnection connection;

	public QueryEngine(ConnectionFactory connectionFactory)
	{
		this.connection = connectionFactory.createManagedConnection();
	}

	public List<byte[]> run(StorageQuery query)
	{
		try
		{
			return query.run(connection);
		} catch (IOException e)
		{
			logger.error("", e);
		} catch (ClassNotFoundException e)
		{
			logger.error("", e);
		}

		return new ArrayList<byte[]>();
	}
}
