package org.prot.storage;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.log4j.Logger;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;

public class ObjectUpdater
{
	private static final Logger logger = Logger.getLogger(ObjectUpdater.class);

	private HBaseManagedConnection connection;

	private ObjectCreator creator;

	private ObjectRemover remover;

	public ObjectUpdater(ConnectionFactory connectionFactory)
	{
		this.connection = connectionFactory.createManagedConnection();

		this.creator = new ObjectCreator(connectionFactory);
		this.remover = new ObjectRemover(connectionFactory);
	}

	public void updateObject(String appId, String kind, Key key, byte[] obj) throws IOException,
			ClassNotFoundException

	{
		HTable tableEntities = getEntitiesTable();
		HTable tableIndexByPropertyAsc = getIndexByPropertyTableAsc();

		logger.debug("Removing entity from IndexByProperty");
		byte[] oldObj = remover.retrieveObject(tableEntities, appId, kind, key);
		Map<String, byte[]> index = remover.createIndexMap(oldObj);
		remover.removeObjectFromIndexByProperty(tableIndexByPropertyAsc, appId, kind, key, index);

		logger.debug("Updating entity in the Entities table");
		byte[] rowKey = creator.writeEntity(tableEntities, appId, kind, key, obj);

		logger.debug("CreatingIndexByProperty");
		creator.writeIndexByPropertyAsc(tableIndexByPropertyAsc, rowKey, appId, kind, index);
	}

	private HTable getIndexByPropertyTableAsc()
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_INDEX_BY_PROPERTY_ASC);
		return table;
	}

	private HTable getEntitiesTable()
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_ENTITIES);
		return table;
	}
}
