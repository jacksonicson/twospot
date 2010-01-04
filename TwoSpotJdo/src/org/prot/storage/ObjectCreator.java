package org.prot.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.log4j.Logger;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;

public class ObjectCreator
{
	private static final Logger logger = Logger.getLogger(ObjectCreator.class);

	private HBaseManagedConnection connection;

	public ObjectCreator(ConnectionFactory connectionFactory)
	{
		this.connection = connectionFactory.createManagedConnection();
	}

	public ObjectCreator(HBaseManagedConnection connection)
	{
		this.connection = connection;
	}

	public void createObject(String appId, String kind, Key key, byte[] obj) throws IOException
	{
		HTable entitiesTable = StorageUtils.getTableEntity(connection);
		HTable indexByKindTable = StorageUtils.getTableIndexByKind(connection);
		HTable indexByPropertyAsc = StorageUtils.getTableIndexByPropertyAsc(connection);
		HTable indexCustom = null;

		try
		{
			// Write the entity
			logger.debug("Writing entity");
			byte[] rowKey = writeEntity(entitiesTable, appId, kind, key, obj);

			// Update the index tables
			logger.debug("Updating index by kind");
			writeIndexByKind(indexByKindTable, rowKey, appId, kind);

			logger.debug("Updating index by property");
			ObjectRemover remover = new ObjectRemover(connection);
			Map<String, byte[]> index = remover.createIndexMap(obj);
			writeIndexByPropertyAsc(indexByPropertyAsc, rowKey, appId, kind, index);

			logger.debug("Updating custom index");
			writeIndexCustom(indexCustom, rowKey, appId, kind, obj);

		} catch (IOException e)
		{
			logger.error("", e);
			throw e;
		}
	}

	public static final byte[] writeEntity(HTable table, String appId, String kind, Key key, byte[] obj)
			throws IOException
	{
		// Create a new put operation
		byte[] rowKey = KeyHelper.createRowKey(appId, kind, key);
		Put put = new Put(rowKey);
		put.add(StorageUtils.bEntity, StorageUtils.bSerialized, obj);

		// Execute put
		table.put(put);

		// Return the key
		return rowKey;
	}

	private void writeIndexByKind(HTable table, byte[] rowKey, String appId, String kind) throws IOException
	{
		byte[] indexRowKey = KeyHelper.createIndexByKindRowKey(appId, kind, rowKey);

		// Create and execute the put operation
		Put put = new Put(indexRowKey);
		put.add(StorageUtils.bKey, StorageUtils.bKey, rowKey);
		table.put(put);
	}

	void writeIndexByPropertyAsc(HTable table, byte[] rowKey, String appId, String kind,
			Map<String, byte[]> index) throws IOException
	{
		List<Put> putList = new ArrayList<Put>();
		for (String propertyName : index.keySet())
		{
			byte[] bValue = index.get(propertyName);
			if (bValue == null)
				continue;

			byte[] propKey = KeyHelper.createIndexByPropertyKey(appId, kind, rowKey, propertyName, bValue);

			Put put = new Put(propKey);
			put.add(StorageUtils.bKey, StorageUtils.bKey, rowKey);
			putList.add(put);
		}

		// Execute all puts
		table.put(putList);
	}

	private void writeIndexCustom(HTable table, byte[] rowKey, String appId, String kind, byte[] obj)
	{
		// Not implemented
	}
}
