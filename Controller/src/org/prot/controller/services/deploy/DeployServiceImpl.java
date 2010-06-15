/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
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
import org.prot.controller.services.gen.Services.AnnounceDeployment;
import org.prot.controller.services.gen.Services.AppDeployed;
import org.prot.controller.services.gen.Services.Boolean;
import org.prot.controller.services.gen.Services.DeployService;
import org.prot.controller.services.gen.Services.RegisterDeployment;
import org.prot.controller.services.gen.Services.Void;
import org.prot.controller.zookeeper.SynchronizationService;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class DeployServiceImpl extends DeployService {
	private static final Logger logger = Logger.getLogger(DeployServiceImpl.class);

	private TokenChecker tokenChecker;

	private SynchronizationService managementService;

	
	@Override
	public void announceDeploy(RpcController controller, AnnounceDeployment request,
			RpcCallback<org.prot.controller.services.gen.Services.String> done) {
		// Check the token
		// if (tokenChecker.checkToken(token) == false)
		// return null;

		logger.debug("Announcing deployment");
		try {
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

			org.prot.controller.services.gen.Services.String.Builder builder = org.prot.controller.services.gen.Services.String
					.newBuilder();
			builder.setValue(deployToken);
			done.run(builder.build());

		} catch (MalformedURLException e) {
			logger.error("Could not aquire an upload token", e);
		} catch (IOException e) {
			logger.error("Connection with the FileServer failed", e);
		}
	}

	@Override
	public void appDeployed(RpcController controller, AppDeployed request, RpcCallback<Void> done) {
		// Check the token
		// if (tokenChecker.checkToken(token) == false)
		// return;

		// Update ZooKeeper data
		managementService.deployApp(request.getAppId(), request.getVersion());
	}

	@Override
	public void register(RpcController controller, RegisterDeployment request, RpcCallback<Boolean> done) {
		// Check the token
		// if (tokenChecker.checkToken(token) == false)
		// return false;

		boolean success = managementService.registerApp(request.getAppId());
		Boolean.Builder builder = Boolean.newBuilder();
		done.run(builder.build());
	}

	public void setTokenChecker(TokenChecker tokenChecker) {
		this.tokenChecker = tokenChecker;
	}

	public void setManagementService(SynchronizationService managementService) {
		this.managementService = managementService;
	}
}
