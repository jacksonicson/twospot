package org.prot.storage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;

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
		Object obj = retrieveObject(tableEntities, appId, kind, key);
		Map<String, byte[]> indexMap = createIndexMap(obj);

		logger.debug("Removing object from entities");
		removeObjectFromEntities(tableEntities, appId, kind, key);

		logger.debug("Removing object from IndexByKind");
		removeObjectFromIndexByKind(tableIndexByKind, appId, kind, key);

		logger.debug("Removing object from IndexByProperty");
		removeObjectFromIndexByProperty(tableIndexByPropertyAsc, appId, kind, key, indexMap);
	}

	private Map<String, byte[]> createIndexMap(Object obj)
	{
		Map<String, byte[]> index = new HashMap<String, byte[]>();

		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields)
		{
			String fieldName = field.getName();
			try
			{
				Method method = obj.getClass().getMethod(
						"get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1));
				Object fieldValue = method.invoke(obj);
				if (fieldValue != null)
					index.put(fieldName, fieldValue.toString().getBytes());

			} catch (IllegalArgumentException e)
			{
				continue;
			} catch (IllegalAccessException e)
			{
				continue;
			} catch (SecurityException e)
			{
				continue;
			} catch (NoSuchMethodException e)
			{
				continue;
			} catch (InvocationTargetException e)
			{
				continue;
			}
		}

		return index;
	}

	private Object retrieveObject(HTable table, String appId, String kind, Key key) throws IOException,
			ClassNotFoundException
	{
		byte[] rowKey = StorageUtils.createRowKey(appId, kind, key);

		Get get = new Get(rowKey);
		Result result = table.get(get);

		if (result.getMap() == null)
			return null;

		byte[] data = result.getMap().get(StorageUtils.bEntity).get(StorageUtils.bSerialized).lastEntry()
				.getValue();
		return StorageUtils.deserialize(data);
	}

	private void removeObjectFromIndexByProperty(HTable table, String appId, String kind, Key key,
			Map<String, byte[]> index) throws IOException
	{
		byte[] rowKey = StorageUtils.createRowKey(appId, kind, key);

		byte[] bAppId = Bytes.toBytes(appId);
		byte[] bKind = Bytes.toBytes(kind);

		// Create a put operation for each property name
		ArrayList<Delete> deleteList = new ArrayList<Delete>();
		for (String propertyName : index.keySet())
		{
			logger.debug("Removing property " + propertyName);

			byte[] bPropertyName = Bytes.toBytes(propertyName);

			byte[] propKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
			propKey = Bytes.add(propKey, StorageUtils.bSlash, bPropertyName);
			propKey = Bytes.add(propKey, StorageUtils.bSlash, index.get(propertyName));
			propKey = Bytes.add(propKey, StorageUtils.bSlash, rowKey);

			logger.debug("Removing object from IndexByProperty " + propKey);

			Delete delete = new Delete(propKey);
			deleteList.add(delete);
		}

		// Execute all puts
		table.delete(deleteList);
	}

	private void removeObjectFromIndexByKind(HTable table, String appId, String kind, Key key)
			throws IOException
	{
		byte[] rowKey = StorageUtils.createRowKey(appId, kind, key);

		// Construct the index key
		byte[] bAppId = Bytes.toBytes(appId);
		byte[] bKind = Bytes.toBytes(kind);

		byte[] indexRowKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
		indexRowKey = Bytes.add(indexRowKey, StorageUtils.bSlash, rowKey);

		Get get = new Get(indexRowKey);
		if (!table.exists(get))
			throw new IOException("Cannot delete index - row does not exist");

		Delete delete = new Delete(indexRowKey);
		table.delete(delete);
	}

	private void removeObjectFromEntities(HTable table, String appId, String kind, Key key)
			throws IOException
	{
		byte[] rowKey = StorageUtils.createRowKey(appId, kind, key);

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
