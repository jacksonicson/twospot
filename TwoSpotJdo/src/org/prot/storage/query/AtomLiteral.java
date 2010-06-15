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

public class AtomLiteral
{
	// Byte[] of the literal value
	private byte[] value;

	// True if value contains a stringified key
	private boolean key;

	public AtomLiteral(byte[] value)
	{
		this.value = value;
	}

	public AtomLiteral(byte[] value, boolean key)
	{
		this.value = value;
		this.key = key;
	}

	public byte[] getValue()
	{
		return value;
	}

	public boolean isKey()
	{
		return this.key;
	}
}
