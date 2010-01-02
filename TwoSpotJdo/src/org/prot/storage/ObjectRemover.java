package org.prot.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.jdo.storage.messages.EntityMessage;
import org.prot.jdo.storage.messages.IndexMessage;
import org.prot.jdo.storage.messages.types.IStorageProperty;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;

import com.google.protobuf.InvalidProtocolBufferException;

public class ObjectRemover
{
	private static final Logger logger = Logger.getLogger(ObjectRemover.class);

	private HBaseManagedConnection connection;

	public ObjectRemover(ConnectionFactory connectionFactory)
	{
		this.connection = (HBaseManagedConnection) connectionFactory.createManagedConnection();
	}

	public void removeObject(String appId, String kind, Key key) throws IOException, ClassNotFoundException
	{
		HTable tableEntities = getEntitiesTable();
		HTable tableIndexByKind = getIndexByKindTable();
		HTable tableIndexByPropertyAsc = getIndexByPropertyTableAsc();

		logger.debug("Retrieving the entity");
		byte[] obj = retrieveObject(tableEntities, appId, kind, key);
		Map<String, byte[]> indexMap = createIndexMap(obj);

		logger.debug("Removing object from entities");
		removeObjectFromEntities(tableEntities, appId, kind, key);

		logger.debug("Removing object from IndexByKind");
		removeObjectFromIndexByKind(tableIndexByKind, appId, kind, key);

		logger.debug("Removing object from IndexByProperty");
		removeObjectFromIndexByProperty(tableIndexByPropertyAsc, appId, kind, key, indexMap);
	}

	Map<String, byte[]> createIndexMap(byte[] obj) throws InvalidProtocolBufferException
	{
		// Deserialize the message (get the index)
		EntityMessage.Builder builder = EntityMessage.newBuilder();
		builder.mergeFrom(obj);
		EntityMessage entityMsg = builder.build();

		// Get all index messages
		List<IndexMessage> indexMsgs = entityMsg.getIndexMessages();

		// Index map
		Map<String, byte[]> index = new HashMap<String, byte[]>();
		for (IndexMessage indexMsg : indexMsgs)
		{
			String propertyName = indexMsg.getFieldName();

			IStorageProperty property = entityMsg.getProperty(propertyName);
			byte[] bValue = property.getValueAsBytes();
			if (bValue == null)
			{
				logger.debug("Not adding null properties to index map");
				continue;
			}

			logger.debug("Adding property to index map: " + propertyName);
			index.put(propertyName, bValue);
		}

		return index;
	}

	byte[] retrieveObject(HTable table, String appId, String kind, Key key) throws IOException,
			ClassNotFoundException
	{
		byte[] rowKey = KeyHelper.createRowKey(appId, kind, key);

		Get get = new Get(rowKey);
		Result result = table.get(get);

		if (result.getMap() == null)
			return null;

		byte[] data = result.getMap().get(StorageUtils.bEntity).get(StorageUtils.bSerialized).lastEntry()
				.getValue();
		return data;
	}

	void removeObjectFromIndexByProperty(HTable table, String appId, String kind, Key key,
			Map<String, byte[]> index) throws IOException
	{
		byte[] rowKey = KeyHelper.createRowKey(appId, kind, key);

		// Create a put operation for each property name
		ArrayList<Delete> deleteList = new ArrayList<Delete>();
		for (String propertyName : index.keySet())
		{
			byte[] propKey = KeyHelper.createIndexByPropertyKey(appId, kind, rowKey, propertyName, index
					.get(propertyName));

			propKey = Bytes.add(propKey, StorageUtils.bSlash, Bytes.toBytes(propertyName));
			propKey = Bytes.add(propKey, StorageUtils.bSlash, index.get(propertyName));
			propKey = Bytes.add(propKey, StorageUtils.bSlash, rowKey);

			Delete delete = new Delete(propKey);
			deleteList.add(delete);
		}

		// Execute all puts
		table.delete(deleteList);
	}

	private void removeObjectFromIndexByKind(HTable table, String appId, String kind, Key key)
			throws IOException
	{
		byte[] rowKey = KeyHelper.createRowKey(appId, kind, key);
		byte[] indexRowKey = KeyHelper.createIndexByKindRowKey(appId, kind, rowKey);

		Get get = new Get(indexRowKey);
		if (!table.exists(get))
			throw new IOException("Cannot delete index - row does not exist");

		Delete delete = new Delete(indexRowKey);
		table.delete(delete);
	}

	private void removeObjectFromEntities(HTable table, String appId, String kind, Key key)
			throws IOException
	{
		byte[] rowKey = KeyHelper.createRowKey(appId, kind, key);

		Get get = new Get(rowKey);
		if (!table.exists(get))
			throw new IOException("Cannot delete entity - row does not exist");

		Delete delete = new Delete(rowKey);
		table.delete(delete);
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
