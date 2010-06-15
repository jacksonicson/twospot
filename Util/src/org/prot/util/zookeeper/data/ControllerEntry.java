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
package org.prot.util.zookeeper.data;

import java.io.Serializable;

public class ControllerEntry implements Serializable
{
	private static final long serialVersionUID = 5323196795384674010L;

	public String serviceAddress;

	public String address;

	public int port;
}
