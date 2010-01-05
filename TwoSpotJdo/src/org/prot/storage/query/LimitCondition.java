package org.prot.storage.query;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.prot.storage.Key;

public class LimitCondition implements Serializable
{
	private static final long serialVersionUID = 1937473360608241627L;

	private static final Logger logger = Logger.getLogger(LimitCondition.class);

	// Limits the number of index rows to fetch
	public static final long MAX_FETCH_INDEX_ROWS = 300;

	// Limits the number of entities to fetch
	public static final long MAX_FETCH_ENTITIES = 300;

	// Limits the number of fetch operations (every fetch is counted)
	private static final long MAX_FETCH_OPERATIONS = 3000;

	private long fetchOperationCounter = 0;

	private long resultCounter = 0;

	private long indexCounter = 0;

	private boolean unique = false;

	private Long count;

	private Long countOffset;

	private Key keyOffset;

	private final boolean incrementOperation()
	{
		fetchOperationCounter++;

		boolean inRange = true;
		inRange &= fetchOperationCounter < MAX_FETCH_OPERATIONS;

		return inRange;
	}

	public final boolean incrementIndex()
	{
		indexCounter++;

		boolean inRange = true;
		inRange &= incrementOperation();
		inRange &= indexCounter < MAX_FETCH_INDEX_ROWS;

		if (!inRange)
			logger.warn("Too many indices fetched" + indexCounter);

		return inRange;
	}

	public final boolean incrementResult()
	{
		resultCounter++;

		boolean inRange = true;
		if (unique && resultCounter > 0)
			inRange &= false;

		inRange &= incrementOperation();
		inRange &= resultCounter < MAX_FETCH_OPERATIONS;

		if (!inRange)
			logger.warn("Too many results fetched: " + resultCounter);

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
