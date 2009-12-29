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
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.datanucleus.metadata.AbstractClassMetaData;

public class HBaseUtils
{
	private static final Logger logger = Logger.getLogger(HBaseUtils.class);

	public static final String ENTITY_TABLE = "entities";
	public static final String COUNTER_TABLE = "counters";
	public static final String INDEX_BY_KIND_TABLE = "indexKind";
	public static final String INDEX_BY_PROPERTY_TABLE = "indexProperty";

	public static String APP_ID = "null";

	public static void setAppId(String appId)
	{
		APP_ID = appId;
	}

	public static byte[] getRowKey(AbstractClassMetaData acmd, Key key)
	{
		String appId = APP_ID;
		assert (appId.length() < 20);
		int diff = 20 - appId.length();
		byte[] bAppId = appId.getBytes();
		byte[] bDiff = new byte[diff];
		bAppId = Bytes.add(bAppId, bDiff);

		String entityName = acmd.getName();
		assert (entityName.length() < 20);
		diff = 20 - entityName.length();
		byte[] bEntityName = entityName.getBytes();
		bDiff = new byte[diff];
		bEntityName = Bytes.add(bEntityName, bDiff);

		byte[] bKey = key.getKey();

		return Bytes.add(Bytes.add(bAppId, bEntityName), bKey);
	}

	public static String getTableName(AbstractClassMetaData acmd)
	{
		return ENTITY_TABLE;
	}

	public static void createSchema(HBaseConfiguration config, AbstractClassMetaData acmd,
			boolean autoCreateColumns) throws IOException
	{
		logger.debug("Creating schema");

		try
		{
			HBaseAdmin admin = new HBaseAdmin(config);
			checkTableEntities(admin);
			checkTableCounter(admin);
			checkTableIndexByKind(admin);
			checkTableIndexByProperty(admin);
		} catch (Exception e)
		{
			logger.error(e);
		}
	}

	private static void checkTableIndexByProperty(HBaseAdmin hBaseAdmin) throws Exception
	{
		logger.debug("Checking index by kind table");

		HTableDescriptor hTable;
		String tableName = INDEX_BY_PROPERTY_TABLE;

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

		// Check if the table contains the column family (key:key)
		boolean modified = false;
		if (!hTable.hasFamily("key".getBytes()))
		{
			HColumnDescriptor hColumn = new HColumnDescriptor("key");
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

	private static void checkTableIndexByKind(HBaseAdmin hBaseAdmin) throws Exception
	{
		logger.debug("Checking index by kind table");

		HTableDescriptor hTable;
		String tableName = INDEX_BY_KIND_TABLE;

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

		// Check if the table contains the column family (key:key)
		boolean modified = false;
		if (!hTable.hasFamily("key".getBytes()))
		{
			HColumnDescriptor hColumn = new HColumnDescriptor("key");
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

	private static void checkTableCounter(HBaseAdmin hBaseAdmin) throws Exception
	{
		logger.debug("Checking counters table");

		HTableDescriptor hTable;
		String tableName = COUNTER_TABLE;

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

		// Check if the table contains the column family (counter:counter)
		boolean modified = false;
		if (!hTable.hasFamily("counter".getBytes()))
		{
			HColumnDescriptor hColumn = new HColumnDescriptor("counter");
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

	private static void checkTableEntities(HBaseAdmin hBaseAdmin) throws Exception
	{
		logger.debug("Checking entities table");

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

		// Check if the table contains the column family (entity:entity)
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