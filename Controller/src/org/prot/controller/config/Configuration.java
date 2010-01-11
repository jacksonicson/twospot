package org.prot.controller.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.prot.util.net.AddressExtractor;

public class Configuration
{
	private static final Logger logger = Logger.getLogger(Configuration.class);

	// Singleton instance
	private static Configuration configuration;

	// UID of this controller
	private final String UID;

	// Address
	private String address;

	// Configuration
	private Properties properties = new Properties();

	// Domain plus port under which the platform is running
	private String platformDomain;

	// Port on which the Controller listens
	private int controllerPort = -1;

	// Prefix which ist used when building the classpath for the AppServer
	private String classpathPrefix = "";

	// Contains an additional classpath which is added to the classpath file
	private String additionalClasspath = "";

	// Connection URL for the fileserver
	private String fileServerURL;

	// VM options which are used when starting the AppServer
	private String vmOptions;

	public static Configuration getConfiguration()
	{
		if (configuration == null)
		{
			configuration = new Configuration();
		}

		return configuration;
	}

	private final InetAddress getInetAddress(String networkInterface) throws SocketException
	{
		return AddressExtractor.getInetAddress(networkInterface, false);
	}

	public Configuration()
	{
		// UID
		this.UID = java.util.UUID.randomUUID().toString();

		// Read the configuration files
		InputStream utilIn = this.getClass().getResourceAsStream("/etc/config.properties");
		InputStream controllerIn = this.getClass().getResourceAsStream("/etc/controller.properties");
		try
		{
			properties.load(utilIn);
			properties.load(controllerIn);

			this.controllerPort = Integer.parseInt(properties.getProperty("http.controller.port"));

			this.platformDomain = properties.getProperty("platform.domain");

			this.classpathPrefix = properties.getProperty("appserver.classpath.prefix");

			this.additionalClasspath = properties.getProperty("appserver.classpath.additional");

			this.fileServerURL = properties.getProperty("fileserver.url");

			this.vmOptions = properties.getProperty("appserver.vm.options");

			this.address = getInetAddress(properties.getProperty("zk.controller.networkInterface"))
					.getHostAddress();

		} catch (SocketException e)
		{
			logger.error("Could not parse the configuration", e);
			System.exit(1);
		} catch (IOException e)
		{
			logger.error("Could not load configuration", e);
			System.exit(1);
		} catch (NumberFormatException e)
		{
			logger.error("Could not parse the configuration", e);
			System.exit(1);
		} catch (NullPointerException e)
		{
			logger.error("Could not parse the configuration", e);
			System.exit(1);
		}
	}

	public Properties getProperties()
	{
		return properties;
	}

	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}

	public int getControllerPort()
	{
		return controllerPort;
	}

	public String getPlatformDomain()
	{
		return platformDomain;
	}

	public String getClasspathPrefix()
	{
		return classpathPrefix;
	}

	public String getAdditionalClasspath()
	{
		return additionalClasspath;
	}

	public String getFileServerURL()
	{
		return fileServerURL;
	}

	public String getVmOptions()
	{
		return vmOptions;
	}

	public String getUID()
	{
		return UID;
	}

	public String getAddress()
	{
		return address;
	}

}
