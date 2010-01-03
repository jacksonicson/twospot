package org.prot.storage.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;

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

	public static Object deserialize(ClassLoaderResolver clr, byte[] data) throws IOException,
			ClassNotFoundException
	{
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		MyStream s = new MyStream(in, clr);

		logger.warn("Classloader: " + StorageUtils.class.getClassLoader());

		Object obj = s.readObject();

		return obj;
	}

	public static byte[] serialize(Object obj) throws IOException
	{
		// WARN: Imposssible within the server - class is not in classpath
		ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(arrayOut);
		out.writeObject(obj);

		return arrayOut.toByteArray();
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
}
