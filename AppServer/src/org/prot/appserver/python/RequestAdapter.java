package org.prot.appserver.python;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.prot.appserver.Configuration;
import org.python.core.PyDictionary;

public class RequestAdapter
{
	// Data sources
	private Request baseRequest;

	private HttpServletResponse response;

	public RequestAdapter(Request baseRequest, HttpServletRequest request, HttpServletResponse response)
	{
		this.baseRequest = baseRequest;
		this.response = response;
	}

	public String getEnvironment()
	{
		return Configuration.getInstance().getAppId() + ".settings";
	}

	public String getUri()
	{
		return baseRequest.getUri().toString();
	}

	public Map<String, String> getOptions()
	{
		Map<String, String> options = new HashMap<String, String>();
		options.put("django.root", "/");
		return options;
	}

	public void write(byte[] chunk)
	{
		try
		{
			response.getOutputStream().write(chunk);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		try
		{
			response.getOutputStream().close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void setStatus(int status)
	{
		response.setStatus(status);
	}

	public boolean isHttps()
	{
		return baseRequest.isSecure();
	}

	public String getArgs()
	{
		return "";
	}

	public PyDictionary getHeaders()
	{
		PyDictionary dict = new PyDictionary();

		for (Enumeration<String> names = baseRequest.getHeaderNames(); names.hasMoreElements();)
		{
			String name = names.nextElement();
			String value = baseRequest.getHeader(name);
			if (value != null)
			{
				dict.put(name, value);
			}
		}

		return dict;
	}

	public void setHeader(String key, String value)
	{
		response.setHeader(key, value);
	}

	public byte[] read()
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] buffer = new byte[64];
		int len;
		
		try
		{
			InputStream in = baseRequest.getInputStream();
			
			while ((len = in.read(buffer)) > 0)
			{
				bytes.write(buffer, 0, len);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return bytes.toByteArray();
	}

	public String getAuthType()
	{
		return baseRequest.getAuthType();
	}

	public int getContentLength()
	{
		return baseRequest.getContentLength();
	}

	public String getContentType()
	{
		return baseRequest.getContentType();
	}

	public void setContentType(String type)
	{
		response.setContentType(type);
	}

	public String getRemoteIp()
	{
		return baseRequest.getRemoteAddr();
	}

	public String getRemoteHost()
	{
		return baseRequest.getRemoteHost();
	}

	public String getUser()
	{
		return baseRequest.getRemoteUser();
	}

	public String getMethod()
	{
		return baseRequest.getMethod();
	}

	public String getServerHostname()
	{
		return baseRequest.getServerName();
	}

	public int getServerPort()
	{
		return baseRequest.getServerPort();
	}

	public String getProtocol()
	{
		return baseRequest.getProtocol();
	}
}
