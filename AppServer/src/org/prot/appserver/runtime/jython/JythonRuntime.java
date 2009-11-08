package org.prot.appserver.runtime.jython;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.config.Configuration;
import org.prot.appserver.runtime.AppRuntime;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class JythonRuntime implements AppRuntime
{
	private static final Logger logger = Logger.getLogger(JythonRuntime.class);

	private static final String IDENTIFIER = "PYTHON";

	@Override
	public String getIdentifier()
	{
		return IDENTIFIER;
	}

	@Override
	public void launch(AppInfo appInfo)
	{
		logger.debug("Launching jython runtime");

		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring_python.xml",
				getClass()));

		// Configure server port
		SelectChannelConnector connector = (SelectChannelConnector) factory.getBean("Connector");
		connector.setPort(Configuration.getInstance().getAppServerPort());

		// Configure the Handler
		PythonHandler handler = (PythonHandler)factory.getBean("PythonHandler");
		handler.setAppInfo(appInfo);
		
		// Start server
		Server server = (Server) factory.getBean("Server");
		try
		{
			server.start();
		} catch (Exception e)
		{
			logger.error("Could not start the jetty server", e);
		}
	}

	@Override
	public void loadConfiguration(AppInfo appInfo, Map yaml)
	{
		JythonConfiguration config = new JythonConfiguration();
		appInfo.setRuntimeConfiguration(config); 
		
		List<Map<String, String>> handlers = (List<Map<String, String>>) yaml.get("handlers");
		if (handlers != null)
		{
			for (Map<String, String> handler : handlers)
			{
				String url = handler.get("regUrl");
				String file = handler.get("file");

				WebConfiguration webConfig = new WebConfiguration(url, file);
				config.addWebConfig(webConfig);
			}
		}
	}
}
