package org.prot.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.metrics.Updater;
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

	public void createObject(String appId, String kind, Key key, Object obj, Map<String, byte[]> index,
			IndexDefinition indexDef) throws IOException
	{
		HTable entitiesTable = getEntitiesTable();
		HTable indexByKindTable = getIndexByKindTable();
		HTable indexByPropertyAsc = getIndexByPropertyTableAsc();
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
			writeIndexByPropertyAsc(indexByPropertyAsc, rowKey, appId, kind, index);

			logger.debug("Updating custom index");
			writeIndexCustom(indexCustom, rowKey, appId, kind, index, indexDef);

		} catch (IOException e)
		{
			logger.error("", e);
			throw e;
		}
	}

	byte[] writeEntity(HTable table, String appId, String kind, Key key, Object obj)
			throws IOException
	{
		// Get the serialized version of the object
		byte[] serObj = StorageUtils.serialize(obj);

		// Create a new put operation
		byte[] rowKey = createRowKey(appId, kind, key);
		Put put = new Put(rowKey);
		put.add(StorageUtils.bEntity, StorageUtils.bSerialized, serObj);

		// Execute put
		table.put(put);

		// Return the key
		return rowKey;
	}

	private void writeIndexByKind(HTable table, byte[] rowKey, String appId, String kind) throws IOException
	{
		// Construct the index key
		byte[] bAppId = Bytes.toBytes(appId);
		byte[] bKind = Bytes.toBytes(kind);

		byte[] indexRowKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
		indexRowKey = Bytes.add(indexRowKey, StorageUtils.bSlash, rowKey);

		// Create and execute the put operation
		Put put = new Put(indexRowKey);
		put.add(StorageUtils.bKey, StorageUtils.bKey, rowKey);
		table.put(put);
	}

	void writeIndexByPropertyAsc(HTable table, byte[] rowKey, String appId, String kind,
			Map<String, byte[]> index) throws IOException
	{
		byte[] bAppId = Bytes.toBytes(appId);
		byte[] bKind = Bytes.toBytes(kind);

		// Create a put operation for each property name
		List<Put> putList = new ArrayList<Put>();
		for (String propertyName : index.keySet())
		{
			logger.debug("Adding property " + propertyName);

			byte[] bPropertyName = Bytes.toBytes(propertyName);

			byte[] propKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			propKey = Bytes.add(propKey, StorageUtils.bSlash, bPropertyName);
			propKey = Bytes.add(propKey, StorageUtils.bSlash, index.get(propertyName));
			propKey = Bytes.add(propKey, StorageUtils.bSlash, rowKey);

			Put put = new Put(propKey);
			put.add(StorageUtils.bKey, StorageUtils.bKey, rowKey);
			putList.add(put);
		}

		// Execute all puts
		table.put(putList);
	}

	private void writeIndexCustom(HTable table, byte[] rowKey, String appId, String kind,
			Map<String, byte[]> index, IndexDefinition indexDef)
	{
		// TODO:
	}

	private byte[] createRowKey(String appId, String kind, Key key)
	{
		assert (appId.length() < 20);
		int diff = 20 - appId.length();
		byte[] bAppId = appId.getBytes();
		byte[] bDiff = new byte[diff];
		bAppId = Bytes.add(bAppId, bDiff);

		assert (kind.length() < 20);
		diff = 20 - kind.length();
		byte[] bKind = kind.getBytes();
		bDiff = new byte[diff];
		bKind = Bytes.add(bKind, bDiff);

		byte[] bKey = key.getKey();

		return Bytes.add(Bytes.add(bAppId, bKind), bKey);
	}

	private HTable getIndexByPropertyTableAsc()
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_INDEX_BY_PROPERTY_ASC);
		return table;
	}

	private HTable getIndexByKindTable()
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_INDEX_BY_KIND);
		return table;
	}

	private HTable getEntitiesTable()
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_ENTITIES);
		return table;
	}
}
