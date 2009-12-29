package org.prot.stor.hbase.query.plan;

import java.util.List;

import org.prot.stor.hbase.HBaseManagedConnection;

public class LiteralParameter extends QueryStep
{
	private byte[] value;

	public LiteralParameter(byte[] value)
	{
		this.value = value;
	}

	@Override
	public void exeucte(HBaseManagedConnection connection, List<Object> candidates)
	{
		// Do nothing
	}

	public byte[] getValue()
	{
		return value;
	}
}
