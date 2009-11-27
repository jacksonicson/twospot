package org.prot.appserver.runtime.java;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

public class ErrorHandler extends org.eclipse.jetty.server.handler.ErrorHandler
{
	private void printStackTrace()
	{
		StringBuilder sb = new StringBuilder();

		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

		ArrayList<String> list = new ArrayList<String>();
		String[] array = new String[1];

		for (int i = 0; i < stackTraceElements.length; i++)
		{
			StackTraceElement element = stackTraceElements[i];
			String classname = element.getClassName();
			String methodName = element.getMethodName();
			int lineNumber = element.getLineNumber();
			list.add(classname + "." + methodName + ":" + lineNumber);
		}

		array = list.toArray(array);

		for (int i = 0; i < array.length; i++)
		{
			sb.append(array[i] + "\n");
		}

		System.out.println("Stack: " + sb);
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		super.handle(target, baseRequest, request, response);
	}
}