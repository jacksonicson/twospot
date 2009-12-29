/**********************************************************************
Copyright (c) 2009 Erik Bengtson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
    ...
 ***********************************************************************/
package org.prot.stor.hbase.query;

import java.util.Map;

import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ObjectManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.query.AbstractJDOQLQuery;
import org.prot.stor.hbase.HBaseManagedConnection;
import org.prot.stor.hbase.query.plan.QueryPlan;

/**
 * Implementation of JDOQL for HBase datastores.
 */
public class JDOQLQuery extends AbstractJDOQLQuery
{
	private static final Logger logger = Logger.getLogger(JDOQLQuery.class);

	private QueryPlan queryPlan;

	public JDOQLQuery(ObjectManager om)
	{
		this(om, (JDOQLQuery) null);
	}

	public JDOQLQuery(ObjectManager om, JDOQLQuery q)
	{
		super(om, q);
	}

	public JDOQLQuery(ObjectManager om, String query)
	{
		super(om, query);
	}

	protected boolean isCompiled()
	{
		return super.isCompiled();
	}

	protected synchronized void compileInternal(boolean forExecute, Map parameterValues)
	{
		if (isCompiled())
		{
			logger.debug("Query is compiled");
			return;
		}

		// Compile the generic query expressions
		super.compileInternal(forExecute, parameterValues);

		ClassLoaderResolver clr = om.getClassLoaderResolver();
		AbstractClassMetaData acmd = getObjectManager().getMetaDataManager().getMetaDataForClass(
				candidateClass, clr);

		// Check the query type
		switch (type)
		{
		case SELECT:
			logger.debug("Compiling SELECT query");

			// Create a query plan out of the compiled query
			QueryPlan queryPlan = compileQueryFull(parameterValues, acmd);
			this.queryPlan = queryPlan;

			return;

		case BULK_UPDATE:
			throw new NucleusException("Bulk updates are not supported");

		case BULK_DELETE:
			throw new NucleusException("Bulk deletes are not supported");

		default:
			throw new NucleusException("Unsupported query type");
		}
	}

	private QueryPlan compileQueryFull(Map parameters, AbstractClassMetaData candidateCmd)
	{
		QueryPlan plan = new QueryPlan();

		QueryToHBaseMapper mapper = new QueryToHBaseMapper(plan, compilation, parameters, candidateCmd,
				getFetchPlan(), om);
		mapper.compile();

		return plan;
	}

	protected Object performExecute(Map parameters)
	{
		for (Object key : parameters.keySet())
		{
			logger.debug("parameter: " + key + " - " + parameters.get(key));
		}

		logger.debug("Perform execute");

		ManagedConnection mconn = om.getStoreManager().getConnection(om);
		HBaseManagedConnection hbaseConnection = (HBaseManagedConnection) mconn;
		if (this.queryPlan == null)
		{
			logger.error("Query plan is null");
			return null;
		} else
		{
			return queryPlan.execute(hbaseConnection);
		}
	}
}