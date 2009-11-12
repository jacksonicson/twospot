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
	public String extract(byte[] archive, String destPath, String appId) throws IOException
	{
		String folder = createFolder(destPath, appId);
		decompress(archive, folder);
		return folder;
	}

	private String createFolder(String destPath, String appId) throws IOException
	{
		String folder = destPath + "/" + appId;
		logger.debug("Using app-folder: " + folder);

		File file = new File(folder);
		if (file.exists())
			file.delete();

		file.mkdir();
		return folder;
	}

	private void decompress(byte[] warFile, String folder) throws IOException
	{
		ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(warFile));
		ZipEntry entry;
		
		logger.debug("Reading zip entries");
		int fileCounter = 0; 
		while ((entry = zipIn.getNextEntry()) != null)
		{
			logger.debug("zip entry: " + entry.isDirectory() + " - " + entry.getName());
			if (entry.isDirectory())
			{
				File dir = new File(folder + "/" + entry.getName());
				dir.mkdir();
			} else
			{
				fileCounter++; 
				FileOutputStream fos = new FileOutputStream(folder + "/" + entry.getName());
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
		logger.debug(fileCounter + " files extracted");
	}
}
