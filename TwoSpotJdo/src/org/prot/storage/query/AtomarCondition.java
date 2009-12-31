package org.prot.storage.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;

public class AtomarCondition implements Serializable
{
	private static final long serialVersionUID = 2659834747381329323L;

	private static final Logger logger = Logger.getLogger(AtomarCondition.class);

	private ConditionType type;

	private AtomLiteral property;
	private AtomLiteral value;

	public AtomarCondition(ConditionType type, AtomLiteral property, AtomLiteral value)
	{
		this.type = type;
		this.property = property;
		this.value = value;
	}

	private HTable getTableEntity(HBaseManagedConnection connection)
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_ENTITIES);
		return table;
	}

	private HTable getTableIndexByPropertyAsc(HBaseManagedConnection connection)
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_INDEX_BY_PROPERTY_ASC);
		return table;
	}

	private Scan createScanner(byte[] bAppId, byte[] bKind, byte[] bProperty, byte[] bValue)
	{
		byte[] startKey;
		byte[] stopKey;

		switch (type)
		{
		case EQUALS:
			// Schema: appId/Kind/property/value/entityKey
			// Start: gogo/Person/username/Bob/0x00
			// Stop: gogo/Person/username/Bob/0xFFFF
			startKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bProperty);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bValue);
			startKey = Bytes.add(startKey, StorageUtils.bSlash);

			stopKey = Bytes.add(startKey, StorageUtils.getArrayOfOnes());

			return new Scan(startKey, stopKey);

		case GREATER_EQUALS:
			// Schema: appId/Kind/property/value/entityKey
			// Start: gogo/Person/username/Bob/0x00
			// Stop: gogo/Person/username/0xFFFF
			startKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bProperty);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bValue);

			stopKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, bProperty);
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, StorageUtils.getArrayOfOnes());

			return new Scan(startKey, stopKey);

		case GREATER:
			// Schema: appId/Kind/property/value/entityKey
			// Start: gogo/Person/username/Bob++/0x00
			// Stop: gogo/Person/username/0xFFFF

			bValue = StorageUtils.incrementByteArray(bValue);

			startKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bProperty);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bValue);

			stopKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, bProperty);
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, StorageUtils.getArrayOfOnes());

			return new Scan(startKey, stopKey);

		case LOWER_EQUALS:
			// Schema: appId/Kind/property/value/entityKey
			// Start: gogo/Person/username/0x00/0x00
			// Stop: gogo/Person/username/Bob++/0xFFFF
			startKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bProperty);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, new byte[] { 0 });

			bValue = StorageUtils.incrementByteArray(bValue);

			stopKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, bProperty);
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, bValue);

			return new Scan(startKey, stopKey);

		case LOWER:
			// Schema: appId/Kind/property/value/entityKey
			// Start: gogo/Person/username/0x00/0x00
			// Stop: gogo/Person/username/Bob/0xFFFF
			startKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bProperty);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, new byte[] { 0 });

			stopKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, bProperty);
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, bValue);

			return new Scan(startKey, stopKey);
		}

		return null;
	}

	private Set<byte[]> findIn(StorageQuery query, HTable indexTable, LimitCondition limit)
			throws IOException
	{
		byte[] bAppId = Bytes.toBytes(query.getAppId());
		byte[] bKind = Bytes.toBytes(query.getKind());
		byte[] bProperty = property.getValue();
		byte[] bValue = value.getValue();

		// Create the scanner
		Scan scan = createScanner(bAppId, bKind, bProperty, bValue);
		logger.debug("Starting at: " + new String(scan.getStartRow()));
		ResultScanner resultScanner = indexTable.getScanner(scan);

		// Create a new set for the results
		Set<byte[]> entityKeys = new HashSet<byte[]>();

		// Scan the table
		for (Iterator<Result> it = resultScanner.iterator(); it.hasNext() && limit.isInRange();)
		{
			Result result = it.next();
			if (result.getMap() == null)
				continue;

			// Increment the limit counter
			limit.increment();

			// Extract the entity key
			byte[] entityKey = result.getMap().get(StorageUtils.bKey).get(StorageUtils.bKey).lastEntry()
					.getValue();
			entityKeys.add(entityKey);
		}

		return entityKeys;
	}

	private void materialize(HTable entityTable, Set<byte[]> keys, List<Object> result) throws IOException,
			ClassNotFoundException
	{
		logger.debug("Materializing entities");

		for (byte[] key : keys)
		{
			Get get = new Get(key);
			Result entity = entityTable.get(get);
			if (entity.getMap() == null)
			{
				logger.warn("Could not fetch the entity");
				continue;
			}

			byte[] data = entity.getMap().get(StorageUtils.bEntity).get(StorageUtils.bSerialized)
					.firstEntry().getValue();

			Object obj = StorageUtils.deserialize(data);
			result.add(obj);
		}
	}

	void run(HBaseManagedConnection connection, StorageQuery query, List<Object> result, LimitCondition limit)
			throws IOException, ClassNotFoundException
	{
		logger.debug("Running atomar condition of type: " + type);
		logger.debug("Property is: " + property);
		logger.debug("Value is: " + new String(value.getValue()));

		// Get the tables
		HTable tableEntities = getTableEntity(connection);
		HTable tableIndex = getTableIndexByPropertyAsc(connection);

		// Find the entity keys using the index table
		Set<byte[]> entityKeys = null;
		switch (type)
		{
		case EQUALS:
		case GREATER:
		case GREATER_EQUALS:
		case LOWER_EQUALS:
		case LOWER:
			// Lookup in the index-table
			entityKeys = findIn(query, tableIndex, limit);

			// Materialize the results
			materialize(tableEntities, entityKeys, result);
			break;

		default:
			logger.warn("Unsupported condition operator");
		}
	}
}
