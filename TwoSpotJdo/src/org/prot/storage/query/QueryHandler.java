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
package org.prot.storage.query;

import java.io.IOException;
import java.util.Collection;

public interface QueryHandler
{
	public void execute(Collection<byte[]> result, StorageQuery query) throws IOException;

	public void execute(Collection<byte[]> result, StorageQuery query, AtomarCondition condition)
			throws IOException;

}
