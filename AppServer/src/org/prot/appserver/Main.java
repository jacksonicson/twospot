package org.prot.appserver;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.Container.Relationship;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.appfetch.HttpAppFetcher;
import org.prot.appserver.appfetch.WarLoader;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main implements Listener
{
	private void startJava(AppInfo info) throws Exception
	{
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring/spring_java.xml",
				getClass()));

		// Configure server port
		SelectChannelConnector connector = (SelectChannelConnector) factory.getBean("Connector");
		connector.setPort(Configuration.getInstance().getAppServerPort());

		// Deployer
		AppDeployer deployer = (AppDeployer) factory.getBean("AppDeployer");

		// Start server
		Server server = (Server) factory.getBean("Server");
		server.addBean(deployer);
		server.addLifeCycleListener(this);
		server.start();
	}

	private void startPython(AppInfo info) throws Exception
	{
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring/spring_python.xml",
				getClass()));

		// Configure server port
		SelectChannelConnector connector = (SelectChannelConnector) factory.getBean("Connector");
		connector.setPort(Configuration.getInstance().getAppServerPort());

		// Start server
		Server server = (Server) factory.getBean("Server");
		server.addLifeCycleListener(this);
		server.start();
	}

	public Main() throws UnknownRuntimeException, Exception
	{
		HttpAppFetcher fetcher = new HttpAppFetcher();
		AppInfo appInfo = fetcher.fetchApp(Configuration.getInstance().getAppId());

		WarLoader loader = new WarLoader();
		loader.handle(appInfo);

		// TODO
		appInfo.setRuntime(AppRuntime.PYTHON);

		switch (appInfo.getRuntime())
		{
		case JAVA:
			startJava(appInfo);
			break;
		case PYTHON:
			startPython(appInfo);
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

		try
		{
			new Main();
		} catch (UnknownRuntimeException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void lifeCycleFailure(LifeCycle arg0, Throwable arg1)
	{
	}

	@Override
	public void lifeCycleStarted(LifeCycle arg0)
	{
		System.out.println("server started");
	}

	@Override
	public void lifeCycleStarting(LifeCycle arg0)
	{
	}

	@Override
	public void lifeCycleStopped(LifeCycle arg0)
	{
	}

	@Override
	public void lifeCycleStopping(LifeCycle arg0)
	{
	}
}
