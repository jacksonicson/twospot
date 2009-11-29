package org.prot.app.services.log;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
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
			HTableDescriptor desc = new HTableDescriptor(LOG_TABLE);
			desc.addFamily(new HColumnDescriptor("message"));
			admin.createTable(desc);
		}

		table = new HTable("logs");

		HTableDescriptor desc = table.getTableDescriptor();
		if (desc.hasFamily("message".getBytes()) == false)
		{
			admin.addColumn(LOG_TABLE, new HColumnDescriptor("message"));
		}

		logger.info("Table is online: " + table);
	}

	@Override
	public void writeLog(String appId, int priority, String message, String stack)
	{
		Put put = new Put(new Long(System.currentTimeMillis()).toString().getBytes());
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

	@Override
	public List<String> getLog(String appId)
	{
		final List<String> logs = new ArrayList<String>();

		AccessController.doPrivileged(new PrivilegedAction<Object>()
		{
			@Override
			public Object run()
			{
				try
				{
					HTable htable = new HTable(LOG_TABLE);

					Scan scan = new Scan();
					scan.addFamily("message".getBytes());

					ResultScanner scanner = htable.getScanner(scan);
					Iterator<Result> it = scanner.iterator();

					for (Result result = it.next(); result != null; result = it.next())
					{
						logs.add(new String(result.getValue("message".getBytes())));
					}

				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}
		});

		return logs;
	}
}
