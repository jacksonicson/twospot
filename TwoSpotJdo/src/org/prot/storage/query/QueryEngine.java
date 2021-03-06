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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.Key;
import org.prot.storage.query.error.QueryException;

public class QueryEngine
{
	private static final Logger logger = Logger.getLogger(QueryEngine.class);

	// private HBaseManagedConnection connection;
	private QueryHandler handler;

	public QueryEngine(QueryHandler handler)
	{
		this.handler = handler;
		// this.connection = connectionFactory.createManagedConnection();
	}

	public byte[] fetch(String appId, Key key)
	{
		StorageQuery query = new StorageQuery(appId, key);
		List<byte[]> result = run(query);
		if (result.size() == 1)
		{
			return result.get(0);
		} else if (result.size() > 1)
		{
			logger.fatal("Multiple objects for one key: " + key);
			throw new QueryException("Multiple objects for one key");
		}

		// Nothing found
		return null;
	}

	public List<byte[]> run(StorageQuery query)
	{
		try
		{
			return query.run(handler);
		} catch (IOException e)
		{
			logger.error("Error while running query", e);
		}

		return new ArrayList<byte[]>();
	}
}
