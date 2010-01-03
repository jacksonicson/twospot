package org.prot.storage.query.error;

public class QueryException extends RuntimeException
{
	private static final long serialVersionUID = 1638643915946200196L;

	public QueryException(String msg)
	{
		super(msg);
	}
}
