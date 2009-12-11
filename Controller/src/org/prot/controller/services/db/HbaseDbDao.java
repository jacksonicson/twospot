package org.prot.controller.services.db;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.log4j.Logger;

class HbaseDbDao implements DbDao
{
	private static final Logger logger = Logger.getLogger(HbaseDbDao.class);
	
	private String toHex(byte[] data)
	{
		String hex = "";
		for (int i = 0; i < data.length; i++)
		{
			hex += " " + Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1);
		}
		return hex;
	}

	private String deserialize(byte[] data) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		ObjectInputStream oin;
		bin.mark(0);

		try
		{
			bin.reset();
			oin = new ObjectInputStream(bin);
			Object o = oin.readObject();
			if (o instanceof String == true)
				return "" + o.toString();
		} catch (Exception e)
		{
		}

		try
		{
			bin.reset();
			oin = new ObjectInputStream(bin);
			return "" + oin.readDouble();
		} catch (Exception e)
		{
		}

		try
		{
			bin.reset();
			oin = new ObjectInputStream(bin);
			return "" + oin.readFloat();
		} catch (Exception e)
		{
		}

		try
		{
			bin.reset();
			oin = new ObjectInputStream(bin);
			return "" + oin.readLong();
		} catch (Exception e)
		{
		}

		try
		{
			bin.reset();
			oin = new ObjectInputStream(bin);
			return "" + oin.readShort();
		} catch (Exception e)
		{
		}

		try
		{
			bin.reset();
			oin = new ObjectInputStream(bin);
			return "" + oin.readChar();
		} catch (Exception e)
		{
		}

		try
		{
			bin.reset();
			oin = new ObjectInputStream(bin);
			return "" + oin.readBoolean();
		} catch (Exception e)
		{
		}

		return toHex(data);
	}

	@Override
	public DataTablet getTableData(final String tableName)
	{
		final DataTablet tablet = new DataTablet();
		
		// TODO: The Hbase-API should do this in critical sections ... but it
		// doesn't
		// See also: http://www.jpox.org/servlet/jira/browse/NUCHBASE-12
		AccessController.doPrivileged(new PrivilegedAction<Object>()
		{
			@Override
			public Object run()
			{
				try
				{
					HTable htable = new HTable(tableName.getBytes());

					byte[] famkey = htable.getTableDescriptor().getFamiliesKeys().iterator().next();
					Scan scan = new Scan();
					scan.addFamily(famkey);

					ResultScanner scanner = htable.getScanner(scan);
					Iterator<Result> it = scanner.iterator();

					for (Result result = it.next(); result != null; result = it.next())
					{
						NavigableMap<byte[], byte[]> map = result.getFamilyMap(famkey);
						for (byte[] column : map.keySet())
						{
							tablet.put(new String(column), deserialize(map.get(column)));
						}
						
						tablet.nextRow();
					}

				} catch (MasterNotRunningException e)
				{
					logger.error(e);
				} catch (IOException e)
				{
					logger.error(e);
				} catch (ClassNotFoundException e)
				{
					logger.error(e);
				}

				return null;
			}
		});

		return tablet;
	}

	@Override
	public List<String> getTables(final String appId)
	{
		// TODO: The Hbase-API should do this in critical sections ... but it
		// doesn't
		// See also: http://www.jpox.org/servlet/jira/browse/NUCHBASE-12
		List<String> names = AccessController.doPrivileged(new PrivilegedAction<List<String>>()
		{
			@Override
			public List<String> run()
			{
				try
				{
					HBaseConfiguration config = new HBaseConfiguration();
					ArrayList<String> names = new ArrayList<String>();

					// Create a connection to the hbase
					HBaseAdmin admin = new HBaseAdmin(config);
					
					// List all tables
					HTableDescriptor[] list = admin.listTables();

					// Iterate over table names
					for (HTableDescriptor descriptor : list)
					{
						// Get the table name
						String name = new String(descriptor.getName());

						logger.debug("Checking name: " + name);
						
						// Check if table name starts with the AppId
						if (name.startsWith(appId))
						{
							// Check if its a user table
							if (name.indexOf(".user.") != -1)
							{
								logger.debug("DbBrowser found table name: " + name);
								names.add(name);
							}
						}
					}

					return names;

				} catch (MasterNotRunningException e)
				{
					logger.error("error", e);
				} catch (IOException e)
				{
					logger.error("error", e);
				}

				return null;
			}
		});

		return names;
	}

}
