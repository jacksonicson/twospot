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
package org.prot.app.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

public class DosPreventionHandler extends HandlerWrapper
{
	private static final Logger logger = Logger.getLogger(DosPreventionHandler.class);
	
	private DosPrevention dosPrevention;

	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		long requestId = dosPrevention.startRequest();
		try
		{
			super.handle(target, baseRequest, request, response);
		} finally
		{
			dosPrevention.finishRequest(requestId);
		}

	}

	public void setDosPrevention(DosPrevention dosPrevention)
	{
		this.dosPrevention = dosPrevention;
	}
}
