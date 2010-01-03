package org.prot.storage;

import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;

public class Key implements Serializable
{
	private static final long serialVersionUID = -1077956197302875365L;

	byte[] key = null;

	public Key()
	{
		// Empty constructor is required
	}

	public Key(String key)
	{
		this(key, false);
	}

	public Key(String key, boolean isCompact)
	{
		if (!isCompact)
		{
			this.key = Base64.decodeBase64(key.getBytes());
		} else
		{
			this.key = Base64.decodeBase64(key.getBytes());
		}
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

	public String toCompactString()
	{
		return new String(Base64.encodeBase64(this.key));
	}

	public String toString()
	{
		if (key == null)
			return "";

		// URL-Safe encoding
		return new String(Base64.encodeBase64(key, true));
	}
}
