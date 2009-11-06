package org.prot.appserver.appfetch;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.prot.appserver.AppRuntime;
import org.prot.appserver.Configuration;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.app.WebConfiguration;
import org.yaml.snakeyaml.Yaml;

public class WarLoader
{
	public void handle(AppInfo appInfo)
	{
		try
		{
			// Create a temporary folder for the application files
			String folder = createFolder(appInfo.getAppId());

			// Decompress application files to the directory
			decompress(appInfo.getWarFile(), folder);

			// Load application configuration
			readConfiguration(appInfo, folder);

		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (InvalidYamlFileException e)
		{
			e.printStackTrace();
		}
	}

	private String createFolder(String appId) throws IOException
	{
		String folder = Configuration.getInstance().getWorkingDirectory() + "/" + appId;
		Configuration.getInstance().setAppDirectory(folder);

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

	private void readConfiguration(AppInfo appInfo, String folder) throws IOException,
			InvalidYamlFileException
	{
		File yamlFile = new File(folder + "/app.yaml");
		InputStream in = new FileInputStream(yamlFile);

		Yaml yaml = new Yaml();
		Object parsed = yaml.load(in);

		if (parsed instanceof Map == false)
			throw new InvalidYamlFileException();

		configure(appInfo, (Map) parsed);
	}

	private void configure(AppInfo appInfo, Map<String, Object> yaml)
	{
		String appId = (String) yaml.get("appId");
		appInfo.setAppId(appId);

		String runtime = (String) yaml.get("runtime");
		if (runtime.equals("java"))
			appInfo.setRuntime(AppRuntime.JAVA);
		else if (runtime.equals("python"))
			appInfo.setRuntime(AppRuntime.PYTHON);

		// TODO: The following depends on the application code (use something
		// like a plugin infrastructure)
		List<Map<String, String>> handlers = (List<Map<String,String>>) yaml.get("handlers");
		if (handlers != null)
		{
			for (Map<String,String> handler : handlers)
			{
				String url = handler.get("regUrl");
				String file = handler.get("file"); 
				
				WebConfiguration webConfig = new WebConfiguration(url, file);
				appInfo.addWebConfiguration(webConfig); 
			}
		}
	}
}
