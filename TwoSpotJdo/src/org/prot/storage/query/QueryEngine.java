package org.prot.storage.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.Key;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.query.error.QueryException;

public class QueryEngine
{
	private static final Logger logger = Logger.getLogger(QueryEngine.class);

	private HBaseManagedConnection connection;

	public QueryEngine(ConnectionFactory connectionFactory)
	{
		this.connection = connectionFactory.createManagedConnection();
	}

	public byte[] fetch(String appId, String kind, Key key)
	{
		logger.debug("Querying for a single object");
		StorageQuery query = new StorageQuery(appId, kind);
		List<byte[]> result = run(query);
		if (result.size() == 1)
		{
			return result.get(0);
		} else if (result.size() > 1)
		{
			logger.fatal("Multiple objects for one key: " + key);
			throw new QueryException("Multiple objects for one key");
		}

		// Nothing found
		return null;
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
