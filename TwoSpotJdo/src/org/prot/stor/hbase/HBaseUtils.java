/**********************************************************************
Copyright (c) 2009 Erik Bengtson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
    ...
 ***********************************************************************/
package org.prot.stor.hbase;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.datanucleus.metadata.AbstractClassMetaData;

public class HBaseUtils
{
	private static final String ENTITY_TABLE = "entities";

	private static String APP_ID = "appId";

	public static String getRowKey(AbstractClassMetaData acmd)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("/");
		buffer.append(APP_ID);
		buffer.append("/");
		buffer.append(acmd.getName());
		buffer.append(":");
		buffer.append("NULL");

		return buffer.toString();
	}

	public static String getTableName(AbstractClassMetaData acmd)
	{
		return ENTITY_TABLE;
	}
	
	public static void createSchema(HBaseConfiguration config, AbstractClassMetaData acmd,
			boolean autoCreateColumns) throws IOException
	{
		
		System.out.println("creating schema");
		
		HBaseAdmin hBaseAdmin = new HBaseAdmin(config);
		HTableDescriptor hTable;
		String tableName = ENTITY_TABLE;

		try
		{
			// Check if the table exists
			hTable = hBaseAdmin.getTableDescriptor(tableName.getBytes());
		} catch (TableNotFoundException ex)
		{
			// Table does not exist - so create a new one
			hTable = new HTableDescriptor(tableName);
			hBaseAdmin.createTable(hTable);
		}

		// Check if the table contains the column family
		boolean modified = false;
		if (!hTable.hasFamily("entity".getBytes()))
		{
			HColumnDescriptor hColumn = new HColumnDescriptor("entity");
			hTable.addFamily(hColumn);
			modified = true;
		}

		if (modified)
		{
			hBaseAdmin.disableTable(hTable.getName());
			hBaseAdmin.modifyTable(hTable.getName(), hTable);
			hBaseAdmin.enableTable(hTable.getName());
		}
	}
}