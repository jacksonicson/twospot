package org.prot.appserver.python;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.python.core.PyInteger;
import org.python.core.PyIterator;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PySequence;
import org.python.core.PyString;

public class WsgiChannel
{
	private static final long serialVersionUID = -4379580145439172410L;

	// Request & Response
	private Request request;
	private Response response;

	// Input
	private InputStream in;
	private BufferedReader inReader;
	private RequestContentIterator requestContentIterator;

	// Output
	private OutputStream out;

	class RequestContentIterator extends PyIterator
	{
		@Override
		public PyObject __iternext__()
		{
			try
			{
				return new PyString(WsgiChannel.this.inReader.readLine());
			} catch (IOException e)
			{
				return null;
			}
		}
	}

	public WsgiChannel(Request request, Response response)
	{
		this.request = request;
		this.response = response;

		try
		{
			// Create input streams
			in = this.request.getInputStream();
			inReader = new BufferedReader(new InputStreamReader(in));
			requestContentIterator = new RequestContentIterator();

			// Create output streams
			out = this.response.getOutputStream();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public String read(int size) throws IOException
	{
		byte[] buffer = new byte[size];
		int len = in.read(buffer);
		return new String(buffer, 0, len);
	}

	public String readline() throws IOException
	{
		String line = inReader.readLine();
		if (line == null)
			return null;

		return line;
	}

	public PyList readlines(PyInteger sizeHint) throws IOException
	{
		PyList list = new PyList();

		String line = null;
		int counter = 0;
		while ((line = inReader.readLine()) != null && counter++ <= sizeHint.getValue())
			list.add(line);

		return list;
	}

	public PyIterator __iter__()
	{
		return requestContentIterator;
	}

	public void flush() throws IOException
	{
		out.flush();
	}

	public void write(String string) throws IOException
	{
		out.write(string.getBytes());
	}

	public void writelines(PySequence sequence) throws IOException
	{
		for (PyObject str : sequence.asIterable())
		{
			out.write(str.toString().getBytes());
		}
	}

	public void setStatus(String status)
	{
		String[] parts = status.split("\\s");
		switch (parts.length)
		{
		case 1:
			response.setStatus(Integer.parseInt(parts[0]));
			return;
		case 2:
			response.setStatus(Integer.parseInt(parts[0]), parts[1]);
			return;
		}
	}

	public void setHeader(String name, String value)
	{
		response.setHeader(name, value);
	}

	public String getScheme()
	{
		return request.getScheme();
	}
}
