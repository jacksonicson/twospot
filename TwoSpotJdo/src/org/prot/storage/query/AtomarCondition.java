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

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import org.apache.log4j.Logger;

public class AtomarCondition implements Serializable
{
	private static final long serialVersionUID = 2659834747381329323L;

	private static final Logger logger = Logger.getLogger(AtomarCondition.class);

	// Condition type (equals, lower than, greater than, greater)
	private ConditionType type;

	// Property (left side)
	private AtomLiteral property;

	// Value to check (right side)
	private AtomLiteral value;

	public AtomarCondition(ConditionType type, AtomLiteral property, AtomLiteral value)
	{
		this.type = type;
		this.property = property;
		this.value = value;
	}

	void run(QueryHandler handler, StorageQuery query, Collection<byte[]> result, LimitCondition limit)
			throws IOException
	{
		handler.execute(result, query, this);
	}

	public ConditionType getType()
	{
		return type;
	}

	public AtomLiteral getProperty()
	{
		return property;
	}

	public AtomLiteral getValue()
	{
		return value;
	}
}
