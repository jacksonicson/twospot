package org.prot.storage;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.log4j.Logger;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.SchemaCreator;
import org.prot.storage.connection.StorageUtils;
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
		// Asserts
		assert (key != null);

		// Assert field size
		StorageUtils.assertFieldSize(obj.length);

		// Create the entity
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
		// Asserts
		assert (key != null);

		// Assert field size
		StorageUtils.assertFieldSize(obj.length);

		// Update the entity;
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
		ImplQueryHandler handler = new ImplQueryHandler(connectionFactory);
		QueryEngine engine = new QueryEngine(handler);
		return engine.run(query);
	}

	@Deprecated
	public byte[] query(String appId, String kind, Key key)
	{
		ImplQueryHandler handler = new ImplQueryHandler(connectionFactory);
		QueryEngine engine = new QueryEngine(handler);
		return engine.fetch(appId, kind, key);
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
