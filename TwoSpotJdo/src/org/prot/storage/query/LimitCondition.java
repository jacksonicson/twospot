package org.prot.storage.query;

import java.io.Serializable;

import org.prot.stor.hbase.Key;

public class LimitCondition implements Serializable
{
	private static final long serialVersionUID = 1937473360608241627L;

	private long count;

	private Long countOffset;

	private Key keyOffset;

	public void setCount(long count)
	{
		this.count = count;
	}

	public void setOffset(long offset)
	{
		this.countOffset = offset;
		this.keyOffset = null;
	}

	public void setOffset(Key offset)
	{
		this.keyOffset = offset;
		this.countOffset = null;
	}
}
