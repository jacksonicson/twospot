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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.Key;

/**
 * Query-Syntax (from google appengine: http://code.google.com/appengine/docs
 * /python/datastore/gqlreference.html):
 */
public class StorageQuery implements Serializable
{
	private static final long serialVersionUID = 3402635680542012919L;

	private static final Logger logger = Logger.getLogger(StorageQuery.class);

	// AppId to query
	private String appId;

	// Kind of the entity to query
	private String kind;

	// Key of the entity to query
	private Key key;

	// The select condition (could be empty)
	private SelectCondition condition = new SelectCondition();

	// Limit condition which counts the number of fetch operations
	private LimitCondition limit = new LimitCondition();

	public StorageQuery(String appId, String kind)
	{
		this(appId, kind, null);
	}

	public StorageQuery(String appId, Key key)
	{
		this(appId, null, key);
	}

	public StorageQuery(String appId, String kind, Key key)
	{
		this.appId = appId;
		this.key = key;
		this.kind = kind;
	}

	public SelectCondition getCondition()
	{
		return this.condition;
	}

	List<byte[]> run(QueryHandler handler) throws IOException
	{
		// List which contains all results
		List<byte[]> result = new ArrayList<byte[]>();

		if (!condition.isEmpty())
		{
			condition.run(handler, this, result, limit);
		} else
		{
			handler.execute(result, this);
		}

		return result;
	}

	public LimitCondition getLimit()
	{
		return this.limit;
	}

	public void setKey(Key key)
	{
		this.key = key;
	}

	public void setKind(String kind)
	{
		this.kind = kind;
	}

	public Key getKey()
	{
		return key;
	}

	public String getKind()
	{
		return kind;
	}

	public String getAppId()
	{
		return appId;
	}
}
