package org.prot.storage;

import org.apache.hadoop.hbase.util.Bytes;
import org.prot.storage.connection.StorageUtils;

public class KeyHelper
{
	private static final int MAX_PROPERTY_SIZE = 10 * 1024;

	public static final byte[] createRowKey(String appId, Key key)
	{
		// The key which is application wide unique
		byte[] bKey = key.getKey();

		// The appId as 20 byte string
		byte[] bAppId = Bytes.toBytes(appId);
		if (bAppId.length < 20)
			bAppId = Bytes.padTail(bAppId, 20 - bAppId.length);

		return Bytes.add(bKey, bAppId);
	}

	public static final byte[] createIndexByKindRowKey(String appId, String kind, byte[] rowKey)
	{
		byte[] indexRowKey = createIndexByKindKey(appId, kind);
		indexRowKey = Bytes.add(indexRowKey, StorageUtils.bSlash, rowKey);

		return indexRowKey;
	}

	public static final byte[] createIndexByKindKey(String appId, String kind)
	{
		// Construct the index key
		byte[] bAppId = Bytes.toBytes(appId);
		byte[] bKind = Bytes.toBytes(kind);

		byte[] indexRowKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);

		return indexRowKey;
	}

	public static final byte[] createIndexByPropertyKey(String appId, String kind, byte[] rowKey,
			String propertyName, byte[] value)
	{
		byte[] bAppId = Bytes.toBytes(appId);
		byte[] bKind = Bytes.toBytes(kind);
		byte[] bPropertyName = Bytes.toBytes(propertyName);

		if (value.length > MAX_PROPERTY_SIZE)
			value = Bytes.head(value, MAX_PROPERTY_SIZE);

		byte[] propKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);
		propKey = Bytes.add(propKey, StorageUtils.bSlash, bPropertyName);
		propKey = Bytes.add(propKey, StorageUtils.bSlash, value);
		propKey = Bytes.add(propKey, StorageUtils.bSlash, rowKey);

		return propKey;
	}

	public static byte[] getArrayOfOnes()
	{
		byte[] ones = new byte[1024];
		for (int i = 0; i < ones.length; i++)
			ones[i] = (byte) 0xFF;

		return ones;
	}
	
	public static byte[] getArrayOfZeros()
	{
		byte[] ones = new byte[1024];
		for (int i = 0; i < ones.length; i++)
			ones[i] = (byte) 0x00;

		return ones;
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
}
