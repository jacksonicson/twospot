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
package guestbook;

import guestbook.data.GuestEntry;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CreateEntry extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		PersistenceManager manager = DataConnection.getManager();

		GuestEntry entry = new GuestEntry();
		entry.setName(request.getParameter("name"));
		entry.setMessage(request.getParameter("message"));
		entry.setTimestamp(System.currentTimeMillis());
		manager.makePersistent(entry);

		manager.close();

		response.sendRedirect("/guestbook");
	}
}
