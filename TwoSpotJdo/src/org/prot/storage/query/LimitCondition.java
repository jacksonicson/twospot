package org.prot.storage.query;

import java.io.Serializable;

import org.prot.storage.Key;

public class LimitCondition implements Serializable
{
	private static final long serialVersionUID = 1937473360608241627L;

	// Limits the number of index rows to fetch
	public static final long MAX_FETCH_INDEX_ROWS = 300;

	// Limits the number of entities to fetch
	public static final long MAX_FETCH_ENTITIES = 300;

	// Limits the number of fetch operations (every fetch is counted)
	private static final long MAX_FETCH_OPERATIONS = 3000;

	private long fetchOperationCounter = 0;

	private boolean unique = false;

	private Long count;

	private Long countOffset;

	private Key keyOffset;

	public void increment()
	{
		fetchOperationCounter++;
	}

	public boolean isInRange()
	{
		boolean inRange = true;
		inRange &= fetchOperationCounter < MAX_FETCH_OPERATIONS;

		if (unique && fetchOperationCounter > 0)
			inRange &= false;

		return inRange;
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
