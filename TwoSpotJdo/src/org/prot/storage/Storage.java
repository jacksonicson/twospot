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

import java.util.List;

import org.prot.storage.query.StorageQuery;

public interface Storage {

	/*
	 * Used by the JDO implementation
	 */
	public List<Key> createKey(String appId, long amount);

	public void createObject(String appId, String kind, Key key, byte[] obj);

	public void updateObject(String appId, String kind, Key key, byte[] obj);

	public boolean deleteObject(String appId, String kind, Key key);

	public List<byte[]> query(StorageQuery query);

	public byte[] query(String appId, Key key);

	/*
	 * Used by the DB browser
	 */
	public List<String> listKinds(String appId);

	public List<byte[]> scanEntities(String appId, String kind);
}
