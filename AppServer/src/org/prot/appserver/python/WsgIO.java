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

public class WsgIO
{
	private Request request;
	private Response response;

	private InputStream in;
	private BufferedReader inReader;
	private OutputStream out; 

	public WsgIO(Request request, Response response)
	{
		this.request = request;
		this.response = response;

		try
		{
			in = this.request.getInputStream();
			inReader = new BufferedReader(new InputStreamReader(in));
			
			out = this.response.getOutputStream(); 
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public String read(int size)
	{
		try
		{
			byte[] buffer = new byte[size];
			int len = in.read(buffer);
			return new String(buffer, 0, len);

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public String readline()
	{
		try
		{
			String line = inReader.readLine();
			if (line == null)
				return null;

			return line;

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
			String line = null;
			int counter = 0;
			while ((line = inReader.readLine()) != null && counter <= sizeHint.getValue())
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
			iter = new TestIter(inReader);
		}

		return iter;
	}

	public void flush()
	{
		try
		{
			out.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void write(String string)
	{
		try
		{
			out.write(string.getBytes());
		} catch (IOException e)
		{
			// e.printStackTrace();
		}
	}

	public void writelines(PySequence sequence)
	{
		try
		{
			for (PyObject str : sequence.asIterable())
			{
				out.write(str.toString().getBytes());
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

	public String getScheme()
	{
		return request.getScheme();
	}
}
