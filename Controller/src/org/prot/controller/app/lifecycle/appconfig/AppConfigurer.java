package org.prot.controller.app.lifecycle.appconfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

public class AppConfigurer {
	private static final Logger logger = Logger.getLogger(AppConfigurer.class);

	private static final String CONFIG_FILE = "app.yaml";

	public String configure(String appDirectory) throws ConfigurationException {
		try {
			// Do the general configuration
			Map<?, ?> yamlObj = loadFile(appDirectory);
			String runtime = (String) yamlObj.get("runtime");
			return runtime;

		} catch (InvalidYamlFileException e) {
			logger.error("Invalid YAML application configuration", e);
			throw new ConfigurationException();
		} catch (IOException e) {
			logger.error("Error while reading the YAML application configuration", e);
			throw new ConfigurationException();
		}
	}

	private Map<?, ?> loadFile(String appDirectory) throws InvalidYamlFileException, IOException {
		File yamlFile = new File(appDirectory + "/" + CONFIG_FILE);

		InputStream in = null;
		Object yamlObj = null; 
		try {
			in = new FileInputStream(yamlFile);
			Yaml yaml = new Yaml();
			yamlObj = yaml.load(in);
		} catch (IOException e) {
			throw e; 
		} finally {
			if (in != null)
				in.close();
		}

		if (yamlObj instanceof Map<?, ?> == false)
			throw new InvalidYamlFileException();

		return (Map<?, ?>) yamlObj;
	}
}
