package org.prot.stor.hbase.query.plan;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.stor.hbase.HBaseManagedConnection;

public class LiteralParameter extends QueryStep
{
	private static final Logger logger = Logger.getLogger(LiteralParameter.class);

	private byte[] value;

	public LiteralParameter(byte[] value)
	{
		this.value = value;
	}

	@Override
	public void exeucte(HBaseManagedConnection connection, List<Object> candidates)
	{

	}
}
