package org.prot.app.services.security;

import java.io.FilePermission;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.PropertyPermission;

import org.apache.log4j.Logger;

public class HardPolicy extends Policy
{
	private static final Logger logger = Logger.getLogger(HardPolicy.class);

	class MyCol extends PermissionCollection
	{
		private List<Permission> permissions = new ArrayList<Permission>();

		@Override
		public void add(Permission permission)
		{
			permissions.add(permission);
		}

		@Override
		public Enumeration<Permission> elements()
		{
			return Collections.enumeration(permissions);
		}

		@Override
		public boolean implies(Permission permission)
		{
			if (permission.getClass().equals(RuntimePermission.class))
			{
				System.out.println("permission: " + permission.getActions());
				return true;
			}

			if (permission.getClass().equals(PropertyPermission.class))
				return true;

			if (permission.getClass().equals(FilePermission.class))
				return true;

			
			if(permission instanceof SocketPermission)
			{
				System.out.println("permission: " + permission.getActions());
				return false;
			}
			
			/*
			 * AllPermission perm = new AllPermission();
			 * if(perm.implies(permission)) return true;
			 */

			return false;
		}

	}

	private MyCol myCol = new MyCol();

	public PermissionCollection getPermissions(ProtectionDomain domain)
	{
		System.out.println("get permissions from domain: " + domain.getPermissions());
		return myCol;
	}

	public PermissionCollection getPermissions(CodeSource codesource)
	{
		System.out.println("get permissions from code source: " + codesource);
		return myCol;
	}

	public boolean implies(ProtectionDomain domain, Permission permission)
	{
		try
		{
			URL url = new URL("file:/D:/-");
			CodeSource test = domain.getCodeSource();
			CodeSource mine = new CodeSource(url, (CodeSigner[]) null);

			// Alles was von der code source kommt ist mal sicher
			boolean implies = mine.implies(test);
			if (implies)
			{
				if(permission instanceof RuntimePermission)
				{
//					System.out.println("Ok codesource: " + domain.getCodeSource().getLocation());
//					System.out.println("Permission: " + permission);
				}				
//				System.out.println("Permission: " + permission);
//				
				return true;
			}

		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

//		System.out.println("Failed codesource: " + domain.getCodeSource().getLocation());

		boolean check = myCol.implies(permission);
		if (check == false)
		{
			System.out.println("Codesource: " + domain.getCodeSource());
			System.out.println("Security permission: " + permission);
			System.out.println("Classloader: " + domain.getClassLoader());

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
			
			System.out.println("Callstack: " +sb.toString());

		}

		return check;
	}

	public void refresh()
	{
		System.out.println("Refresh");
	}
}
