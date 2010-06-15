/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.storage.connection;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.storage.TooMuchDataException;
import org.prot.storage.query.LimitCondition;

public class StorageUtils
{
	private static final Logger logger = Logger.getLogger(StorageUtils.class);

	private static final long MAX_FIELD_SIZE = 1024 * 1024 * 2; // 2MB

	public static final String TABLE_SEQUENCES = "counters";
	public static final String TABLE_ENTITIES = "entities";
	public static final String TABLE_INDEX_BY_KIND = "indexByKind";
	public static final String TABLE_INDEX_BY_PROPERTY_DESC = "indexByPropertyDesc";
	public static final String TABLE_INDEX_BY_PROPERTY_ASC = "indexByPropertyAsc";
	public static final String TABLE_INDEX_CUSTOM = "indexCustom";

	public static final byte[] bSlash = Bytes.toBytes("/");
	public static final byte[] bKey = Bytes.toBytes("key");
	public static final byte[] bEntity = Bytes.toBytes("entity");
	public static final byte[] bSerialized = Bytes.toBytes("serialized");
	public static final byte[] bCounter = Bytes.toBytes("counter");

	public static final void assertFieldSize(long length)
	{
		if (length > MAX_FIELD_SIZE)
			throw new TooMuchDataException();
	}

	public static final HTable getTableEntity(HBaseManagedConnection connection)
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_ENTITIES);
		return table;
	}

	public static final HTable getTableIndexByPropertyAsc(HBaseManagedConnection connection)
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_INDEX_BY_PROPERTY_ASC);
		return table;
	}

	public static final HTable getTableIndexByKind(HBaseManagedConnection connection)
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_INDEX_BY_KIND);
		return table;
	}

	public static final HTable getSequenceTable(HBaseManagedConnection connection)
	{
		String tableName = StorageUtils.TABLE_SEQUENCES;
		return connection.getHTable(tableName);
	}

	public static final void materialize(HTable entityTable, List<byte[]> keys, Collection<byte[]> result,
			LimitCondition limit) throws IOException
	{
		for (byte[] key : keys)
		{
			// Check limits
			if (!limit.incrementResult())
				return;

			// Create a new get
			Get get = new Get(key);

			// Execute
			Result entity = entityTable.get(get);
			if (entity.getMap() == null)
			{
				logger.warn("Could not fetch the entity");
				continue;
			}

			// Add result
			byte[] data = entity.getMap().get(StorageUtils.bEntity).get(StorageUtils.bSerialized)
					.firstEntry().getValue();

			result.add(data);
		}
	}
}
