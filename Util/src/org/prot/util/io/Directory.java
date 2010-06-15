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
package org.prot.util.io;

import java.io.File;

/**
 * Deletes a directrory recursively
 * 
 * @author Andreas Wolke
 * 
 */
public class Directory {
	public static boolean deleteFolder(File folder) {
		boolean success = true;

		// Iterate over the content
		for (String content : folder.list()) {
			// File on the new path
			File file = new File(folder, content);

			// Check if its a folder
			if (file.isDirectory())
				deleteFolder(file);

			// Finally delete the file or folder
			success &= file.delete();
		}

		// Remove the folder itselfe
		success &= folder.delete();

		return success;
	}
}
