package org.prot.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

	private static Random random;

	private HBaseManagedConnection connection;

	private static final int SHARDS = 20;

	private static final long SHARD_SIZE = Long.MAX_VALUE / SHARDS;

	public KeyCreator(ConnectionFactory connectionFactory)
	{
		random = new Random(System.currentTimeMillis());
		this.connection = connectionFactory.createManagedConnection();
	}

	private void createCounterShard(HTable table, String appId) throws IOException
	{
		// Counter shardening
		List<Put> shard = new ArrayList<Put>();
		for (long i = 0; i < SHARDS; i++)
		{
			Put put = new Put(Bytes.add(Bytes.toBytes(appId), Bytes.toBytes(i)));
			put.add(StorageUtils.bCounter, StorageUtils.bCounter, Bytes.toBytes(0l));
			shard.add(put);
		}

		table.put(shard);
	}

	public List<Key> fetchKey(String appId, long amount) throws IOException
	{
		HTable table = StorageUtils.getSequenceTable(connection);

		// Select a shard
		long shard = 0;
		byte[] shardName = Bytes.add(Bytes.toBytes(appId), Bytes.toBytes(shard));

		// There is a counter for each application
		Get get = new Get(shardName);

		// Check if the counter row already exists
		if (!table.exists(get))
			createCounterShard(table, appId);

		// Status
		boolean updateSuccess = false;
		long counter = 0;
		long incCounter = 0;

		// Some retries to get a key range
		for (int tries = 0; tries < 3 && !updateSuccess; tries++)
		{
			// Select a shart
			shard = random.nextInt(SHARDS);
			shardName = Bytes.add(Bytes.toBytes(appId), Bytes.toBytes(shard));

			// Use the current shard
			get = new Get(shardName);

			// Current sharded counter value
			Result result = table.get(get);
			if (result.size() <= 0)
				throw new NullPointerException("no reuslsts");

			// Load the counters
			Entry<Long, byte[]> data = result.getMap().get(StorageUtils.bCounter).get(StorageUtils.bCounter)
					.lastEntry();
			byte[] bValue = data.getValue();

			// Counter variables
			counter = Bytes.toLong(bValue);
			incCounter = counter + amount;

			// Put the new counter value
			Put put = new Put(shardName);
			put.add(StorageUtils.bCounter, StorageUtils.bCounter, Bytes.toBytes(incCounter));

			// Check if the counter has changed since reading it
			updateSuccess = table.checkAndPut(shardName, StorageUtils.bCounter, StorageUtils.bCounter, Bytes
					.toBytes(counter), put);
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
			// Encode a timestamp in the key
			long invTime = Long.MAX_VALUE - System.currentTimeMillis();

			// Encode a random value which distributes the entities
			long random = KeyCreator.random.nextLong();

			// Calculate the shard offset
			long shardedIndex = shard * SHARD_SIZE + counter;

			// Key has the form: RANDOM | INV_TIME | COUNTER
			byte[] bKey = Bytes.add(Bytes.toBytes(invTime), Bytes.toBytes(shardedIndex));
			bKey = Bytes.add(Bytes.toBytes(random), bKey);

			Key key = new Key();
			key.setKey(bKey);
			keys.add(key);
		}

		return keys;
	}
}
