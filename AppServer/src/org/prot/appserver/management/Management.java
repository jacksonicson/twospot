package org.prot.appserver.management;

import java.util.List;

public class Management
{

	public void init()
	{
//		try
//		{
//			// Needs the RMI registry on the specific port
//			String jmxurl = "service:jmx:rmi:///jndi/rmi://localhost:9999/" + "appId";
//
//			MBeanServer server = MBeanServerFactory.createMBeanServer();
//
//			ServerStatus status = new ServerStatus();
//			ObjectName name = new ObjectName("ServerStatus:port=1099");
//			server.registerMBean(status, name);
//
//			// Create a new connector
//			JMXServiceURL url = new JMXServiceURL(jmxurl);
//			
//			Map<String,String> properties = new HashMap<String, String>(); 
////			properties.put("com.sun.management.jmxremote.authenticate","false");
//			JMXConnectorServer connector = JMXConnectorServerFactory.newJMXConnectorServer(url, properties, server);
//			
//
//			// Start the connector
//			connector.start();
//
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
	}

	void handleConnection()
	{
	}
}
