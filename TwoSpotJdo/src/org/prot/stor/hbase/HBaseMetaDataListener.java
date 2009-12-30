/**********************************************************************
Copyright (c) 2009 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
   ...
 **********************************************************************/
package org.prot.stor.hbase;

import org.apache.log4j.Logger;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.InvalidMetaDataException;
import org.datanucleus.metadata.MetaDataListener;
import org.datanucleus.util.Localiser;

/**
 * Listener for the load of metadata for classes. Allows us to reject metadata
 * when it isn't supported by this datastore.
 */
public class HBaseMetaDataListener implements MetaDataListener
{
	private static final Logger logger = Logger.getLogger(HBaseMetaDataListener.class);

	protected static final Localiser LOCALISER = Localiser.getInstance(
			"org.datanucleus.store.hbase.Localisation", HBaseStoreManager.class.getClassLoader());

	private HBaseStoreManager storeManager;

	HBaseMetaDataListener(HBaseStoreManager storeManager)
	{
		this.storeManager = storeManager;
	}

	public void loaded(AbstractClassMetaData cmd)
	{
		logger.debug("Meta data listener loaded");

		if (cmd.getIdentityType() == IdentityType.DATASTORE && !cmd.isEmbeddedOnly())
		{
			throw new InvalidMetaDataException(LOCALISER, "HBase.DatastoreID", cmd.getFullClassName());
		}
	}
}