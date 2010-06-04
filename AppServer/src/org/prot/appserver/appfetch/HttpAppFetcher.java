package org.prot.appserver.appfetch;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.config.Configuration;

public class HttpAppFetcher implements AppFetcher {
	private static final Logger logger = Logger.getLogger(HttpAppFetcher.class);

	private HttpClient httpClient;

	private String url;

	private void startHttp() {
		try {
			httpClient = new HttpClient();
			httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
			httpClient.start();
		} catch (Exception e) {
			logger.error("Could not start the http client", e);
			System.exit(1);
		}
	}

	private void stopHttp() {
		try {
			httpClient.stop();
		} catch (Exception e) {
			logger.error("Could not stop the http client", e);
			System.exit(1);
		}
	}

	@Override
	public AppInfo fetchApp(String appId) {
		AppInfo appInfo = new AppInfo();

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			String completeUrl = url + appId;
			URL url = new URL(completeUrl);
			InputStream in = url.openStream();
			BufferedInputStream bufIn = new BufferedInputStream(in);
			while (true) {
				int data = bufIn.read();
				if (data == -1)
					break;

				out.write(data);
			}

			appInfo.setWarFile(out.toByteArray());
			appInfo.setAppId(Configuration.getInstance().getAppId());

		} catch (Exception e) {
			logger.error(e);
		}
		/*
		 * 
		 * ContentExchange exchange = new ContentExchange(true) { protected void
		 * onConnectionFailed(Throwable x) { super.onConnectionFailed(x);
		 * logger.error("Connection failed while downloading WAR archive", x);
		 * System.exit(1); }
		 * 
		 * protected void onException(Throwable x) { super.onException(x);
		 * logger.error("Error while downloading WAR archive", x);
		 * System.exit(1); } };
		 * 
		 * exchange.setMethod("GET"); String completeUrl = url + appId;
		 * exchange.setURL(completeUrl);
		 * logger.info("Loading application from: " + completeUrl);
		 * 
		 * try { startHttp();
		 * 
		 * httpClient.send(exchange); exchange.waitForDone();
		 * 
		 * // Check status int status = exchange.getResponseStatus(); if (status
		 * != HttpStatus.OK_200) {
		 * logger.error("Fileserver did return error status: " + status); return
		 * null; }
		 * 
		 * // Check size (applications cannot be 0 bytes) if
		 * (exchange.getResponseContentBytes().length == 0) {
		 * logger.error("Fileserver returned an empty archive"); return null; }
		 * 
		 * appInfo.setWarFile(exchange.getResponseContentBytes());
		 * appInfo.setAppId(Configuration.getInstance().getAppId());
		 * 
		 * stopHttp();
		 * 
		 * } catch (InterruptedException e) {
		 * logger.error("Interrupted while downloading WAR archive", e);
		 * System.exit(1); } catch (IOException e) {
		 * logger.error("IOException while downloading WAR archive", e);
		 * System.exit(1); }
		 */
		return appInfo;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
