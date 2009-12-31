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
package org.prot.stor.hbase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.datanucleus.ObjectManager;
import org.datanucleus.StateManager;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.StorePersistenceHandler;
import org.datanucleus.util.Localiser;
import org.prot.storage.Key;
import org.prot.storage.Storage;

/**
 * Wichtigste Klasse. Hier werden die Objekte serialisiert und in der Datenbank
 * gespeichert. Außerdem müssen die Index-Tabellen hier aktualisiert werden.
 * 
 * @author Andreas Wolke
 * 
 */
public class HBasePersistenceHandler implements StorePersistenceHandler
{
	private static final Logger logger = Logger.getLogger(HBasePersistenceHandler.class);

	protected static final Localiser LOCALISER = Localiser.getInstance(
			"org.datanucleus.store.hbase.Localisation", HBaseStoreManager.class.getClassLoader());

	private HBaseStoreManager storeMgr;

	HBasePersistenceHandler(StoreManager storeMgr)
	{
		this.storeMgr = (HBaseStoreManager) storeMgr;
	}

	@Override
	public void close()
	{
		// Do nothing
	}

	public void deleteObject(StateManager sm)
	{
		// Cannot delete a read only object
		storeMgr.assertReadOnlyForUpdateOfObject(sm);

		StorageManagedConnection mconn = (StorageManagedConnection) storeMgr.getConnection(sm
				.getObjectManager());
		try
		{
			// Aquire object infos
			String appId = HBaseUtils.APP_ID;
			String kind = sm.getObject().getClass().getSimpleName();
			Key key = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);

			// Delete the object
			Storage storage = mconn.getStorage();
			storage.deleteObject(appId, kind, key);

		} finally
		{
			
		}
	}

	public void fetchObject(StateManager sm, int[] fieldNumbers)
	{
		logger.debug("FETCH OBJECT");
	}

	public Object findObject(ObjectManager om, Object id)
	{
		logger.debug("FIND OBJECT");
		return null;
	}

	public void insertObject(StateManager sm)
	{
		// Check if the storage manager manages the class
		if (!storeMgr.managesClass(sm.getClassMetaData().getFullClassName()))
		{
			storeMgr.addClass(sm.getClassMetaData().getFullClassName(), sm.getObjectManager()
					.getClassLoaderResolver());
		}

		// Get all object properties except the primary key
		String[] pks = sm.getClassMetaData().getPrimaryKeyMemberNames();
		Set<String> spks = new HashSet<String>();
		for (String s : pks)
			spks.add(s);

		Map<String, byte[]> index = new HashMap<String, byte[]>();

		try
		{
			for (String name : sm.getLoadedFieldNames())
			{
				if (spks.contains(name))
					continue;

				Object oobj = sm.getObject();
				char[] cName = name.toCharArray();
				cName[0] = Character.toUpperCase(cName[0]);
				Method method;
				method = oobj.getClass().getMethod("get" + new String(cName), new Class[0]);

				Object value = method.invoke(oobj, new Object[0]);
				byte[] bValue = null;
				if (value instanceof Integer)
					bValue = Bytes.toBytes((Integer) value);
				else if (value instanceof String)
					bValue = Bytes.toBytes((String) value);
				else if (value instanceof Long)
					bValue = Bytes.toBytes((Long) value);
				else if (value instanceof Boolean)
					bValue = Bytes.toBytes((Boolean) value);
				else if (value instanceof Double)
					bValue = Bytes.toBytes((Double) value);
				else
					continue;

				// Add the property to the index
				index.put(name, bValue);
			}
		} catch (SecurityException e)
		{
			e.printStackTrace();
			return;
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

		// Save the object
		StorageManagedConnection mconn = (StorageManagedConnection) storeMgr.getConnection(sm
				.getObjectManager());
		try
		{
			String appId = HBaseUtils.APP_ID;
			String kind = sm.getClassMetaData().getEntityName();
			Key key = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);
			Object obj = sm.getObject();

			Storage storage = mconn.getStorage();
			storage.createObject(appId, kind, key, obj, index, null);
		} finally
		{
			// mconn.release();
		}
	}

	public void updateObject(StateManager sm, int[] fieldNumbers)
	{
	}

	@Override
	public void locateObject(StateManager sm)
	{
		logger.debug("LOCATING");
	}
}