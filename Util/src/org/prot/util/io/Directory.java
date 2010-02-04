package org.prot.util.io;

import java.io.File;

public class Directory
{
	public static boolean deleteFolder(File folder)
	{
		boolean success = true;

		// Iterate over the content
		for (String content : folder.list())
		{
			// File on the new path
			File file = new File(folder, content);

			// Check if its a folder
			if (file.isDirectory())
				deleteFolder(file);

			// Finally delete the file or folder
			success &= file.delete();
		}

		return success;
	}
}
