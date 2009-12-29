package org.prot.stor.hbase.query.plan;

public class FetchExpression extends QueryStep
{
	private FetchType type;

	private LiteralParameter left;
	private LiteralParameter right;

	public FetchExpression(FetchType type, LiteralParameter left, LiteralParameter right)
	{
		this.type = type;
		this.left = left;
		this.right = right;
	}
}
