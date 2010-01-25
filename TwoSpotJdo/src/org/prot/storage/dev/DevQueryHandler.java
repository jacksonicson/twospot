package org.prot.storage.dev;

import java.io.IOException;
import java.util.Collection;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.jdo.storage.messages.EntityMessage;
import org.prot.jdo.storage.messages.types.IStorageProperty;
import org.prot.storage.Key;
import org.prot.storage.query.AtomarCondition;
import org.prot.storage.query.QueryHandler;
import org.prot.storage.query.StorageQuery;

public class DevQueryHandler implements QueryHandler
{
	private static final Logger logger = Logger.getLogger(DevQueryHandler.class);

	private MemStorage storage;

	public DevQueryHandler(MemStorage storage)
	{
		this.storage = storage;
	}

	@Override
	public void execute(Collection<byte[]> result, StorageQuery query) throws IOException
	{
		if (query.getKey() != null)
		{
			Key key = query.getKey();
			for (MemTable table : storage.getAllTables())
			{
				byte[] entity = table.get(key);
				if (entity != null)
				{
					result.add(entity);
					return;
				}
			}
		} else if (query.getKind() != null)
		{
			MemTable table = storage.getTable(query.getKind());
			for (byte[] object : table.getAll())
			{
				// Check limits
				if (!query.getLimit().incrementResult())
					return;

				result.add(object);
			}
		}
	}

	@Override
	public void execute(Collection<byte[]> result, StorageQuery query, AtomarCondition condition)
			throws IOException
	{
		// Find the entity keys using the index table
		switch (condition.getType())
		{
		case EQUALS:
		case GREATER:
		case GREATER_EQUALS:
		case LOWER_EQUALS:
		case LOWER:

			byte[] property = condition.getProperty().getValue();
			byte[] value = condition.getValue().getValue();

			MemTable table = storage.getTable(query.getKind());
			for (byte[] entity : table.getAll())
			{
				// Check limits
				if (!query.getLimit().incrementResult())
					return;

				// Deserialize the message (get the index)
				EntityMessage.Builder builder = EntityMessage.newBuilder();
				builder.mergeFrom(entity);
				EntityMessage entityMsg = builder.build();

				IStorageProperty storageProperty = entityMsg.getProperty(new String(property));

				if (storageProperty == null || storageProperty.getValueAsBytes() == null)
					continue;

				byte[] compValue = storageProperty.getValueAsBytes();
				switch (condition.getType())
				{
				case EQUALS:
					if (Bytes.equals(compValue, value))
						result.add(entity);
					break;
				case GREATER:
					if (Bytes.compareTo(compValue, value) > 0)
						result.add(entity);
					break;

				case GREATER_EQUALS:
					if (Bytes.compareTo(compValue, value) >= 0)
						result.add(entity);
					break;

				case LOWER:
					if (Bytes.compareTo(compValue, value) < 0)
						result.add(entity);
					break;

				case LOWER_EQUALS:
					if (Bytes.compareTo(compValue, value) <= 0)
						result.add(entity);
					break;
				}
			}

			break;

		default:
			logger.warn("Unsupported condition operator");
		}
	}
}
