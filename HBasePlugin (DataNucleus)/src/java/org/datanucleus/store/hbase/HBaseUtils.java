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
package org.datanucleus.store.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.FetchPlan;
import org.datanucleus.ObjectManager;
import org.datanucleus.StateManager;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.FieldValues;

public class HBaseUtils
{
	public static final String NAMESPACE_USER_TAGBLES = "user";

	// Namespace for the tables (used as prefix)
	private static String namespace = NAMESPACE_USER_TAGBLES;

	// Use -1 for infinite field size
	private static long maxFieldSize = -1;

	// Max rows to fetch with a scanner
	private static long MAX_ROWS = 100;

	public static void setNamespace(String namespace)
	{
		HBaseUtils.namespace = namespace;
	}

	public static void setMaxFieldSize(long size)
	{
		maxFieldSize = size;
	}

	public static boolean checkFieldSize(long size)
	{
		if (maxFieldSize == -1)
			return true;

		return size <= maxFieldSize;
	}

	public static String getTableName(AbstractClassMetaData acmd)
	{
		// String appId = Configuration.getInstance().getAppId();
		String tableName = acmd.getTable();
		if (tableName == null)
			tableName = acmd.getName();

		return namespace + "." + tableName;
	}

	public static String getFamilyName(AbstractClassMetaData acmd, int absoluteFieldNumber)
	{
		AbstractMemberMetaData ammd = acmd.getMetaDataForManagedMemberAtAbsolutePosition(absoluteFieldNumber);
		String columnName = null;

		// Try the first column if specified
		ColumnMetaData[] colmds = ammd.getColumnMetaData();
		if (colmds != null && colmds.length > 0)
		{
			columnName = colmds[0].getName();
		}
		if (columnName == null)
		{
			// Fallback to the field/property name
			columnName = HBaseUtils.getTableName(acmd);
		} else if (columnName.indexOf(":") > -1)
		{
			columnName = columnName.substring(0, columnName.indexOf(":"));
		}
		return columnName;
	}

	public static String getQualifierName(AbstractClassMetaData acmd, int absoluteFieldNumber)
	{
		AbstractMemberMetaData ammd = acmd.getMetaDataForManagedMemberAtAbsolutePosition(absoluteFieldNumber);
		String columnName = null;

		// Try the first column if specified
		ColumnMetaData[] colmds = ammd.getColumnMetaData();
		if (colmds != null && colmds.length > 0)
		{
			columnName = colmds[0].getName();
		}
		if (columnName == null)
		{
			// Fallback to the field/property name
			columnName = ammd.getName();
		}
		if (columnName.indexOf(":") > -1)
		{
			columnName = columnName.substring(columnName.indexOf(":") + 1);
		}
		return columnName;
	}

	/**
	 * Convenience method to get all objects of the candidate type (and optional
	 * subclasses) from the specified XML connection.
	 * 
	 * @param om
	 *            ObjectManager
	 * @param mconn
	 *            Managed Connection
	 * @param candidateClass
	 *            Candidate
	 * @param subclasses
	 *            Include subclasses?
	 * @param ignoreCache
	 *            Whether to ignore the cache
	 * @return List of objects of the candidate type (or subclass)
	 */
	public static List getObjectsOfCandidateType(final ObjectManager om, HBaseManagedConnection mconn,
			Class candidateClass, boolean subclasses, boolean ignoreCache)
	{
		List results = new ArrayList();
		try
		{
			final ClassLoaderResolver clr = om.getClassLoaderResolver();
			final AbstractClassMetaData acmd = om.getMetaDataManager().getMetaDataForClass(candidateClass,
					clr);

			HTable table = mconn.getHTable(HBaseUtils.getTableName(acmd));

			Scan scan = new Scan();
			int[] fieldNumbers = acmd.getAllMemberPositions();
			for (int i = 0; i < fieldNumbers.length; i++)
			{
				byte[] familyNames = HBaseUtils.getFamilyName(acmd, fieldNumbers[i]).getBytes();
				byte[] columnNames = HBaseUtils.getQualifierName(acmd, fieldNumbers[i]).getBytes();
				scan.addColumn(familyNames, columnNames);
			}
			ResultScanner scanner = table.getScanner(scan);
			Iterator<Result> it = scanner.iterator();

			int counter = 0;
			while (it.hasNext() && counter++ < MAX_ROWS)
			{
				final Result result = it.next();
				results.add(om.findObjectUsingAID(clr.classForName(acmd.getFullClassName()),
						new FieldValues()
						{
							// StateManager calls the fetchFields method
							public void fetchFields(StateManager sm)
							{
								sm.replaceFields(acmd.getPKMemberPositions(), new HBaseFetchFieldManager(sm,
										result));
								sm.replaceFields(acmd.getBasicMemberPositions(clr, om.getMetaDataManager()),
										new HBaseFetchFieldManager(sm, result));
							}

							public void fetchNonLoadedFields(StateManager sm)
							{
								sm.replaceNonLoadedFields(acmd.getAllMemberPositions(),
										new HBaseFetchFieldManager(sm, result));
							}

							public FetchPlan getFetchPlanForLoading()
							{
								return null;
							}
						}, ignoreCache, true));

			}
		} catch (IOException e)
		{
			throw new NucleusDataStoreException(e.getMessage(), e);
		}
		return results;
	}

	public static void createSchema(HBaseConfiguration config, AbstractClassMetaData acmd,
			boolean autoCreateColumns) throws IOException
	{
		HBaseAdmin hBaseAdmin = new HBaseAdmin(config);
		HTableDescriptor hTable;
		String tableName = HBaseUtils.getTableName(acmd);
		try
		{
			hTable = hBaseAdmin.getTableDescriptor(tableName.getBytes());
		} catch (TableNotFoundException ex)
		{
			hTable = new HTableDescriptor(tableName);
			hBaseAdmin.createTable(hTable);
		}

		if (autoCreateColumns)
		{
			boolean modified = false;
			if (!hTable.hasFamily(HBaseUtils.getTableName(acmd).getBytes()))
			{
				HColumnDescriptor hColumn = new HColumnDescriptor(HBaseUtils.getTableName(acmd));
				hTable.addFamily(hColumn);
				modified = true;
			}
			int[] fieldNumbers = acmd.getAllMemberPositions();
			for (int i = 0; i < fieldNumbers.length; i++)
			{
				String familyName = getFamilyName(acmd, fieldNumbers[i]);
				if (!hTable.hasFamily(familyName.getBytes()))
				{
					HColumnDescriptor hColumn = new HColumnDescriptor(familyName);
					hTable.addFamily(hColumn);
					modified = true;
				}
			}
			if (modified)
			{
				hBaseAdmin.disableTable(hTable.getName());
				hBaseAdmin.modifyTable(hTable.getName(), hTable);
				hBaseAdmin.enableTable(hTable.getName());
			}
		}
	}
}