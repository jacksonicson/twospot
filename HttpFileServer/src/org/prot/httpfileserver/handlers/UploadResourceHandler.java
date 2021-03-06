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
package org.prot.httpfileserver.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.resource.Resource;

public class UploadResourceHandler extends AbstractHandler {
	private static final Logger logger = Logger
			.getLogger(UploadResourceHandler.class);

	// Lock parallel writes to the same file
	private List<String> lockList = new Vector<String>();

	// Location where the files are stored
	private Resource baseResource;

	boolean acquireLock(String file) {
		synchronized (lockList) {
			if (lockList.contains(file))
				return false;

			lockList.add(file);
			return true;
		}
	}

	void freeLock(String file) {
		synchronized (lockList) {
			lockList.remove(file);
		}
	}

	public void setResourceBase(String resourceBase) throws IOException {
		baseResource = Resource.newResource(resourceBase);
		if (baseResource == null)
			throw new IOException("Could open resource: " + resourceBase);

		if (!baseResource.exists() || !baseResource.isDirectory())
			baseResource = null;

		logger.info("Using resouce base: " + baseResource);
	}

	File createTempFile(String name) throws IOException {
		File target = new File(baseResource.getFile().getAbsoluteFile() + "/"
				+ "." + name + ".war");
		if (target.exists())
			target.delete();

		logger.debug("Creating temp file: " + target.getAbsolutePath());
		target.createNewFile();

		return target;
	}

	void renameTempFile(File resource, String name) throws IOException {
		File dest = new File(baseResource.getFile().getAbsolutePath() + "/"
				+ name + ".war");
		if (dest.exists())
			dest.delete();

		logger.debug("Renaming temp file to: " + dest.getAbsolutePath());
		resource.renameTo(dest);
	}

	boolean validateAppId(String appId) {
		return true;
	}

	boolean validateVersionInfo(String version) {
		return true;
	}

	boolean validateToken(String token) {
		return true;
	}

	void respondError(Request request, HttpServletResponse response, int error)
			throws IOException {
		response.sendError(error);
		request.setHandled(true);
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// Check if its a POST request
		if (HttpMethods.POST.equalsIgnoreCase(request.getMethod()) == false) {
			logger.debug("Could not upload - use HTTP-POST");
			return;
		}

		// Extract the appId and version
		// URL-format: http://host:port/appId/version/token/*
		String uri = request.getRequestURI();
		String appId = null;
		String version = null;
		String token = null;

		// Remove the first slash of the uri
		if (uri.startsWith("/"))
			uri = uri.substring(1);

		// Check if there is an appId and extract it
		int index = uri.indexOf('/');
		if (index >= 0) {
			appId = uri.substring(0, index);
			uri = uri.substring(index + 1);
		} else {
			logger.debug("missing appId information");
			respondError(baseRequest, response, HttpStatus.BAD_REQUEST_400);
			return;
		}

		// Check if there is a version and extract it
		index = uri.indexOf('/');
		if (index >= 0) {
			version = uri.substring(0, index);
			uri = uri.substring(index + 1);
		} else {
			logger.debug("missing version information");
			respondError(baseRequest, response, HttpStatus.BAD_REQUEST_400);
			return;
		}

		// Only use lower case appIds and versions
		appId = appId.toLowerCase();
		version = version.toLowerCase();

		// Check if there is a token and extract it
		index = uri.indexOf('/');
		if (index >= 0) {
			token = uri.substring(0, index);
			uri = uri.substring(index + 1);
		} else {
			logger.debug("missing token information");
			respondError(baseRequest, response, HttpStatus.BAD_REQUEST_400);
			return;
		}

		// Validate appId, version and token
		if (!validateAppId(appId) || !validateVersionInfo(version)
				|| !validateToken(token)) {
			logger.debug("invalid appId, version info or token");
			respondError(baseRequest, response, HttpStatus.BAD_REQUEST_400);
			return;
		}

		// Filename
		String filename = appId + version;
		logger.debug("Filename: " + filename);

		// Debug
		logger.debug("AppId: " + appId + " Version: " + version + " File: "
				+ filename);

		try {
			// Acquire the file lock
			if (acquireLock(filename) == false) {
				logger.debug("could not aquire file lock");
				respondError(baseRequest, response, HttpStatus.BAD_REQUEST_400);
				return;
			}

			File dest = createTempFile(filename);

			logger.debug("Copying...");
			FileOutputStream destOut = new FileOutputStream(dest);
			IO.copy(baseRequest.getInputStream(), destOut);
			destOut.close();

			renameTempFile(dest, appId + version);

			logger.debug("Upload completed");
			response.getWriter().print("upload done");

		} catch (IOException e) {
			logger.error("Error while uploading a file", e);
			respondError(baseRequest, response,
					HttpStatus.INTERNAL_SERVER_ERROR_500);
			return;
		} finally {
			// Free the file lock
			freeLock(filename);
			baseRequest.setHandled(true);
		}
	}
}
