package org.prot.storage;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.log4j.Logger;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.SchemaCreator;
import org.prot.storage.query.QueryEngine;
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
	public List<Key> createKey(String appId, long amount)
	{
		logger.debug("Creating keys " + amount);

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
	public void createObject(String appId, String kind, Key key, byte[] obj)
	{
		assert (key != null);

		logger.debug("Creating object of kind: " + kind);
		ObjectCreator creator = new ObjectCreator(connectionFactory);
		try
		{
			creator.createObject(appId, kind, key, obj);
		} catch (IOException e)
		{
			logger.error("", e);
		}
	}

	@Override
	public void updateObject(String appId, String kind, Key key, byte[] obj)
	{
		ObjectUpdater updater = new ObjectUpdater(connectionFactory);
		try
		{
			updater.updateObject(appId, kind, key, obj);
		} catch (IOException e)
		{
			logger.error(e);
		} catch (ClassNotFoundException e)
		{
			logger.error(e);
		}
	}

	@Override
	public List<byte[]> query(StorageQuery query)
	{
		QueryEngine queryEngine = new QueryEngine(connectionFactory);
		return queryEngine.run(query);
	}

	@Override
	public byte[] query(String appId, String kind, Key key)
	{
		QueryEngine queryEngine = new QueryEngine(connectionFactory);
		return queryEngine.fetch(appId, kind, key);
	}

	@Override
	public boolean deleteObject(String appId, String kind, Key key)
	{
		try
		{
			ObjectRemover remover = new ObjectRemover(connectionFactory);
			remover.removeObject(appId, kind, key);
			return true;
		} catch (IOException e)
		{
			logger.error(e);
			return false;
		}
	}
}
