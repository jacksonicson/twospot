package org.prot.frontend.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.prot.frontend.cache.AppCache;
import org.prot.manager.stats.ControllerInfo;
import org.prot.util.AppIdExtractor;
import org.prot.util.ReservedAppIds;

public class ProxyHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(ProxyHandler.class);

	private AppCache appCache;

	private FrontendProxy frontendProxy;

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{

		// Extract the AppId from the url
		String appId = AppIdExtractor.fromDomain(request.getRequestURL().toString());

		if (appId == null)
		{
			// Error: Missing AppId
			response.sendError(HttpStatus.NOT_FOUND_404, "Missing AppId (scheme://AppId.domain...)");
			baseRequest.setHandled(true);
			return;
		}

		if (appId.equals(ReservedAppIds.FRONTEND_DEPLOY))
			handleDeploy(baseRequest, request, response);
		else
			handleApp(appId, target, baseRequest, request, response);
	}

	private void handleDeploy(Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		logger.debug("Frontend handles deployment: " + baseRequest.getUri());

		URL url = new URL("http://localhost:5050" + baseRequest.getUri());
		URLConnection urlCon = url.openConnection();
		HttpURLConnection httpCon = (HttpURLConnection) urlCon;
		httpCon.setDoOutput(true);

		// Stream the WAR-File to the fileserver
		InputStream in = request.getInputStream();
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
				logger.warn("Upload exceeds maximum file size - stopping transfer");
				baseRequest.setHandled(true);
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
				return;
			}
		}
		out.close();
		logger.debug("Bytes sent to FileServer: " + sum);

		// Read the fileserver response
		logger.info("reading from fileserver now...");
		BufferedReader httpIn = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
		String line = "";
		boolean ok = false;
		while ((line = httpIn.readLine()) != null)
		{
			logger.debug("Reading from fileserver: " + line);
			if (ok = line.equals("upload done"))
				break;
		}
		httpIn.close();

		if (ok)
		{
			logger.info("Fileserver did not return Ok");
			baseRequest.setHandled(true);
			response.setStatus(HttpStatus.OK_200);
			return;
		}

		baseRequest.setHandled(true);
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		return;
	}

	private void handleApp(String appId, String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		try
		{
			// Check if the cache holds a controller for this app
			ControllerInfo info = appCache.getController(appId);
			if (info == null)
			{
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
						"Manager unreachable or did not return a Controller.");
				baseRequest.setHandled(true);
				return;
			}

			// Build the destination url
			String address = info.getAddress();
			String uri = baseRequest.getUri().toString();
			StringBuilder builder = new StringBuilder(5 + 3 + address.length() + 1 + 4 + 1 + 10
					+ uri.length() + 10);
			builder.append(request.getScheme());
			builder.append("://");
			builder.append(address);
			builder.append(":");
			builder.append(info.getPort());
			builder.append("/");
			builder.append(appId);
			builder.append(uri);

			// Create the URI
			HttpURI httpUri = new HttpURI(builder.toString());

			// Forward the request
			frontendProxy.forwardRequest(baseRequest, request, response, httpUri, response);

		} catch (Exception e)
		{
			logger.error("Error while processing the request", e);

			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500);

			baseRequest.setHandled(true);
			return;
		}
	}

	public void setAppCache(AppCache appCache)
	{
		this.appCache = appCache;
	}

	public void setFrontendProxy(FrontendProxy frontendProxy)
	{
		this.frontendProxy = frontendProxy;
	}
}
