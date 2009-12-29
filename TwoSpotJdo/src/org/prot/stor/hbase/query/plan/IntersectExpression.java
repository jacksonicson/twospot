package org.prot.stor.hbase.query.plan;

public class IntersectExpression extends QueryStep
{
	private FetchExpression batchA;
	private FetchExpression batchB;

	public IntersectExpression(FetchExpression batchA, FetchExpression batchB)
	{
		this.batchA = batchA;
		this.batchB = batchB;
	}
}
