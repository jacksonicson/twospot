package org.prot.app.services.log;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.log4j.Logger;

public class HBaseLogDao implements LogDao
{
	private static final String LOG_TABLE = "logs";

	private static final Logger logger = Logger.getLogger(HBaseLogDao.class);

	private HTable table;

	public HBaseLogDao() throws IOException
	{
		HBaseConfiguration config = new HBaseConfiguration();
		HBaseAdmin admin = new HBaseAdmin(config);

		boolean exists = admin.tableExists(LOG_TABLE);
		if (exists == false)
		{
			HTableDescriptor desc = new HTableDescriptor();
			desc.addFamily(new HColumnDescriptor("message"));
			admin.createTable(desc);
		}

		table = new HTable("logs");
	}

	@Override
	public void writeLog(String appId, int priority, String message, String stack)
	{
		Put put = new Put();
		put.add("message".getBytes(), "appId".getBytes(), appId.getBytes());
		put.add("message".getBytes(), "message".getBytes(), message.getBytes());
		put.add("message".getBytes(), "stack".getBytes(), stack.getBytes());
		try
		{
			table.put(put);
		} catch (IOException e)
		{
			logger.error("Cannot write log table." + e);
		}
	}
}
