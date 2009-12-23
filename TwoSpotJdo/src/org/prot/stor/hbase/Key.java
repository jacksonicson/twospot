package org.prot.stor.hbase;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class Key implements Serializable
{
	@Persistent
	byte[] key = null;

	public void generate()
	{
		assert (key == null);
	}

	public byte[] getKey()
	{
		return key;
	}

	public void setKey(byte[] key)
	{
		this.key = key;
	}
}
