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
package org.prot.jdo.storage.messages.types;

import java.io.IOException;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public interface IStorageProperty
{
	public StorageType getType();

	public Object getValue();

	public Object getValue(StorageType requiredType);

	public byte[] getValueAsBytes();

	public void writeTo(CodedOutputStream out) throws IOException;

	public void mergeFrom(CodedInputStream input) throws IOException;
}
