package org.prot.controller.services.deploy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.prot.controller.config.Configuration;
import org.prot.controller.management.AppServerWatcher;
import org.prot.controller.manager.AppManager;

public class DeployServiceImpl implements DeployService
{
	private static final Logger logger = Logger.getLogger(DeployServiceImpl.class);

	private AppManager appManager;

	private AppServerWatcher management;

	@Override
	public String announceDeploy(String token, String appId, String version)
	{
		// Check the token
		if (appManager.checkToken(token) == false)
			return null;

		logger.debug("Announcing deployment");
		try
		{
			Configuration config = Configuration.getConfiguration();
			URL url = new URL(config + "/announce");
			URLConnection urlCon = url.openConnection();
			HttpURLConnection httpCon = (HttpURLConnection) urlCon;
			httpCon.setDoInput(true);

			InputStream in = httpCon.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String deployToken = reader.readLine();

			logger.debug("Deployment token: " + deployToken);

			return deployToken;

		} catch (MalformedURLException e)
		{
			logger.error("Could genearte an upload token", e);
			return null;
		} catch (IOException e)
		{
			logger.error("Connection with the FileServer failed", e);
			return null;
		}
	}

	@Override
	public void appDeployed(String token, String appId, String version)
	{
		// Check the token
		if (appManager.checkToken(token) == false)
			return;

		// Store the info in the management component
		management.notifyDeployment(appId);
	}

	public void setManagement(AppServerWatcher management)
	{
		this.management = management;
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}
}
