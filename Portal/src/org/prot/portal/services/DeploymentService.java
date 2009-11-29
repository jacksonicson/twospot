package org.prot.portal.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class DeploymentService
{
	private static final Logger logger = Logger.getLogger(DeploymentService.class);

	public int deployApplication(String appId, String version, InputStream in) throws Exception
	{
		URL url = new URL("http://localhost:5050/" + appId + "/" + version);
		URLConnection urlCon = url.openConnection();
		HttpURLConnection httpCon = (HttpURLConnection) urlCon;
		httpCon.setDoOutput(true);

		// Stream the WAR-File to the fileserver
		OutputStream out = httpCon.getOutputStream();
		byte[] buffer = new byte[64];
		int len = 0;
		long sum = 0;
		final long MAX_SIZE = 25 * 1024 * 1024;
		while ((len = in.read(buffer)) > 0)
		{
			out.write(buffer, 0, len);
			sum += len;

			if (len > MAX_SIZE)
			{
				// TODO: Throw an exception
				logger.warn("Upload exceeds maximum file size - stopping transfer");
				return 505;
			}
		}
		out.close();

		// Read the fileserver response
		logger.info("reading from fileserver now"); 
		BufferedReader httpIn = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
		String line = ""; 
		boolean ok = false; 
		while((line = httpIn.readLine()) != null)
		{
			logger.debug("Reading from fileserver: " + line);
			if(ok = line.equals("upload done"))
				break; 
		}
		httpIn.close();

		if(ok)
			return 200; 
		
		return 505; 
	}
}
