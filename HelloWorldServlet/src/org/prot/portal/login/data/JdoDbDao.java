package org.prot.portal.login.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

public class JdoDbDao implements DbDao
{
	private static final Logger logger = Logger.getLogger(JdoDbDao.class);

	private String toHex(byte[] data)
	{
		String hex = "";
		for(int i=0; i<data.length; i++)
		{
			hex += " " + Integer.toString( ( data[i] & 0xff ) + 0x100, 16).substring( 1 );
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
			if(o instanceof String == true)
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
	public DataTablet getTableData(String tableName)
	{
		DataTablet tablet = new DataTablet();
		
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
				tablet.nextRow(); 
				
				NavigableMap<byte[], byte[]> map = result.getFamilyMap(famkey);
				for (byte[] column : map.keySet())
				{
					tablet.put(new String(column), deserialize(map.get(column)));
				}
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

		return tablet;
	}

	@Override
	public List<String> getTables(String username, String appId)
	{

		HBaseConfiguration config = new HBaseConfiguration();
		try
		{
			ArrayList<String> names = new ArrayList<String>();

			HBaseAdmin admin = new HBaseAdmin(config);
			HTableDescriptor[] list = admin.listTables();
			for (HTableDescriptor descriptor : list)
			{
				String name = new String(descriptor.getName());
				logger.debug("checking name: " + name);

				// Extract names with appId and the namespace
				if (name.startsWith(appId))
				{
					if (name.indexOf(".user") != -1)
					{
						names.add(name);

						logger.debug("adding name");
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

		return new ArrayList<String>();
	}

}
