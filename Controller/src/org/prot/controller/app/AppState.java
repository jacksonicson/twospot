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
package org.prot.controller.app;

public enum AppState
{
	NEW(AppLife.FIRST),

	STARTING(AppLife.FIRST),

	ONLINE(AppLife.FIRST),

	BANNED(AppLife.SECOND),

	DROPPED(AppLife.SECOND),

	KILLED(AppLife.SECOND),

	DEPLOYED(AppLife.SECOND),

	DEAD(AppLife.SECOND);

	private final AppLife life;

	private AppState(AppLife life)
	{
		this.life = life;
	}

	public AppLife getLife()
	{
		return this.life;
	}
}
