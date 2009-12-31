package org.prot.storage.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.hadoop.hbase.util.Bytes;

public class StorageUtils
{
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

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException
	{
		// WARN: Impossible within the server - class is not in classpath
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
		Object obj = in.readObject();
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

}
