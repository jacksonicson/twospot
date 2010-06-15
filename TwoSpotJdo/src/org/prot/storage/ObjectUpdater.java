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
package org.prot.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;

public class ObjectUpdater
{
	private static final Logger logger = Logger.getLogger(ObjectUpdater.class);

	private HBaseManagedConnection connection;

	private ObjectCreator creator;

	private ObjectRemover remover;

	public ObjectUpdater(ConnectionFactory connectionFactory)
	{
		this.connection = connectionFactory.createManagedConnection();

		this.creator = new ObjectCreator(connection);

		this.remover = new ObjectRemover(connection);
	}

	public void updateObject(String appId, String kind, Key key, byte[] obj) throws IOException,
			ClassNotFoundException

	{
		HTable tableEntities = StorageUtils.getTableEntity(connection);
		HTable tableIndexByPropertyAsc = StorageUtils.getTableIndexByPropertyAsc(connection);

		// Retrieve the entity
		byte[] oldObj = remover.retrieveObject(tableEntities, appId, kind, key);
		Map<String, byte[]> oldIndex = remover.createIndexMap(oldObj);
		Map<String, byte[]> newIndex = remover.createIndexMap(obj);

		// Create the difference of the old and new index list
		// List of index entries to delete
		Map<String, byte[]> toDel = new HashMap<String, byte[]>();

		// List of index entries to add
		Map<String, byte[]> toAdd = new HashMap<String, byte[]>();

		// Iterate over all new index antries and check if they are new or have
		// changed to the old ones
		for (String index : newIndex.keySet())
		{
			// Check if the old index list contains the index entry
			if (oldIndex.containsKey(index))
			{
				// The old index list contains the index entry, check now if the
				// values have changed
				if (!Bytes.equals(oldIndex.get(index), newIndex.get(index)))
				{
					// We need to delete the old index entry
					toDel.put(index, oldIndex.get(index));
					// We need to add the new index entry
					toAdd.put(index, newIndex.get(index));
				}
			} else
			{
				// The old index list does not contain the new index entry. We
				// need to add the new index entry
				toAdd.put(index, newIndex.get(index));
			}
		}

		// Iterate over the old index list and check if the index entries are
		// not present in the new index list
		for (String index : oldIndex.keySet())
		{
			// The new index list does not contain the old entry
			if (!newIndex.containsKey(index))
			{
				// We need to remove the old index entry
				toDel.put(index, oldIndex.get(index));
			}
		}

		// Remove all old index enties
		remover.removeObjectFromIndexByProperty(tableIndexByPropertyAsc, appId, kind, key, toDel);

		// Write the new entity to the entity table
		byte[] rowKey = ObjectCreator.writeEntity(tableEntities, appId, kind, key, obj);

		// Add the new index entries
		creator.writeIndexByPropertyAsc(tableIndexByPropertyAsc, rowKey, appId, kind, toAdd);
	}
}
