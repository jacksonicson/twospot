package org.prot.appserver.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.runtime.AppRuntime;
import org.prot.appserver.runtime.NoSuchRuntimeException;
import org.prot.appserver.runtime.RuntimeConfigurer;
import org.prot.appserver.runtime.RuntimeRegistry;
import org.yaml.snakeyaml.Yaml;

public class AppConfigurer
{
	private static final Logger logger = Logger.getLogger(AppConfigurer.class);

	private static final String CONFIG_FILE = "app.yaml";

	private RuntimeRegistry runtimeRegistry;

	public AppInfo configure(String appDirectory) throws ConfigurationException
	{
		try
		{
			// Do the general configuration
			Map<?, ?> yamlObj = loadFile(appDirectory);
			AppInfo appInfo = loadBasicConfiguration(yamlObj);

			// Do the runtime specific configuration
			AppRuntime runtime = runtimeRegistry.getRuntime(appInfo.getRuntime());
			runtime.loadConfiguration(appInfo, yamlObj);

			return appInfo;

		} catch (InvalidYamlFileException e)
		{
			logger.error("Invalid YAML application configuration", e);
			throw new ConfigurationException();
		} catch (IOException e)
		{
			logger.error("Error while reading the YAML application configuration", e);
			throw new ConfigurationException();
		} catch (NoSuchRuntimeException e)
		{
			logger.error("Could not find the runtime configured in the YAML file", e);
			throw new ConfigurationException();
		}
	}

	private AppInfo loadBasicConfiguration(Map<?, ?> yaml)
	{
		AppInfo appInfo = new AppInfo();

		// Extract the AppId
		String appId = (String) yaml.get("appId");
		appInfo.setAppId(appId.trim());

		// Extract the Runtime
		String runtime = (String) yaml.get("runtime");
		appInfo.setRuntime(runtime);

		return appInfo;
	}

	private Map<?, ?> loadFile(String appDirectory) throws InvalidYamlFileException, IOException
	{
		File yamlFile = new File(appDirectory + "/" + CONFIG_FILE);
		InputStream in = new FileInputStream(yamlFile);

		Yaml yaml = new Yaml();
		Object yamlObj = yaml.load(in);

		if (yamlObj instanceof Map<?, ?> == false)
			throw new InvalidYamlFileException();

		return (Map<?, ?>) yamlObj;
	}

	public void setRuntimeRegistry(RuntimeRegistry runtimeRegistry)
	{
		this.runtimeRegistry = runtimeRegistry;
	}
}
