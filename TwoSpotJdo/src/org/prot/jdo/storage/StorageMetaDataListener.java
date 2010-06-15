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
package org.prot.jdo.storage;

import org.apache.log4j.Logger;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.InvalidMetaDataException;
import org.datanucleus.metadata.MetaDataListener;
import org.datanucleus.util.Localiser;

/**
 * Is called whenever the metadata for a class are loaded
 * 
 * @author Andreas Wolke
 */
public class StorageMetaDataListener implements MetaDataListener {
	private static final Logger logger = Logger.getLogger(StorageMetaDataListener.class);

	protected static final Localiser LOCALISER = Localiser.getInstance(
			"org.datanucleus.store.hbase.Localisation", StorageStoreManager.class.getClassLoader());

	public void loaded(AbstractClassMetaData cmd) {
		logger.debug("Meta data listener loaded");

		if (cmd.getIdentityType() != IdentityType.APPLICATION)
			throw new InvalidMetaDataException(LOCALISER, "HBase.DatastoreID", cmd.getFullClassName());
	}
}