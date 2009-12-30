package org.prot.storage.query;

import java.io.Serializable;

import org.prot.storage.Key;

public class LimitCondition implements Serializable
{
	private static final long serialVersionUID = 1937473360608241627L;

	private long resultCount = 0;

	private boolean unique = false;

	private Long count;

	private Long countOffset;

	private Key keyOffset;

	public void increment()
	{
		resultCount++;
	}

	public boolean isInRange()
	{
		if (unique && resultCount > 0)
			return false;

		if(count != null)
			return resultCount < count;
		
		return true; 
	}

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

	public void setUnique(boolean unique)
	{
		this.unique = unique;
	}
}
