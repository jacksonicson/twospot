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
package org.prot.frontend.cache.timeout;

import org.prot.manager.stats.ControllerInfo;

public class CachedControllerInfo extends ControllerInfo
{
	private static final long serialVersionUID = 2352999772518891579L;

	private long timestamp;

	private Long blocked = null;

	public CachedControllerInfo(ControllerInfo info)
	{
		update(info);
	}

	public void setBlocked(boolean blocked)
	{
		if (blocked)
			this.blocked = System.currentTimeMillis();
		else
			this.blocked = null;
	}

	public Long getBlocked()
	{
		return blocked;
	}

	public boolean isBlocked()
	{
		return blocked != null;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
}
