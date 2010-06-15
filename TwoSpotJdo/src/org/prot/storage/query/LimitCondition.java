/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.storage.query;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class LimitCondition implements Serializable
{
	private static final long serialVersionUID = 1937473360608241627L;

	private static final Logger logger = Logger.getLogger(LimitCondition.class);

	// Limits the number of index rows to fetch
	public static final long MAX_FETCH_INDEX_ROWS = 300;

	// Limits the number of entities to fetch
	public static final long MAX_FETCH_ENTITIES = 300;

	// Limits the number of fetch operations (every fetch is counted)
	private static final long MAX_FETCH_OPERATIONS = 300000;

	private long fetchOperationCounter = 0;

	private long resultCounter = 0;

	private long indexCounter = 0;

	private boolean unique = false;

	private Long count;

	private Long countOffset;

	private final boolean incrementOperation()
	{
		fetchOperationCounter++;

		boolean inRange = true;
		inRange &= fetchOperationCounter < MAX_FETCH_OPERATIONS;

		return inRange;
	}

	public final void resetIndexCounter()
	{
		indexCounter = 0;
	}

	public final boolean incrementIndex()
	{
		indexCounter++;

		boolean inRange = true;
		inRange &= incrementOperation();
		inRange &= indexCounter < MAX_FETCH_INDEX_ROWS;

		return inRange;
	}

	public final boolean incrementResult()
	{
		resultCounter++;

		boolean inRange = true;
		if (unique && resultCounter > 1)
			inRange &= false;

		if (count != null && resultCounter > count)
			inRange &= false;

		inRange &= incrementOperation();
		inRange &= resultCounter < MAX_FETCH_OPERATIONS;

		return inRange;
	}

	public void setCount(long count)
	{
		this.count = count;
	}

	public void setOffset(long offset)
	{
		this.countOffset = offset;
	}

	public void setUnique(boolean unique)
	{
		this.unique = unique;
	}
}
