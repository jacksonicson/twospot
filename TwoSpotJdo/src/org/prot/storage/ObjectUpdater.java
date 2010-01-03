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

		logger.debug("Removing entity from index IndexByProperty");
		byte[] oldObj = remover.retrieveObject(tableEntities, appId, kind, key);
		Map<String, byte[]> index = remover.createIndexMap(oldObj);
		remover.removeObjectFromIndexByProperty(tableIndexByPropertyAsc, appId, kind, key, index);

		logger.debug("Updating entity in the Entities table");
		byte[] rowKey = creator.writeEntity(tableEntities, appId, kind, key, obj);

		logger.debug("Creating index IndexByProperty");
		creator.writeIndexByPropertyAsc(tableIndexByPropertyAsc, rowKey, appId, kind, index);
	}
}
