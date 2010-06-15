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

public class UnsupportedOperationException extends RuntimeException
{
	private static final long serialVersionUID = 4805915842629335730L;

	public UnsupportedOperationException(String msg)
	{
		super(msg);
	}
}
