package org.prot.storage.connection;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

public class StorageUtils
{
	private static final Logger logger = Logger.getLogger(StorageUtils.class);

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

	public static final void materialize(HTable entityTable, Set<byte[]> keys, List<byte[]> result)
			throws IOException
	{
		logger.trace("Materializing entities");

		for (byte[] key : keys)
		{
			Get get = new Get(key);
			Result entity = entityTable.get(get);
			if (entity.getMap() == null)
			{
				logger.warn("Could not fetch the entity");
				continue;
			}

			byte[] data = entity.getMap().get(StorageUtils.bEntity).get(StorageUtils.bSerialized)
					.firstEntry().getValue();

			result.add(data);
		}
	}
}
