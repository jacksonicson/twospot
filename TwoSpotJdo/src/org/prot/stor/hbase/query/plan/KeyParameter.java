package org.prot.stor.hbase.query.plan;

public class KeyParameter extends LiteralParameter
{
	public KeyParameter(byte[] key)
	{
		super(key);
	}
}
