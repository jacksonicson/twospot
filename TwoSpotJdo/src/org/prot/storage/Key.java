package org.prot.storage;

import java.io.Serializable;

import org.apache.hadoop.hbase.util.Base64;
import org.apache.log4j.Logger;

public class Key implements Serializable
{
	private static final long serialVersionUID = -1077956197302875365L;

	private static final Logger logger = Logger.getLogger(Key.class);
	
	byte[] key = null;

	public Key()
	{
		// Empty constructor is required
	}
	
	public Key(String stringKey)
	{
		logger.debug("Restoring key from base64 string: " + stringKey);
		this.key = Base64.decode(stringKey);
	}

	public byte[] getKey()
	{
		return key;
	}

	public void setKey(byte[] key)
	{
		this.key = key;
	}

	public boolean equals(Object obj)
	{
		// Check if its this object
		if (obj == this)
			return true;

		// Check if its the correct instance
		if (!(obj instanceof Key))
			return false;

		// Compare the byte array
		Key key = (Key) obj;
		return key.equals(key.getKey());
	}

	public int hashCode()
	{
		if (key == null)
			return 0;

		return key.hashCode();
	}

	public String toString()
	{
		if (key == null)
			return "";

		// Do a base64 encoding
		return Base64.encodeBytes(key);
	}
}
