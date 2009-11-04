package org.prot.appserver.appfetch;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.prot.appserver.app.AppInfo;

public class WarLoader
{
	private String discLocation = "C:/temp";

	public void handle(AppInfo appInfo)
	{
		try
		{
			String folder = createFolder(appInfo.getAppId());
			decompress(appInfo.getWarFile(), folder);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private String createFolder(String appId) throws IOException
	{
		String folder = discLocation + "/" + appId;
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
		while ((entry = zipIn.getNextEntry()) != null)
		{
			System.out.println("zip entry: " + entry.isDirectory() + " - " + entry.getName());
			if (entry.isDirectory())
			{
				File dir = new File(folder + "/" + entry.getName());
				dir.mkdir();
			} else
			{
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

	}

	private void readConfiguration(String folder)
	{
		File yaml = new File(folder + "/app.yaml");
	}
}
