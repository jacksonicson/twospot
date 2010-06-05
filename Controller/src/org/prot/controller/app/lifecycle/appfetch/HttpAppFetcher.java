package org.prot.controller.app.lifecycle.appfetch;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;

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
	public byte[] fetchApp(String appId) {
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

			return out.toByteArray();
		} catch (Exception e) {
			logger.error(e);
		}

		return null;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
