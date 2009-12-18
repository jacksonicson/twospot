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
import org.prot.controller.app.TokenChecker;
import org.prot.controller.config.Configuration;
import org.prot.controller.zookeeper.ManagementService;

public class DeployServiceImpl implements DeployService
{
	private static final Logger logger = Logger.getLogger(DeployServiceImpl.class);

	private TokenChecker tokenChecker;

	private ManagementService managementService;

	@Override
	public boolean register(String token, String appId, String version)
	{
		// Check the token
		if (tokenChecker.checkToken(token) == false)
			return false;

		return managementService.registerApp(appId);
	}

	@Override
	public String announceDeploy(String token, String appId, String version)
	{
		// Check the token
		if (tokenChecker.checkToken(token) == false)
			return null;

		logger.debug("Announcing deployment");
		try
		{
			Configuration config = Configuration.getConfiguration();
			String fileServerUrl = config.getFileServerURL() + "/announce";
			logger.debug("Using FileServer URL: " + fileServerUrl);

			URL url = new URL(fileServerUrl);
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
			logger.error("Could not aquire an upload token", e);
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
		if (tokenChecker.checkToken(token) == false)
			return;

		// Update ZooKeeper data
		managementService.deployApp(appId, version);
	}

	public void setTokenChecker(TokenChecker tokenChecker)
	{
		this.tokenChecker = tokenChecker;
	}

	public void setManagementService(ManagementService managementService)
	{
		this.managementService = managementService;
	}
}
