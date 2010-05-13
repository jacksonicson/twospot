package org.prot.portal.loadtest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LoadTestController implements Controller
{
	private static final int length = 1024 * 1024;
	private static byte[] data = new byte[length];

	static
	{
		Random r = new Random();
		r.nextBytes(data);
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		ZipOutputStream out = new ZipOutputStream(new OutputStream()
		{
			@Override
			public void write(int b) throws IOException
			{
			}
		});
		out.putNextEntry(new ZipEntry("test"));
		out.write(data);
		out.close();

		return null;
	}

}
