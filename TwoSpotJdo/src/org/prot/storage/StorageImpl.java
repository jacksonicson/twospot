package org.prot.storage;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.log4j.Logger;
import org.prot.stor.hbase.Key;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.SchemaCreator;
import org.prot.storage.query.StorageQuery;

public class StorageImpl implements Storage
{
	private static final Logger logger = Logger.getLogger(StorageImpl.class);

	private ConnectionFactory connectionFactory;

	public StorageImpl()
	{
		this.connectionFactory = new ConnectionFactory();

		SchemaCreator schemaCreator;
		try
		{
			schemaCreator = new SchemaCreator(this.connectionFactory);
			schemaCreator.checkAndCreate();
		} catch (MasterNotRunningException e)
		{
			logger.error("HBase master ist not running", e);
		} catch (IOException e)
		{
			logger.error("", e);
		}
	}

	@Override
	public List<Key> createKey(String appId, String kind, int amount)
	{
		KeyCreator keyCreator = new KeyCreator(connectionFactory);
		try
		{
			return keyCreator.fetchKey(appId, amount);
		} catch (IOException e)
		{
			logger.error(e);
			return null;
		}
	}

	@Override
	public void createObject(String appId, String kind, Key key, Object obj)
	{
		assert (key != null);
	}

	@Override
	public void deleteObject(Key key)
	{

	}

	@Override
	public void query(StorageQuery query)
	{

	}

	@Override
	public void updateObject(Key key, Object obj)
	{

	}
}
