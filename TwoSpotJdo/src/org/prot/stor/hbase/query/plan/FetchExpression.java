package org.prot.stor.hbase.query.plan;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.stor.hbase.HBaseManagedConnection;

public class FetchExpression extends QueryStep
{
	private static final Logger logger = Logger.getLogger(FetchExpression.class);

	private FetchType type;

	private LiteralParameter left;
	private LiteralParameter right;

	public FetchExpression(FetchType type, LiteralParameter left, LiteralParameter right)
	{
		this.type = type;
		this.left = left;
		this.right = right;
	}

	@Override
	public void exeucte(HBaseManagedConnection connection, List<Object> candidates)
	{
		logger.debug("Executing fetch expression");
	}
}
