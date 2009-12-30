package org.prot.stor.hbase.query.plan;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.stor.hbase.HBaseManagedConnection;

public class IntersectExpression extends QueryStep
{
	private static final Logger logger = Logger.getLogger(IntersectExpression.class);

	private FetchExpression batchA;
	private FetchExpression batchB;

	public IntersectExpression(FetchExpression batchA, FetchExpression batchB)
	{
		this.batchA = batchA;
		this.batchB = batchB;
	}

	public FetchExpression getBatchA()
	{
		return batchA;
	}

	public FetchExpression getBatchB()
	{
		return batchB;
	}

	@Override
	public void exeucte(HBaseManagedConnection connection, List<Object> candidates)
	{
		// Create a intersect of both results!
		// Warng: This produces a lot of queries and memory consumtion and may
		// be blocked!
	}
}
