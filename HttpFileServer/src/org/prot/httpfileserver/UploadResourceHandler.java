package org.prot.httpfileserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;

public class UploadResourceHandler extends AbstractHandler
{
	String createFile(String name) throws IOException
	{
		File tmpFile = new File("./files/__" + name);
		if (tmpFile.exists())
			tmpFile.delete();

		return tmpFile.getAbsolutePath();
	}

	void renameFile(String name)
	{
		File tmpFile = new File("./files/__" + name);
		File overwrite = new File("./files/" + name);
		if(overwrite.exists())
			overwrite.delete(); 
		
		if (tmpFile.exists())
			tmpFile.renameTo(new File("./files/" + name));
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		System.out.println("POST");

		if (HttpMethods.POST.equalsIgnoreCase(request.getMethod()) == false)
		{
			response.sendError(404);
			return;
		}

		String path_info = request.getPathInfo();
		String[] components = path_info.split("/");

		if (components.length == 3 && components[1].equals("app"))
		{
			String appId = components[2];
			String fileName = appId + ".war";

			String file = createFile(fileName);
			try
			{
				System.out.println("opening streams and reading file contents");

				System.out.println("Content length: " + baseRequest.getContentLength());

				FileOutputStream out = new FileOutputStream(file);
				InputStream in = baseRequest.getInputStream();

				IO.copy(in, out);

				out.close();
				in.close();

				renameFile(fileName);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		baseRequest.setHandled(true);
	}
}
