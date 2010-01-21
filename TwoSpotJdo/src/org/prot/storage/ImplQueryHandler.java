package org.prot.storage;

import java.io.IOException;
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
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;
import org.prot.storage.query.AtomarCondition;
import org.prot.storage.query.LimitCondition;
import org.prot.storage.query.QueryHandler;
import org.prot.storage.query.StorageQuery;

public class ImplQueryHandler implements QueryHandler
{
	private static final Logger logger = Logger.getLogger(ImplQueryHandler.class);

	private HBaseManagedConnection connection;

	public ImplQueryHandler(ConnectionFactory connectionFactory)
	{
		this.connection = (HBaseManagedConnection) connectionFactory.createManagedConnection();
	}

	@Override
	public void execute(Collection<byte[]> result, StorageQuery query) throws IOException
	{
		if (query.getKey() != null)
		{
			fetchByKey(query, result);

		} else if (query.getKind() != null)
		{
			fetchByKind(query, result);
		}
	}

	private void fetchByKey(StorageQuery query, Collection<byte[]> result) throws IOException
	{
		HTable entityTable = StorageUtils.getTableEntity(connection);

		byte[] rowKey = KeyHelper.createRowKey(query.getAppId(), query.getKind(), query.getKey());
		List<byte[]> keySet = new ArrayList<byte[]>();
		keySet.add(rowKey);

		StorageUtils.materialize(entityTable, keySet, result, query.getLimit());
	}

	private void fetchByKind(StorageQuery query, Collection<byte[]> result) throws IOException
	{
		HTable entityTable = StorageUtils.getTableEntity(connection);
		HTable indexByKindTable = StorageUtils.getTableIndexByKind(connection);

		byte[] startKey = KeyHelper.createIndexByKindKey(query.getAppId(), query.getKind());

		byte[] bAppId = Bytes.toBytes(query.getAppId());
		byte[] bKind = Bytes.toBytes(query.getKind());
		bKind = KeyHelper.incrementByteArray(bKind);
		byte[] stopKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);

		Scan scan = new Scan(startKey, stopKey);
		ResultScanner scanner = indexByKindTable.getScanner(scan);
		List<byte[]> keySet = new ArrayList<byte[]>();
		for (Iterator<Result> iterator = scanner.iterator(); iterator.hasNext();)
		{
			// Update limits
			if (!query.getLimit().incrementIndex())
				break;

			// Fetch result
			Result res = iterator.next();
			if (res.getMap() == null)
				continue;

			// Update keyset
			byte[] rowKey = res.getMap().get(StorageUtils.bKey).get(StorageUtils.bKey).lastEntry().getValue();
			keySet.add(rowKey);
		}

		// Materialize all entities
		StorageUtils.materialize(entityTable, keySet, result, query.getLimit());
	}

	@Override
	public void execute(Collection<byte[]> result, StorageQuery query, AtomarCondition condition)
			throws IOException
	{
		// Get the tables
		HTable tableEntities = StorageUtils.getTableEntity(connection);
		HTable tableIndex = StorageUtils.getTableIndexByPropertyAsc(connection);

		// Find the entity keys using the index table
		List<byte[]> entityKeys = null;
		switch (condition.getType())
		{
		case EQUALS:
		case GREATER:
		case GREATER_EQUALS:
		case LOWER_EQUALS:
		case LOWER:
			// Lookup in the index-table
			entityKeys = findByIndex(condition, query, tableIndex, query.getLimit());

			// Materialize the results
			StorageUtils.materialize(tableEntities, entityKeys, result, query.getLimit());
			break;

		default:
			logger.warn("Unsupported condition operator");
		}

	}

	private Scan createScanner(AtomarCondition condition, byte[] bAppId, byte[] bKind, byte[] bProperty,
			byte[] bValue)
	{
		byte[] startKey;
		byte[] stopKey;

		switch (condition.getType())
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

	private List<byte[]> findByIndex(AtomarCondition condition, StorageQuery query, HTable indexTable,
			LimitCondition limit) throws IOException
	{
		byte[] bAppId = Bytes.toBytes(query.getAppId());
		byte[] bKind = Bytes.toBytes(query.getKind());
		byte[] bProperty = condition.getProperty().getValue();
		byte[] bValue = condition.getValue().getValue();

		// Create the scanner
		Scan scan = createScanner(condition, bAppId, bKind, bProperty, bValue);
		ResultScanner resultScanner = indexTable.getScanner(scan);

		// Create a new set for the results
		List<byte[]> entityKeys = new ArrayList<byte[]>();

		// Scan the table
		for (Iterator<Result> it = resultScanner.iterator(); it.hasNext();)
		{
			// Check limits
			if (!limit.incrementIndex())
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
}
