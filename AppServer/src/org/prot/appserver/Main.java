package org.prot.appserver;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.deploy.WebAppDeployer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main
{

	public Main()
	{
		try
		{

			XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring/spring.xml",
					getClass()));

			WebAppDeployer deployer = (WebAppDeployer) factory.getBean("WebAppDeployer");
		
//			deployer.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
//					".*/jsp-api-[^/]*\\.jar$|.*/jsp-[^/]*\\.jar$");

			Server server = (Server) factory.getBean("Server");
			server.addBean(deployer);

			SelectChannelConnector connector = (SelectChannelConnector) factory.getBean("InsideConnector");
			connector.setPort(Configuration.getInstance().getAppServerPort());

			
			
			server.addLifeCycleListener(new Listener() {

				@Override
				public void lifeCycleFailure(LifeCycle arg0, Throwable arg1)
				{
					// TODO Auto-generated method stub
					
				}

				@Override
				public void lifeCycleStarted(LifeCycle arg0)
				{
					System.out.println("server started"); 
					
				}

				@Override
				public void lifeCycleStarting(LifeCycle arg0)
				{
					// TODO Auto-generated method stub
					
				}

				@Override
				public void lifeCycleStopped(LifeCycle arg0)
				{
					// TODO Auto-generated method stub
					
				}

				@Override
				public void lifeCycleStopping(LifeCycle arg0)
				{
					// TODO Auto-generated method stub
					
				}
				
			});
			server.start();
			new Monitor();

			/*
			 * InputStream configFile =
			 * Main.class.getResourceAsStream("/etc/jetty/configuration.xml");
			 * XmlConfiguration config = new XmlConfiguration(configFile);
			 * Server server = (Server) config.configure();
			 * 
			 * // TODO: Move this into the spring configuration file!
			 * SelectChannelConnector connector =
			 * (SelectChannelConnector)config.
			 * getIdMap().get("SelectChannelConnector");
			 * connector.setPort(Configuration
			 * .getInstance().getAppServerPort());
			 * 
			 * 
			 * server.addLifeCycleListener(new Listener() {
			 * 
			 * @Override public void lifeCycleFailure(LifeCycle arg0, Throwable
			 * arg1) { }
			 * 
			 * @Override public void lifeCycleStarted(LifeCycle arg0) {
			 * System.out.println("server started"); }
			 * 
			 * @Override public void lifeCycleStarting(LifeCycle arg0) { }
			 * 
			 * @Override public void lifeCycleStopped(LifeCycle arg0) { }
			 * 
			 * @Override public void lifeCycleStopping(LifeCycle arg0) { } });
			 * server.start();
			 * 
			 * new Monitor();
			 */

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void parseArguments(String[] args)
	{
		Options options = new Options();

		Option appId = OptionBuilder.withArgName("application id").hasArg().isRequired().create("appId");
		options.addOption(appId);

		Option controlPort = OptionBuilder.withArgName("control port").hasArg().isRequired().create(
				"ctrlPort");
		options.addOption(controlPort);

		Option appServerPort = OptionBuilder.withArgName("appServer port").hasArg().isRequired().create(
				"appSrvPort");
		options.addOption(appServerPort);

		try
		{
			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse(options, args);

			Configuration config = Configuration.getInstance();
			config.setAppId(cmd.getOptionValue("appId"));
			config.setControlPort(new Integer(cmd.getOptionValue("ctrlPort")));
			config.setAppServerPort(new Integer(cmd.getOptionValue("appSrvPort")));

		} catch (ParseException e)
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("AppServer", options);
			System.exit(0);
		} catch (NumberFormatException e)
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("AppServer", options);
			System.exit(0);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		parseArguments(args);

		// TODO: use spring ioc
		new Main();
	}
}
