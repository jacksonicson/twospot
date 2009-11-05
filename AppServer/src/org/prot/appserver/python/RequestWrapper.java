package org.prot.appserver.python;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.python.core.PyDictionary;

public class RequestWrapper
{
	// Data source
	private Request baseRequest;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public RequestWrapper(Request baseRequest, HttpServletRequest request, HttpServletResponse response)
	{
		this.baseRequest = baseRequest;
		this.request = request;
		this.response = response;
	}
	
	public PyDictionary getEnvironment() {
		PyDictionary dict = new PyDictionary();
		// dict.put("PYTHONPATH", "C:/jython2.5.1/Lib/site-packages/;C:/jython2.5.1/Lib/");
		// dict.put("JYTHONPATH", "C:/jython2.5.1/Lib/site-packages/;C:/jython2.5.1/Lib/;D:/work/django/blub");
		dict.put("DJANGO_SETTINGS_MODULE", "D:/work/django/blub/");
		return dict; 
	}

	public String getUri()
	{
		return baseRequest.getUri().toString();
	}

	public Map<String, String> getOptions()
	{
		Map<String, String> options = new HashMap<String, String>();
		options.put("django.root", "todo");
		return options;
	}
	
	public void write(byte[] chunk) {
		try
		{
			response.getOutputStream().write(chunk);
		} catch (IOException e)
		{
			e.printStackTrace();
		} 
	}
	
	public void close() {
		try
		{
			response.getOutputStream().close();
		} catch (IOException e)
		{
			e.printStackTrace();
		} 
	}

	public void setStatus(int status) {
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
		
		for(Enumeration<String> names = baseRequest.getHeaderNames(); names.hasMoreElements(); ) {
			String name = names.nextElement();
			String value = baseRequest.getHeader(name);
			if(value != null)
			{
				System.out.println("Name: " + name + " value: " + value); 
				dict.put(name, value); 
			}
		}
		
		return dict; 
	}
	
	public void setHeader(String key, String value) {
		response.setHeader(key, value); 
	}

	public String read()
	{
		return "asdf"; 
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
