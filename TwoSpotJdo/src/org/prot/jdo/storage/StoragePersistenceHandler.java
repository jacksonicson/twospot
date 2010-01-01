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
package org.prot.jdo.storage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.datanucleus.ObjectManager;
import org.datanucleus.StateManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.StorePersistenceHandler;
import org.datanucleus.util.Localiser;
import org.prot.storage.Key;
import org.prot.storage.Storage;

import com.google.protobuf.CodedOutputStream;

/**
 * Wichtigste Klasse. Hier werden die Objekte serialisiert und in der Datenbank
 * gespeichert. Au�erdem m�ssen die Index-Tabellen hier aktualisiert werden.
 * 
 * @author Andreas Wolke
 * 
 */
public class StoragePersistenceHandler implements StorePersistenceHandler
{
	private static final Logger logger = Logger.getLogger(StoragePersistenceHandler.class);

	protected static final Localiser LOCALISER = Localiser.getInstance(
			"org.datanucleus.store.hbase.Localisation", StorageStoreManager.class.getClassLoader());

	private StorageStoreManager storeMgr;

	StoragePersistenceHandler(StoreManager storeMgr)
	{
		this.storeMgr = (StorageStoreManager) storeMgr;
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
			String appId = StorageHelper.APP_ID;
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

	private List<IndexMessage> buildIndex(AbstractClassMetaData acmd)
	{
		List<IndexMessage> index = new ArrayList<IndexMessage>();

		int[] memberPositions = acmd.getAllMemberPositions();
		for (int memberPosition : memberPositions)
		{

			AbstractMemberMetaData member = acmd.getMetaDataForManagedMemberAtPosition(memberPosition);
			int fieldNumber = member.getAbsoluteFieldNumber();
			String fieldName = member.getName();
			int fieldType;

			Class<?> type = member.getType();
			if (type == String.class)
			{
				fieldType = 0;
			} else if (type == Integer.class)
			{
				fieldType = 1;
			} else if (type == Long.class)
			{
				fieldType = 2;
			} else if (type == Double.class)
			{
				fieldType = 3;
			} else if (type == Boolean.class)
			{
				fieldType = 4;
			} else if (type == Key.class)
			{
				fieldType = 5;
			} else
			{
				throw new NucleusException("Unknown field type");
			}

			IndexMessage message = new IndexMessage(fieldNumber, fieldName, fieldType);
			index.add(message);
		}

		return index;
	}

	private byte[] createMessage(StateManager sm) throws IOException
	{
		AbstractClassMetaData acmd = sm.getClassMetaData();

		// Create a protocol buffer message
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final CodedOutputStream output = CodedOutputStream.newInstance(stream);

		// Create and write the index data
		List<IndexMessage> index = buildIndex(acmd);
		for (IndexMessage message : index)
			message.writeTo(1, output);

		// Write the class-name
		String className = acmd.getFullClassName();
		output.writeString(2, className);

		// Fetch and write all fields (serialize the object)
		InsertFieldManager fieldManager = new InsertFieldManager(output);
		sm.provideFields(acmd.getAllMemberPositions(), fieldManager);

		// Flush
		output.flush();

		return stream.toByteArray();
	}

	public void insertObject(StateManager sm)
	{
		// Check if the storage manager manages the class
		if (!storeMgr.managesClass(sm.getClassMetaData().getFullClassName()))
		{
			storeMgr.addClass(sm.getClassMetaData().getFullClassName(), sm.getObjectManager()
					.getClassLoaderResolver());
		}

		// Create a connection
		StorageManagedConnection connection = (StorageManagedConnection) storeMgr.getConnection(sm
				.getObjectManager());
		String appId = StorageHelper.APP_ID;
		String kind = sm.getClassMetaData().getEntityName();
		Storage storage = connection.getStorage();

		try
		{
			// Serialize the message
			byte[] serializedObject = createMessage(sm);
			logger.debug("Serialized object: " + new String(serializedObject));

			// Call storage to create the object
			Key key = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);
			storage.createObject(appId, kind, key, serializedObject);

		} catch (IOException e)
		{
			throw new NucleusException("Error while inserting object", e);
		} finally
		{
			// TODO: Release the connection
		}
	}

	public void updateObject(StateManager sm, int[] fieldNumbers)
	{
		logger.debug("UPDATE OBJECT");
		// Check if the storage manager manages the class
		if (!storeMgr.managesClass(sm.getClassMetaData().getFullClassName()))
		{
			throw new NucleusException("Cannot update an unmanged class");
		}
		// Save the object
		StorageManagedConnection mconn = (StorageManagedConnection) storeMgr.getConnection(sm
				.getObjectManager());
		try
		{
			String appId = StorageHelper.APP_ID;
			String kind = sm.getClassMetaData().getEntityName();
			Key key = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);
			Object obj = sm.getObject();

			Storage storage = mconn.getStorage();
			storage.updateObject(appId, kind, key, obj, null, null);

			logger.debug("Update done");

		} finally
		{
			// mconn.release();
		}
	}

	@Override
	public void locateObject(StateManager sm)
	{
		logger.debug("LOCATING");
	}
}