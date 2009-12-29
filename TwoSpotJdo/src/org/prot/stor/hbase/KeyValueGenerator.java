package org.prot.stor.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.RowLock;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.datanucleus.store.valuegenerator.AbstractDatastoreGenerator;
import org.datanucleus.store.valuegenerator.ValueGenerationBlock;

public class KeyValueGenerator extends AbstractDatastoreGenerator
{
	private static final Logger logger = Logger.getLogger(KeyValueGenerator.class);

	public KeyValueGenerator(String name, Properties props)
	{
		super(name, props);
	}

	@Override
	protected ValueGenerationBlock reserveBlock(long size)
	{
		logger.debug("reserving block: " + size);
		List list = new ArrayList();

		HBaseManagedConnection connection = (HBaseManagedConnection) connectionProvider.retrieveConnection();
		HTable table = connection.getHTable(HBaseUtils.COUNTER_TABLE);

		RowLock lock;
		try
		{
			Get get = new Get(Bytes.toBytes(HBaseUtils.APP_ID));

			Result result = table.get(get);
			logger.info("Result found: " + result.size());

			byte[] counter = Bytes.toBytes("counter");

			Entry<Long, byte[]> data = result.getMap().get(counter).get(counter).lastEntry();
			System.out.println("Timestamp: " + data.getKey());
			byte[] value = data.getValue();

			long lValue = Bytes.toLong(value);
			logger.info("Counter is now: " + lValue);

			long newValue = lValue + size;

			System.out.println("IValue now: " + newValue);

			if (result != null)
			{
				logger.debug("Updating now");

				Put put = new Put(Bytes.toBytes(HBaseUtils.APP_ID));
				put.add(counter, counter, Bytes.toBytes(newValue));

				// table.put(put);
				table.checkAndPut(Bytes.toBytes(HBaseUtils.APP_ID), counter, counter, Bytes.toBytes(lValue),
						put);
			}

			long i = 0;
			do
			{
				Key key = new Key();
				key.setKey(Bytes.toBytes((long) (lValue + i)));
				list.add(key);

			} while (++i < size);

		} catch (IOException e)
		{
			logger.error(e);
		}

		return new ValueGenerationBlock(list);
	}

	protected boolean requiresRepository()
	{
		// Yes we require a repository
		return true;
	}

	protected boolean repositoryExists()
	{
		// Repository does not exist
		return false;
	}

	protected boolean createRepository()
	{
		HBaseManagedConnection connection = (HBaseManagedConnection) connectionProvider.retrieveConnection();
		HTable table = connection.getHTable(HBaseUtils.COUNTER_TABLE);

		try
		{
			Result result = table.get(new Get(Bytes.toBytes(HBaseUtils.APP_ID)));
			logger.info("Result found");

			if (result == null || result.size() == 0)
			{
				logger.info("Creating a new counter");
				Put put = new Put(Bytes.toBytes(HBaseUtils.APP_ID));
				byte[] counter = Bytes.toBytes("counter");
				put.add(counter, counter, Bytes.toBytes(0l));
				table.put(put);
			}

		} catch (IOException e)
		{
			logger.error(e);
		}

		return true;
	}
}
