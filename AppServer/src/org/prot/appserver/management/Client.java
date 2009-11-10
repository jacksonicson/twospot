package org.prot.appserver.management;

import javax.management.JMX;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Client
{

	public Client()
	{
		try
		{
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");
			JMXConnector connector = JMXConnectorFactory.connect(url, null);
			MBeanServerConnection servercon = connector.getMBeanServerConnection();
			
			ObjectName name = new ObjectName("org.prot:type=ServerStatus");
			MBeanInfo info = servercon.getMBeanInfo(name);
			System.out.println("Name: " + info.getClassName());
			
			ServerStatusMXBean proxy = JMX.newMBeanProxy(servercon, name, ServerStatusMXBean.class);
//			System.out.println("Value: " + proxy.getValue());
			
			connector.close(); 
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String arg[])
	{
		new Client();
	}
}
