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
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;

public class KeyCreator
{
	private static final Logger logger = Logger.getLogger(KeyCreator.class);

	private HBaseManagedConnection connection;

	public KeyCreator(ConnectionFactory connectionFactory)
	{
		this.connection = connectionFactory.createManagedConnection();
	}

	private void createKeyEntry(HTable table, String appId) throws IOException
	{
		Put put = new Put(Bytes.toBytes(appId));
		put.add(StorageUtils.bCounter, StorageUtils.bCounter, Bytes.toBytes(0l));
		table.put(put);
	}

	public List<Key> fetchKey(String appId, long amount) throws IOException
	{
		HTable table = StorageUtils.getSequenceTable(connection);

		// There is a counter for each application
		Get get = new Get(Bytes.toBytes(appId));

		// Check if the counter row already exists
		if (!table.exists(get))
			createKeyEntry(table, appId);

		boolean updateSuccess = false;
		long counter = 0;
		long incCounter = 0;

		// Some retries to get a key range
		for (int tries = 0; tries < 3 && !updateSuccess; tries++)
		{
			Result result = table.get(get);
			assert (result.size() > 0);

			// Load the counters
			Entry<Long, byte[]> data = result.getMap().get(StorageUtils.bCounter).get(StorageUtils.bCounter)
					.lastEntry();
			byte[] bValue = data.getValue();

			// Counter variables
			counter = Bytes.toLong(bValue);
			incCounter = counter + amount;

			// Put the new counter value
			Put put = new Put(Bytes.toBytes(appId));
			put.add(StorageUtils.bCounter, StorageUtils.bCounter, Bytes.toBytes(incCounter));

			// Check if the counter has changed since reading it
			updateSuccess = table.checkAndPut(Bytes.toBytes(appId), StorageUtils.bCounter,
					StorageUtils.bCounter, Bytes.toBytes(counter), put);
		}

		if (!updateSuccess)
		{
			logger.warn("Could not update sequence counter for " + appId);
			throw new StorageError("Could reservate key sequence");
		}

		// Create the keys
		List<Key> keys = new ArrayList<Key>();
		for (; counter < incCounter; counter++)
		{
			// Encode the time in the key - newer entries are at the top of the
			// hbase table
			long invTime = Long.MAX_VALUE - System.currentTimeMillis();
			byte[] bKey = Bytes.add(Bytes.toBytes(invTime), Bytes.toBytes(counter));

			Key key = new Key();
			key.setKey(bKey);
			keys.add(key);
		}

		return keys;
	}
}
