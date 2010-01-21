package org.prot.stoarge.tools;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class SchemaTool
{
	public SchemaTool()
	{
		HBaseConfiguration config = new HBaseConfiguration();
		try
		{
			HBaseAdmin admin = new HBaseAdmin(config);
			SchemaCreator creator = new SchemaCreator(admin);
			creator.checkAndCreate();

		} catch (MasterNotRunningException e)
		{
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String arg[])
	{
		new SchemaTool();
	}
}
