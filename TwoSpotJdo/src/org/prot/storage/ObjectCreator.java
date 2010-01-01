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

	public void createObject(String appId, String kind, Key key, byte[] obj, Map<String, byte[]> index,
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
			// writeIndexCustom(indexCustom, rowKey, appId, kind, index,
			// indexDef);

		} catch (IOException e)
		{
			logger.error("", e);
			throw e;
		}
	}

	byte[] writeEntity(HTable table, String appId, String kind, Key key, byte[] obj) throws IOException
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
		byte[] indexRowKey = KeyHelper.createIndexByKindKey(appId, kind, rowKey);

		// Create and execute the put operation
		Put put = new Put(indexRowKey);
		put.add(StorageUtils.bKey, StorageUtils.bKey, rowKey);
		table.put(put);
	}

	void writeIndexByPropertyAsc(HTable table, byte[] rowKey, String appId, String kind,
			Map<String, byte[]> index) throws IOException
	{
		// Create a put operation for each property name
		List<Put> putList = new ArrayList<Put>();
		for (String propertyName : index.keySet())
		{
			logger.debug("Adding property " + propertyName);

			byte[] propKey = KeyHelper.createIndexByPropertyKey(appId, kind, rowKey, propertyName, index
					.get(propertyName));

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
		throw new NotImplementedException();
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
