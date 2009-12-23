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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.log4j.Logger;
import org.datanucleus.ObjectManager;
import org.datanucleus.StateManager;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.StorePersistenceHandler;
import org.datanucleus.util.Localiser;
import org.datanucleus.util.StringUtils;

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

	/** Localiser for messages. */
	protected static final Localiser LOCALISER = Localiser.getInstance(
			"org.datanucleus.store.hbase.Localisation", HBaseStoreManager.class.getClassLoader());

	protected final HBaseStoreManager storeMgr;

	HBasePersistenceHandler(StoreManager storeMgr)
	{
		this.storeMgr = (HBaseStoreManager) storeMgr;
	}

	public void close()
	{
		// Do nothing
	}

	public void deleteObject(StateManager sm)
	{
		// Check if read-only so update not permitted
		storeMgr.assertReadOnlyForUpdateOfObject(sm);
		HBaseManagedConnection mconn = (HBaseManagedConnection) storeMgr.getConnection(sm.getObjectManager());
		try
		{

			AbstractClassMetaData acmd = sm.getClassMetaData();
			HTable table = mconn.getHTable(HBaseUtils.getTableName(acmd));
			table.delete(newDelete(sm));

		} catch (IOException e)
		{
			throw new NucleusDataStoreException(e.getMessage(), e);
		} finally
		{
			mconn.release();
		}
	}

	public void fetchObject(StateManager sm, int[] fieldNumbers)
	{
		System.out.println("fetching object");
		// HBaseManagedConnection mconn = (HBaseManagedConnection)
		// storeMgr.getConnection(sm.getObjectManager());
		// try
		// {
		// AbstractClassMetaData acmd = sm.getClassMetaData();
		// HTable table = mconn.getHTable(HBaseUtils.getTableName(acmd));
		// Result result = getResult(sm, table);
		// if (result.getRow() == null)
		// {
		// throw new NucleusObjectNotFoundException();
		// }
		// HBaseFetchFieldManager fm = new HBaseFetchFieldManager(sm, result);
		// sm.replaceFields(acmd.getAllMemberPositions(), fm);
		// table.close();
		// } catch (IOException e)
		// {
		// throw new NucleusDataStoreException(e.getMessage(), e);
		// } finally
		// {
		// mconn.release();
		// }
	}

	public Object findObject(ObjectManager om, Object id)
	{
		System.out.println("find object");
		// TODO Auto-generated method stub
		return null;
	}

	public void insertObject(StateManager sm)
	{
		System.out.println("insert object");

		// Check if read-only so update not permitted
		storeMgr.assertReadOnlyForUpdateOfObject(sm);

		if (!storeMgr.managesClass(sm.getClassMetaData().getFullClassName()))
		{
			storeMgr.addClass(sm.getClassMetaData().getFullClassName(), sm.getObjectManager()
					.getClassLoaderResolver());
		}

		// Check existence of the object since HBase doesn't enforce application
		// identity
		try
		{
			// Throws an exception if the object could not be located
			locateObject(sm);

			throw new NucleusUserException(LOCALISER.msg("HBase.Insert.ObjectWithIdAlreadyExists",
					StringUtils.toJVMIDString(sm.getObject()), sm.getInternalObjectId()));
		} catch (NucleusObjectNotFoundException onfe)
		{
			// Do nothing since object with this id doesn't exist
		}

		HBaseManagedConnection mconn = (HBaseManagedConnection) storeMgr.getConnection(sm.getObjectManager());
		try
		{
			AbstractClassMetaData acmd = sm.getClassMetaData();

			HTable table = mconn.getHTable(HBaseUtils.getTableName(acmd));

			// Generator fills key automatically
			Key kkey = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);

			String key = HBaseUtils.getRowKey(acmd, kkey);

			Put put = new Put(key.getBytes());

			// Create a serialized version of the class
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(out);

			oout.writeObject(sm.getObject());

			System.out.println("Object: " + sm.getObject().getClass());

			// entity:serialized
			put.add("entity".getBytes(), "serialized".getBytes(), out.toByteArray());

			table.put(put);
			table.close();
		} catch (IOException e)
		{
			throw new NucleusDataStoreException(e.getMessage(), e);
		} finally
		{
			mconn.release();
		}
	}

	private Put newPut(StateManager sm) throws IOException
	{
		// Get the key of the object
		Key key = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);

		// Serialize the key
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(key);

		// Create a new put operation
		Put batch = new Put(bos.toByteArray());

		// Close the streams
		oos.close();
		bos.close();
		return batch;
	}

	private Delete newDelete(StateManager sm) throws IOException
	{
		// Get the primary key
		Key key = (Key) sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);

		// Serialize the primary key
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(key);

		// Create a new delete operation
		Delete batch = new Delete(bos.toByteArray());

		// Close the streams
		oos.close();
		bos.close();

		return batch;
	}

	private Result getResult(StateManager sm, HTable table) throws IOException
	{
		Object pkValue = sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(pkValue);
		Get get = new Get(bos.toByteArray());
		Result result = table.get(get);
		oos.close();
		bos.close();
		return result;
	}

	private boolean exists(AbstractClassMetaData acmd, StateManager sm, HTable table) throws IOException
	{
		// Load the primary key
		Object pkValue = sm.provideField(sm.getClassMetaData().getPKMemberPositions()[0]);

		// Serialize the primary key
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(pkValue);

		String row = HBaseUtils.getRowKey(acmd, (Key) pkValue);
		System.out.println("Exists row: " + row);
		Get get = new Get(row.getBytes());

		boolean result = table.exists(get);
		oos.close();
		bos.close();
		return result;
	}

	public void locateObject(StateManager sm)
	{
		System.out.println("locating object");

		HBaseManagedConnection mconn = (HBaseManagedConnection) storeMgr.getConnection(sm.getObjectManager());
		try
		{
			AbstractClassMetaData acmd = sm.getClassMetaData();
			HTable table = mconn.getHTable(HBaseUtils.getTableName(acmd));

			// Check if the object exists in that table
			if (!exists(acmd, sm, table))
			{
				throw new NucleusObjectNotFoundException();
			}
			table.close();
		} catch (IOException e)
		{
			throw new NucleusDataStoreException(e.getMessage(), e);
		} finally
		{
			mconn.release();
		}
	}

	public void updateObject(StateManager sm, int[] fieldNumbers)
	{
		System.out.println("update object");
		// // Check if read-only so update not permitted
		// storeMgr.assertReadOnlyForUpdateOfObject(sm);
		//
		// HBaseManagedConnection mconn = (HBaseManagedConnection)
		// storeMgr.getConnection(sm.getObjectManager());
		// try
		// {
		// AbstractClassMetaData acmd = sm.getClassMetaData();
		// HTable table = mconn.getHTable(HBaseUtils.getTableName(acmd));
		// Put put = newPut(sm);
		// Delete delete = newDelete(sm); // we will ignore the delete object
		// HBaseInsertFieldManager fm = new HBaseInsertFieldManager(sm, put,
		// delete);
		// sm.provideFields(fieldNumbers, fm);
		// if (!put.isEmpty())
		// {
		// table.put(put);
		// }
		// if (!put.isEmpty())
		// {
		// table.delete(delete);
		// }
		// table.close();
		// } catch (IOException e)
		// {
		// throw new NucleusDataStoreException(e.getMessage(), e);
		// } finally
		// {
		// mconn.release();
		// }
	}
}