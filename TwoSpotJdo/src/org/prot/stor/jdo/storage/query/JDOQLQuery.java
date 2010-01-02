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
package org.prot.stor.jdo.storage.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.FetchPlan;
import org.datanucleus.ObjectManager;
import org.datanucleus.StateManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.FieldValues;
import org.datanucleus.store.query.AbstractJDOQLQuery;
import org.prot.jdo.storage.FetchFieldManager;
import org.prot.jdo.storage.StorageHelper;
import org.prot.jdo.storage.StorageManagedConnection;
import org.prot.storage.Storage;
import org.prot.storage.query.StorageQuery;

import com.google.protobuf.CodedInputStream;

/**
 * Implementation of JDOQL for HBase datastores.
 */
public class JDOQLQuery extends AbstractJDOQLQuery
{
	private static final Logger logger = Logger.getLogger(JDOQLQuery.class);

	private StorageQuery storageQuery;

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

			// Create a new storage query
			StorageQuery storageQuery = compileQueryFull(parameterValues, acmd);
			this.storageQuery = storageQuery;

			return;

		case BULK_UPDATE:
			throw new NucleusException("Bulk updates are not supported");

		case BULK_DELETE:
			throw new NucleusException("Bulk deletes are not supported");

		default:
			throw new NucleusException("Unsupported query type");
		}
	}

	private StorageQuery compileQueryFull(Map parameters, AbstractClassMetaData acmd)
	{
		StorageQuery storageQuery = new StorageQuery(StorageHelper.APP_ID, candidateClass.getSimpleName());
		QueryToStorageMapper mapper = new QueryToStorageMapper(storageQuery, compilation, parameters, acmd,
				getFetchPlan(), om);

		mapper.compile();

		return storageQuery;
	}

	protected Object performExecute(Map parameters)
	{
		logger.debug("Executing query");

		StorageManagedConnection connection = (StorageManagedConnection) om.getStoreManager().getConnection(
				om);
		Storage storage = connection.getStorage();
		List<byte[]> list = storage.query(this.storageQuery);

		List<Object> candidates = new ArrayList<Object>();

		// Assign StateManagers to any returned objects
		ClassLoaderResolver clr = om.getClassLoaderResolver();
		Iterator<byte[]> iter = list.iterator();
		while (iter.hasNext())
		{

			final byte[] data = iter.next();
			Object obj = null;
			CodedInputStream in = CodedInputStream.newInstance(data);
			try
			{
				final ClassLoaderResolver clr2 = clr;
				final FetchFieldManager manager = new FetchFieldManager(in, clr, om);
				Class cls = clr.classForName(manager.getClassName());
				final AbstractClassMetaData acmd = om.getMetaDataManager().getMetaDataForClass(cls, clr2);
				obj = om.findObjectUsingAID(cls, new FieldValues()
				{

					@Override
					public void fetchFields(StateManager sm)
					{
						sm.replaceFields(acmd.getPKMemberPositions(), manager);
						sm
								.replaceFields(acmd.getBasicMemberPositions(clr2, om.getMetaDataManager()),
										manager);
					}

					@Override
					public void fetchNonLoadedFields(StateManager sm)
					{
					}

					@Override
					public FetchPlan getFetchPlanForLoading()
					{
						return null;
					}

				}, true, true);

				// obj = om.findObjectUsingAID(arg0, arg1, arg2, arg3)
				//				
				//				
				// obj = manager.get();
			} catch (IOException e)
			{
				e.printStackTrace();
				continue;
			}

			if (obj == null)
				continue;

			candidates.add(obj);
		}
		return candidates;
	}
}