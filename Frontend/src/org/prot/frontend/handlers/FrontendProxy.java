package org.prot.frontend.handlers;

import java.io.IOException;
import java.net.ConnectException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.prot.util.handler.HttpProxyHelper;

public class FrontendProxy extends HttpProxyHelper<HttpServletResponse>
{

	@Override
	protected void expired(HttpServletResponse response)
	{
		try
		{
			response.sendError(HttpStatus.REQUEST_TIMEOUT_408,
					"Connection with the Controller timed out");
		} catch (IOException e1)
		{
			// Do nothing
		}
	}

	protected boolean error(HttpServletResponse response, Throwable e)
	{
		// Frontend could not connect with the Controller
		if (e instanceof ConnectException)
		{
			try
			{
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
						"Frontend could not communicate with the Controller");
			} catch (IOException e1)
			{
				return false;
			}

			return true;
		}

		return false;
	}
}
