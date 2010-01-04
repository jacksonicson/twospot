package org.prot.storage.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.storage.KeyHelper;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;

public class AtomarCondition implements Serializable
{
	private static final long serialVersionUID = 2659834747381329323L;

	private static final Logger logger = Logger.getLogger(AtomarCondition.class);

	// Condition type (equals, lower than, greater than, greater)
	private ConditionType type;

	// Property (left side)
	private AtomLiteral property;

	// Value to check (right side)
	private AtomLiteral value;

	public AtomarCondition(ConditionType type, AtomLiteral property, AtomLiteral value)
	{
		this.type = type;
		this.property = property;
		this.value = value;
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

			stopKey = Bytes.add(startKey, KeyHelper.getArrayOfOnes());

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
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, KeyHelper.getArrayOfOnes());

			return new Scan(startKey, stopKey);

		case GREATER:
			// Schema: appId/Kind/property/value/entityKey
			// Start: gogo/Person/username/Bob++/0x00
			// Stop: gogo/Person/username/0xFFFF

			bValue = KeyHelper.incrementByteArray(bValue);

			startKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bProperty);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bValue);

			stopKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, bProperty);
			stopKey = Bytes.add(stopKey, StorageUtils.bSlash, KeyHelper.getArrayOfOnes());

			return new Scan(startKey, stopKey);

		case LOWER_EQUALS:
			// Schema: appId/Kind/property/value/entityKey
			// Start: gogo/Person/username/0x00/0x00
			// Stop: gogo/Person/username/Bob++/0xFFFF
			startKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, bProperty);
			startKey = Bytes.add(startKey, StorageUtils.bSlash, new byte[] { 0 });

			bValue = KeyHelper.incrementByteArray(bValue);

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

	private List<byte[]> findByIndex(StorageQuery query, HTable indexTable, LimitCondition limit)
			throws IOException
	{
		byte[] bAppId = Bytes.toBytes(query.getAppId());
		byte[] bKind = Bytes.toBytes(query.getKind());
		byte[] bProperty = property.getValue();
		byte[] bValue = value.getValue();

		// Create the scanner
		Scan scan = createScanner(bAppId, bKind, bProperty, bValue);
		ResultScanner resultScanner = indexTable.getScanner(scan);

		// Create a new set for the results
		List<byte[]> entityKeys = new ArrayList<byte[]>();

		// Scan the table
		for (Iterator<Result> it = resultScanner.iterator(); it.hasNext();)
		{
			// Check limits
			if(!limit.incrementIndex())
				break;
			
			// Get the next result
			Result result = it.next();
			if (result.getMap() == null)
				continue;

			// Extract the entity key
			byte[] entityKey = result.getMap().get(StorageUtils.bKey).get(StorageUtils.bKey).lastEntry()
					.getValue();
			entityKeys.add(entityKey);
		}

		return entityKeys;
	}

	void run(HBaseManagedConnection connection, StorageQuery query, Collection<byte[]> result, LimitCondition limit)
			throws IOException
	{
		logger.debug("Running atomar condition of type: " + type);
		logger.debug("Property is: " + property);
		logger.debug("Value is: " + new String(value.getValue()));

		// Get the tables
		HTable tableEntities = StorageUtils.getTableEntity(connection);
		HTable tableIndex = StorageUtils.getTableIndexByPropertyAsc(connection);

		// Find the entity keys using the index table
		List<byte[]> entityKeys = null;
		switch (type)
		{
		case EQUALS:
		case GREATER:
		case GREATER_EQUALS:
		case LOWER_EQUALS:
		case LOWER:
			// Lookup in the index-table
			entityKeys = findByIndex(query, tableIndex, limit);

			// Materialize the results
			StorageUtils.materialize(tableEntities, entityKeys, result, limit);
			break;

		default:
			logger.warn("Unsupported condition operator");
		}
	}
}
