package org.prot.appserver.extract;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public class WarExtractor implements AppExtractor
{
	private static final Logger logger = Logger.getLogger(WarExtractor.class);

	@Override
	public void extract(byte[] archive, String destPath, String appId) throws IOException
	{
		String folder = createFolder(destPath);
		decompress(archive, folder);
	}

	private String createFolder(String destPath) throws IOException
	{
		String folder = destPath;
		logger.debug("Using App-folder: " + folder);

		// Delete folder if it already exists!
		File file = new File(folder);
		if (file.exists())
			file.delete();

		// Create the folder
		file.mkdir();

		// Return path to the folder
		return folder;
	}

	private void decompress(byte[] warFile, String folder) throws IOException
	{
		ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(warFile));
		ZipEntry entry;

		logger.debug("Reading ZIP entries");
		int fileCounter = 0;
		while ((entry = zipIn.getNextEntry()) != null)
		{
			logger.debug("ZIP entry: " + entry.isDirectory() + " - " + entry.getName());
			if (entry.isDirectory())
			{
				// Ignore directory entries (Directory structure is created for
				// each file)
				// ZIP-Files don't require folde entries!
			} else
			{
				// Destination file for the ZIP-File
				File dest = new File(folder, entry.getName());

				// Get the parent directory
				File parentDir = dest.getParentFile();

				// Create folder structure to the parent directory
				parentDir.mkdirs();

				// Extract the file
				fileCounter++;
				FileOutputStream fos = new FileOutputStream(dest);
				BufferedOutputStream fo = new BufferedOutputStream(fos);

				byte buffer[] = new byte[1024];
				int len = 0;
				while ((len = zipIn.read(buffer, 0, 1024)) != -1)
				{
					fo.write(buffer, 0, len);
				}

				fo.flush();
				fo.close();
			}
		}

		logger.debug("Done with " + fileCounter + " files extracted");
	}
}
