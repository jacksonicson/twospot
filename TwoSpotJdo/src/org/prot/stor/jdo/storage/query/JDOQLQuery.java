package org.prot.stor.jdo.storage.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.FetchPlan;
import org.datanucleus.ObjectManager;
import org.datanucleus.StateManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.query.evaluator.JDOQLEvaluator;
import org.datanucleus.query.evaluator.JavaQueryEvaluator;
import org.datanucleus.store.FieldValues;
import org.datanucleus.store.query.AbstractJDOQLQuery;
import org.prot.jdo.storage.StorageHelper;
import org.prot.jdo.storage.StorageManagedConnection;
import org.prot.jdo.storage.field.FetchFieldManager;
import org.prot.storage.Storage;
import org.prot.storage.UnsupportedOperationException;
import org.prot.storage.query.StorageQuery;

import com.google.protobuf.CodedInputStream;

public class JDOQLQuery extends AbstractJDOQLQuery
{
	private static final long serialVersionUID = -5519841357863023031L;

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

	@SuppressWarnings("unchecked")
	protected synchronized void compileInternal(boolean forExecute, Map parameterValues)
	{
		if (isCompiled())
		{
			logger.debug("Query is already compiled");
			return;
		}

		// Compile the generic query expressions
		super.compileInternal(forExecute, parameterValues);

		// Get the cls and acmd for the class
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
			throw new UnsupportedOperationException("Build update ist not supported");

		case BULK_DELETE:
			throw new UnsupportedOperationException("Build delete ist not supported");

		default:
			throw new UnsupportedOperationException("Query type ist not supported");
		}
	}

	@SuppressWarnings("unchecked")
	private StorageQuery compileQueryFull(Map parameters, AbstractClassMetaData acmd)
	{
		// Create a new storage query
		StorageQuery storageQuery = new StorageQuery(StorageHelper.APP_ID, candidateClass.getSimpleName());

		// Create a new storage mapper
		QueryToStorageMapper mapper = new QueryToStorageMapper(storageQuery, compilation, parameters, acmd,
				getFetchPlan(), om);

		// Map the JDOQL-query to the storage query
		mapper.compile();

		return storageQuery;
	}

	@SuppressWarnings("unchecked")
	protected Object performExecute(Map parameters)
	{
		// Create a new connection to the storage
		StorageManagedConnection connection = (StorageManagedConnection) om.getStoreManager().getConnection(
				om);
		Storage storage = connection.getStorage();

		// Execute the query
		final List<byte[]> entities = storage.query(this.storageQuery);

		// List of all candidates
		final List<Object> candidates = new ArrayList<Object>();

		// Get the class loader resolver
		final ClassLoaderResolver clr = om.getClassLoaderResolver();

		// Get the ACMD for the candidate class
		final AbstractClassMetaData acmd = om.getMetaDataManager().getMetaDataForClass(candidateClass, clr);

		// Assign StateManagers to any returned objects
		for (Iterator<byte[]> itEntities = entities.iterator(); itEntities.hasNext();)
		{
			final byte[] entityData = itEntities.next();

			// Create a new coded input stream to deserialize the entity data
			CodedInputStream in = CodedInputStream.newInstance(entityData);

			// New fetch field manager to fill the entity instance
			final FetchFieldManager manager;
			try
			{
				manager = new FetchFieldManager(in, om, clr);
			} catch (IOException e)
			{
				logger.error("Could not fetch entity", e);
				continue;
			}

			// Get the candidate class (NOT USING THE CLASSNAME FROM THE ENTITY
			// MESSAGE HERE)
			Class candidateClass = clr.classForName(acmd.getFullClassName());

			// Create the candidate object (deserialize)
			Object candidate = om.findObjectUsingAID(candidateClass, new FieldValues()
			{
				@Override
				public void fetchFields(StateManager sm)
				{
					// Replace all primary key fields
					sm.replaceFields(acmd.getPKMemberPositions(), manager);

					// Replace all basic member fields
					int[] memberPositions = acmd.getBasicMemberPositions(clr, om.getMetaDataManager());
					sm.replaceFields(memberPositions, manager);
				}

				@Override
				public void fetchNonLoadedFields(StateManager sm)
				{
					// Replace non loaded fields
					sm.replaceNonLoadedFields(acmd.getAllMemberPositions(), manager);
				}

				@Override
				public FetchPlan getFetchPlanForLoading()
				{
					return null;
				}

			}, true, true);

			// Add the candidate to the candidates list
			candidates.add(candidate);
		}

		// Filter the candidates in memory
		JavaQueryEvaluator evaluator = new JDOQLEvaluator(this, candidates, compilation, parameters, om
				.getClassLoaderResolver());
		Collection results = evaluator.execute(true, true, true, true, true);

		return results;
	}
}