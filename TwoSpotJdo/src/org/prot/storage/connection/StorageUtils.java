package org.prot.storage.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.hadoop.hbase.util.Bytes;
import org.prot.storage.Key;

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

	public static byte[] createRowKey(String appId, String kind, Key key)
	{
		assert (appId.length() < 20);
		int diff = 20 - appId.length();
		byte[] bAppId = appId.getBytes();
		byte[] bDiff = new byte[diff];
		bAppId = Bytes.add(bAppId, bDiff);

		assert (kind.length() < 20);
		diff = 20 - kind.length();
		byte[] bKind = kind.getBytes();
		bDiff = new byte[diff];
		bKind = Bytes.add(bKind, bDiff);

		byte[] bKey = key.getKey();

		return Bytes.add(Bytes.add(bAppId, bKind), bKey);
	}
	
	public static byte[] incrementByteArray(byte[] input)
	{
		boolean match = false;
		for (int i = input.length - 1; i >= 0; i--)
		{
			if (input[i] == 0xFF)
				continue;
			else
			{
				input[i]++;
				match = true;
				break;
			}
		}
		if (!match)
			input = Bytes.add(input, new byte[] { 0x00 });

		return input;
	}

	public static byte[] getArrayOfOnes()
	{
		byte[] ones = new byte[1024];
		for (int i = 0; i < ones.length; i++)
			ones[i] = (byte) 0xFF;

		return ones;
	}

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
