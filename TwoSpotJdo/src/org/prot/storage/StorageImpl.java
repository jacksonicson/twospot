package org.prot.storage;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.StorageUtils;
import org.prot.storage.query.QueryEngine;
import org.prot.storage.query.StorageQuery;

public class StorageImpl implements Storage
{
	private static final Logger logger = Logger.getLogger(StorageImpl.class);

	private ConnectionFactory connectionFactory;

	private long timer = 0;

	private final void startTimer()
	{
		timer = System.currentTimeMillis();
	}

	private final void logTime()
	{
		timer = System.currentTimeMillis() - timer;
		logger.debug("Storage API call took (ms): " + timer);
	}

	public StorageImpl()
	{
		this.connectionFactory = new ConnectionFactory();
	}

	@Override
	public List<Key> createKey(String appId, long amount)
	{
		logger.debug("Creating keys " + amount);
		startTimer();

		KeyCreator keyCreator = new KeyCreator(connectionFactory);
		try
		{
			return keyCreator.fetchKey(appId, amount);
		} catch (IOException e)
		{
			logger.error(e);
			return null;
		} finally
		{
			logTime();
		}
	}

	@Override
	public void createObject(String appId, String kind, Key key, byte[] obj)
	{
		startTimer();

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
		} finally
		{
			logTime();
		}
	}

	@Override
	public void updateObject(String appId, String kind, Key key, byte[] obj)
	{
		startTimer();

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
		} finally
		{
			logTime();
		}
	}

	@Override
	public List<byte[]> query(StorageQuery query)
	{
		startTimer();

		ImplQueryHandler handler = new ImplQueryHandler(connectionFactory);
		QueryEngine engine = new QueryEngine(handler);

		try
		{
			return engine.run(query);
		} finally
		{
			logTime();
		}
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
		startTimer();

		try
		{
			ObjectRemover remover = new ObjectRemover(connectionFactory);
			remover.removeObject(appId, kind, key);
			return true;
		} catch (IOException e)
		{
			logger.error(e);
			return false;
		} finally
		{
			logTime();
		}
	}
}
