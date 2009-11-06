package org.prot.appserver.python;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.python.core.PyInteger;
import org.python.core.PyIterator;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PySequence;
import org.python.core.PyString;

public class WsgIO
{
	private Request request;
	private Response response;

	public WsgIO(Request request, Response response)
	{
		this.request = request;
		this.response = response;
	}

	public PyString read(PyInteger size)
	{
		try
		{
			InputStream in = request.getInputStream();
			byte[] buffer = new byte[size.getValue()];
			int len = in.read(buffer);
			return new PyString(new String(buffer, 0, len));

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public PyString readline()
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = in.readLine();
			if (line == null)
				return null;

			return new PyString(line);

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public PyList readlines(PyInteger sizeHint)
	{
		PyList list = new PyList();
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = null;
			int counter = 0;
			while ((line = in.readLine()) != null && counter <= sizeHint.getValue())
			{
				list.add(line);
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return list;
	}

	class TestIter extends PyIterator
	{

		BufferedReader in;

		public TestIter(BufferedReader in)
		{
			this.in = in;
		}

		@Override
		public PyObject __iternext__()
		{
			String line = null;
			try
			{
				line = in.readLine();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			if (line == null)
				return null;

			return new PyString(line);
		}
	}

	TestIter iter;

	public PyIterator __iter__()
	{
		if (iter == null)
		{
			BufferedReader in;
			try
			{
				in = new BufferedReader(new InputStreamReader(request.getInputStream()));
				iter = new TestIter(in);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return iter;
	}

	public void flush()
	{
		try
		{
			response.getOutputStream().flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void write(PyString string)
	{
		try
		{
			response.getOutputStream().write(string.toBytes());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void writelines(PySequence sequence)
	{
		try
		{
			for (PyObject str : sequence.asIterable())
			{
				response.getOutputStream().write(str.toString().getBytes());
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setStatus(int status)
	{
		response.setStatus(status); 
	}

	public void setHeader(String name, String value)
	{
		response.setHeader(name, value);
	}
	
	public String getScheme() {
		return request.getScheme(); 
	}
}
