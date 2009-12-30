package org.prot.stor.hbase;

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

		StorageManagedConnection connection = (StorageManagedConnection) connectionProvider
				.retrieveConnection();
		Storage storage = connection.getStorage();

		List<Key> keys = storage.createKey(HBaseUtils.APP_ID, size);
		return new ValueGenerationBlock(keys);
	}

	protected boolean requiresRepository()
	{
		// No we don't require a repository
		return false;
	}

	protected boolean repositoryExists()
	{
		// Repository does always exist
		return true;
	}

	protected boolean createRepository()
	{
		// We don't create a repository
		return true;
	}
}
