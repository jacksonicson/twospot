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
package org.prot.app.security;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class AsPermissionCollection extends PermissionCollection
{
	private static final long serialVersionUID = 4860614223592800268L;

	private Set<Permission> permissions = new HashSet<Permission>();

	private Set<Permission> cache = new HashSet<Permission>();

	public void addAll(PermissionCollection col)
	{
		permissions.addAll(Collections.list(col.elements()));
	}

	@Override
	public void add(Permission permission)
	{
		permissions.add(permission);
	}

	@Override
	public Enumeration<Permission> elements()
	{
		return Collections.enumeration(permissions);
	}

	@Override
	public boolean implies(Permission permission)
	{
		if (cache.contains(permission))
			return true;

		for (Permission perm : permissions)
		{
			if (perm.implies(permission))
			{
				if (cache.contains(permission) == false)
					cache.add(permission);

				return true;
			}
		}

		return false;
	}
}
