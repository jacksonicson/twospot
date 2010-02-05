package org.prot.portal.loadtest;

import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LoadTestController implements Controller
{
	private static final int length = 1024 * 512;
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
		ByteArrayOutputStream bout = new ByteArrayOutputStream(length);
		ZipOutputStream out = new ZipOutputStream(bout);
		out.putNextEntry(new ZipEntry("test"));
		out.write(data);
		out.close();

		response.getOutputStream().write(bout.toByteArray());

		return null;
	}

}
