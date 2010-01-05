package org.prot.jdo.storage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.OMFContext;
import org.datanucleus.ObjectManager;
import org.datanucleus.PersistenceConfiguration;
import org.datanucleus.metadata.MetaDataListener;
import org.datanucleus.store.AbstractStoreManager;
import org.datanucleus.store.NucleusConnection;

public class StorageStoreManager extends AbstractStoreManager
{
	MetaDataListener metadataListener;

	/**
	 * Constructor.
	 * 
	 * @param clr
	 *            ClassLoader resolver
	 * @param omfContext
	 *            ObjectManagerFactory context
	 */
	public StorageStoreManager(ClassLoaderResolver clr, OMFContext omfContext)
	{
		super("hbase", clr, omfContext);

		// Handler for metadata
		metadataListener = new StorageMetaDataListener();
		omfContext.getMetaDataManager().registerListener(metadataListener);

		// Handler for persistence process
		persistenceHandler = new StoragePersistenceHandler(this);

		// Check the configuration
		PersistenceConfiguration conf = omfContext.getPersistenceConfiguration();
		if (!conf.getBooleanProperty("twospot.devserver"))
			StorageHelper.setDevMode(true);
		else
			StorageHelper.setDevMode(conf.getBooleanProperty("twospot.devserver"));

		logConfiguration();
	}

	protected void registerConnectionMgr()
	{
		super.registerConnectionMgr();
		this.connectionMgr.disableConnectionPool();
	}

	/**
	 * Release of resources
	 */
	public void close()
	{
		omfContext.getMetaDataManager().deregisterListener(metadataListener);
		super.close();
	}

	public NucleusConnection getNucleusConnection(ObjectManager om)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Accessor for the supported options in string form
	 */
	public Collection<String> getSupportedOptions()
	{
		Set<String> set = new HashSet<String>();
		set.add("ApplicationIdentity");
		set.add("TransactionIsolationLevel.read-committed");
		return set;
	}
}
