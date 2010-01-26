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
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.FetchPlan;
import org.datanucleus.ObjectManager;
import org.datanucleus.StateManager;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.FieldValues;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.StorePersistenceHandler;
import org.datanucleus.util.Localiser;
import org.prot.jdo.storage.field.FetchFieldManager;
import org.prot.jdo.storage.field.InsertFieldManager;
import org.prot.jdo.storage.messages.EntityMessage;
import org.prot.jdo.storage.messages.IndexMessage;
import org.prot.jdo.storage.messages.types.StorageProperty;
import org.prot.jdo.storage.messages.types.StorageType;
import org.prot.storage.Key;
import org.prot.storage.Storage;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class StoragePersistenceHandler implements StorePersistenceHandler
{
	private static final Logger logger = Logger.getLogger(StoragePersistenceHandler.class);

	protected static final Localiser LOCALISER = Localiser.getInstance(
			"org.datanucleus.store.hbase.Localisation", StorageStoreManager.class.getClassLoader());

	private StorageStoreManager storeManager;

	StoragePersistenceHandler(StoreManager storeMgr)
	{
		this.storeManager = (StorageStoreManager) storeMgr;
	}

	@Override
	public void close()
	{
		// Do nothing here
	}

	public void deleteObject(StateManager sm)
	{
		// Cannot delete a read only object
		storeManager.assertReadOnlyForUpdateOfObject(sm);

		// Get a connection
		StorageManagedConnection mconn = (StorageManagedConnection) storeManager.getConnection(sm
				.getObjectManager());
		try
		{
			// Aquire object infos
			String appId = StorageHelper.APP_ID;
			String kind = sm.getObject().getClass().getSimpleName();

			// Get the primary key
			Object pKeyObj = sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);
			Key key = (Key) pKeyObj;
			if (key == null)
				return;

			Storage storage = mconn.getStorage();
			storage.deleteObject(appId, kind, key);

		} finally
		{
			// Release the connection
			mconn.release();
		}
	}

	public void fetchObject(StateManager sm, int[] fieldNumbers)
	{
		StorageManagedConnection connection = (StorageManagedConnection) storeManager.getConnection(sm
				.getObjectManager());
		Storage storage = connection.getStorage();

		AbstractClassMetaData acmd = sm.getClassMetaData();
		Key key = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);

		byte[] object = storage.query(StorageHelper.APP_ID, key);
		if (object == null)
			throw new NucleusObjectNotFoundException();

		CodedInputStream in;
		try
		{
			in = CodedInputStream.newInstance(object);
		} catch (NullPointerException e)
		{
			throw new NucleusDataStoreException("Could not decode object", e);
		}

		FetchFieldManager fm;
		try
		{
			fm = new FetchFieldManager(in, acmd);
		} catch (IOException e)
		{
			throw new NucleusDataStoreException(e.getMessage(), e);
		}

		sm.replaceFields(acmd.getAllMemberPositions(), fm);
	}

	public Object findObject(final ObjectManager om, Object id)
	{
		StorageManagedConnection connection = (StorageManagedConnection) storeManager.getConnection(om);
		Storage storage = connection.getStorage();

		Key key = (Key) id;

		byte[] object = storage.query(StorageHelper.APP_ID, key);
		if (object == null)
			throw new NucleusObjectNotFoundException();

		CodedInputStream in = CodedInputStream.newInstance(object);

		final FetchFieldManager fm;
		try
		{
			fm = new FetchFieldManager(in, om);
		} catch (IOException e)
		{
			throw new NucleusDataStoreException(e.getMessage(), e);
		}

		final AbstractClassMetaData acmd = fm.getAcmd();
		final Class<?> cl = om.getClassLoaderResolver().classForName(fm.getMessageClass());
		final ClassLoaderResolver clr = om.getClassLoaderResolver();

		Object candidate = om.findObjectUsingAID(cl, new FieldValues()
		{
			@Override
			public void fetchFields(StateManager sm)
			{
				// Replace all primary key fields
				sm.replaceFields(acmd.getPKMemberPositions(), fm);

				// Replace all basic member fields
				int[] memberPositions = acmd.getBasicMemberPositions(clr, om.getMetaDataManager());
				sm.replaceFields(memberPositions, fm);
			}

			@Override
			public void fetchNonLoadedFields(StateManager sm)
			{
				// Replace non loaded fields
				sm.replaceNonLoadedFields(acmd.getAllMemberPositions(), fm);
			}

			@Override
			public FetchPlan getFetchPlanForLoading()
			{
				return null;
			}

		}, true, true);

		return candidate;
	}

	@Override
	public void locateObject(StateManager sm)
	{
		StorageManagedConnection connection = (StorageManagedConnection) storeManager.getConnection(sm
				.getObjectManager());
		Storage storage = connection.getStorage();

		Key key = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);

		byte[] object = storage.query(StorageHelper.APP_ID, key);
		if (object == null)
			throw new NucleusObjectNotFoundException();
	}

	private List<IndexMessage> buildIndexMessages(AbstractClassMetaData acmd)
	{
		List<IndexMessage> index = new ArrayList<IndexMessage>();

		// Iterate over all fields
		int[] memberPositions = acmd.getAllMemberPositions();
		for (int memberPosition : memberPositions)
		{
			// Get field finso
			AbstractMemberMetaData member = acmd.getMetaDataForManagedMemberAtPosition(memberPosition);

			// Get infos about this field
			int fieldNumber = member.getAbsoluteFieldNumber();
			String fieldName = member.getName();
			StorageType fieldType = StorageProperty.newType(member.getType());

			// Build the index message
			IndexMessage.Builder builder = IndexMessage.newBuilder();
			builder.setFieldNumber(StorageProperty.messageFieldNumber(fieldNumber));
			builder.setFieldName(fieldName);
			builder.setFieldType(fieldType);

			// Add index message to index list
			index.add(builder.build());
		}

		return index;
	}

	private byte[] createMessage(StateManager sm) throws IOException
	{
		AbstractClassMetaData acmd = sm.getClassMetaData();

		// Write the class-name
		String className = acmd.getFullClassName();

		// Build the IndexMessages
		List<IndexMessage> index = buildIndexMessages(acmd);

		// Build the EntityMessage
		EntityMessage.Builder entity = EntityMessage.newBuilder();
		entity.addAllIndexMessages(index);
		entity.setClassName(className);

		// Fetch and write all fields (serialize the object)
		InsertFieldManager fieldManager = new InsertFieldManager(entity);
		sm.provideFields(acmd.getAllMemberPositions(), fieldManager);

		// Create a protocol buffer message
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		final CodedOutputStream output = CodedOutputStream.newInstance(stream);

		// Serialize the entity
		entity.build().writeTo(output);

		// Flush
		output.flush();

		// Return the serialized message
		return stream.toByteArray();
	}

	public void insertObject(StateManager sm)
	{
		// Check if read-only so update not permitted
		storeManager.assertReadOnlyForUpdateOfObject(sm);

		// Check if the storage manager manages the class
		if (!storeManager.managesClass(sm.getClassMetaData().getFullClassName()))
		{
			// Manage the class
			storeManager.addClass(sm.getClassMetaData().getFullClassName(), sm.getObjectManager()
					.getClassLoaderResolver());
		}

		// Create a connection
		StorageManagedConnection connection = (StorageManagedConnection) storeManager.getConnection(sm
				.getObjectManager());
		Storage storage = connection.getStorage();

		try
		{
			// Serialize the message
			byte[] serializedObject = createMessage(sm);

			// Get the primary key of the entity
			Key key = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);

			// Create the object in the storage service
			String appId = StorageHelper.APP_ID;
			String kind = sm.getClassMetaData().getEntityName();
			storage.createObject(appId, kind, key, serializedObject);

		} catch (IOException e)
		{
			throw new NucleusException("Error while inserting object", e);
		} finally
		{
			// Release the connection
			connection.release();
		}
	}

	public void updateObject(StateManager sm, int[] fieldNumbers)
	{
		// Check if read-only so update not permitted
		storeManager.assertReadOnlyForUpdateOfObject(sm);

		// Save the object
		StorageManagedConnection mconn = (StorageManagedConnection) storeManager.getConnection(sm
				.getObjectManager());
		Storage storage = mconn.getStorage();

		try
		{
			// Serialize the message
			byte[] serializedObject = createMessage(sm);

			// Get the primary key of the entity
			Key key = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);

			// Create the object in the storage service
			String appId = StorageHelper.APP_ID;
			String kind = sm.getClassMetaData().getEntityName();
			storage.updateObject(appId, kind, key, serializedObject);

		} catch (IOException e)
		{
			throw new NucleusException("Error while updating object", e);
		} finally
		{
			// Release the connection
			mconn.release();
		}
	}
}