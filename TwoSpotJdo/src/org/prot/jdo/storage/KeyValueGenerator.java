package org.prot.jdo.storage;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.datanucleus.store.valuegenerator.AbstractDatastoreGenerator;
import org.datanucleus.store.valuegenerator.ValueGenerationBlock;
import org.prot.storage.Key;
import org.prot.storage.Storage;

public class KeyValueGenerator extends AbstractDatastoreGenerator
{
	private static final Logger logger = Logger.getLogger(KeyValueGenerator.class);

	public KeyValueGenerator(String name, Properties props)
	{
		super(name, props);
	}

	@Override
	protected ValueGenerationBlock reserveBlock(long size)
	{
		logger.debug("Reserving keys: " + size);

		// Get the connection
		StorageManagedConnection connection = (StorageManagedConnection) connectionProvider
				.retrieveConnection();

		try
		{
			Storage storage = connection.getStorage();
			List<Key> keys = storage.createKey(StorageHelper.APP_ID, size);
			return new ValueGenerationBlock(keys);
		} finally
		{
			connection.release();
		}
	}

	protected boolean requiresRepository()
	{
		// No we don't require a repository
		return false;
	}

	protected boolean repositoryExists()
	{
		// Repository does always exist (storage creates it)
		return true;
	}

	protected boolean createRepository()
	{
		// We don't create a repository
		return true;
	}
}
