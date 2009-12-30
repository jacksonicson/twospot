package org.prot.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.stor.hbase.HBaseManagedConnection;
import org.prot.stor.hbase.Key;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.StorageUtils;

public class KeyCreator
{
	private static final Logger logger = Logger.getLogger(KeyCreator.class);

	private HBaseManagedConnection connection;

	private final byte[] family = Bytes.toBytes("counter");
	private final byte[] qualifier = Bytes.toBytes("counter");

	public KeyCreator(ConnectionFactory connectionFactory)
	{
		this.connection = (HBaseManagedConnection) connectionFactory.createManagedConnection();
	}

	private void createKeyEntry(HTable table, String appId) throws IOException
	{
		logger.debug("Creating key entry for " + appId);
		Put put = new Put(Bytes.toBytes(appId));
		put.add(family, qualifier, Bytes.toBytes(0l));
		table.put(put);
	}

	public List<Key> fetchKey(String appId, int amount) throws IOException
	{
		HTable table = getSequenceTable();

		// There is a counter for each application
		Get get = new Get(Bytes.toBytes(appId));

		// Check if the counter row already exists
		if (!table.exists(get))
			createKeyEntry(table, appId);

		boolean updateSuccess = false;
		long counter = 0;
		long incCounter = 0;
		for (int tries = 0; tries < 3 && !updateSuccess; tries++)
		{
			Result result = table.get(get);
			assert (result.size() > 0);

			// Load the counters
			Entry<Long, byte[]> data = result.getMap().get(family).get(qualifier).lastEntry();
			byte[] bValue = data.getValue();

			// Counter variables
			counter = Bytes.toLong(bValue);
			incCounter = counter + amount;

			logger.debug("Incremented sequence counter: " + incCounter);

			// Put the new counter value
			Put put = new Put(Bytes.toBytes(appId));
			put.add(family, qualifier, Bytes.toBytes(incCounter));

			// Check if the counter has changed since reading it
			updateSuccess = table.checkAndPut(Bytes.toBytes(appId), family, qualifier,
					Bytes.toBytes(counter), put);
		}

		if (!updateSuccess)
		{
			logger.warn("Could not update sequence counter for " + appId);
			// TODO: Throw an exception
			return null;
		}

		// Create the keys
		List<Key> keys = new ArrayList<Key>();
		for (; counter < incCounter; counter++)
		{
			Key key = new Key();
			key.setKey(Bytes.toBytes(counter));
			keys.add(key);
		}

		return keys;
	}

	private HTable getSequenceTable()
	{
		String tableName = StorageUtils.TABLE_SEQUENCES;
		return connection.getHTable(tableName);
	}
}
