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

		this.creator = new ObjectCreator(connection);

		this.remover = new ObjectRemover(connection);
	}

	public void updateObject(String appId, String kind, Key key, byte[] obj) throws IOException,
			ClassNotFoundException

	{
		HTable tableEntities = StorageUtils.getTableEntity(connection);
		HTable tableIndexByPropertyAsc = StorageUtils.getTableIndexByPropertyAsc(connection);

		// Retrieve the entity
		byte[] oldObj = remover.retrieveObject(tableEntities, appId, kind, key);
		Map<String, byte[]> oldIndex = remover.createIndexMap(oldObj);
		Map<String, byte[]> newIndex = remover.createIndexMap(obj);

		// Remove the entity from the indey by property table
		remover.removeObjectFromIndexByProperty(tableIndexByPropertyAsc, appId, kind, key, oldIndex);

		// We don't have to delete the index by kind table because the key doesn't change!
		
		// Write the new entity to the entity table
		byte[] rowKey = ObjectCreator.writeEntity(tableEntities, appId, kind, key, obj);

		// Recreate the index by property table
		creator.writeIndexByPropertyAsc(tableIndexByPropertyAsc, rowKey, appId, kind, newIndex);
	}
}
