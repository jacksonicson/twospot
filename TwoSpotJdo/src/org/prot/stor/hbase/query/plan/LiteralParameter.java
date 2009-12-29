package org.prot.stor.hbase.query.plan;

public class LiteralParameter extends QueryStep
{
	private byte[] value;

	public LiteralParameter(byte[] value)
	{
		this.value = value;
	}
}
