package org.prot.storage;

import org.apache.hadoop.hbase.util.Bytes;
import org.prot.storage.connection.StorageUtils;

public class KeyHelper
{
	public static final byte[] createRowKey(String appId, String kind, Key key)
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
