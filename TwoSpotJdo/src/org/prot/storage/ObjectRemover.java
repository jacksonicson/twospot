package org.prot.storage;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;
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

	public void removeObject(String appId, String kind, Key key) throws IOException
	{
		HTable tableEntities = getEntitiesTable();
		HTable tableIndexByKind = getIndexByKindTable();
		HTable tableIndexByPropertyAsc = getIndexByPropertyTableAsc();

		logger.debug("Removing object from entities");
		removeObjectFromEntities(tableEntities, appId, kind, key);
	}

	private void removeObjectFromEntities(HTable table, String appId, String kind, Key key)
			throws IOException
	{
		byte[] rowKey = StorageUtils.createRowKey(appId, kind, key);

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
