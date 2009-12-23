package org.prot.stor.hbase;

import java.io.Serializable;

public class Key implements Serializable
{
	private static final long serialVersionUID = -1077956197302875365L;

	byte[] key = null;

	public Key()
	{

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
		if (obj == this)
			return true;
		if (!(obj instanceof Key))
			return false;

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

		return new String(key);
	}
}
