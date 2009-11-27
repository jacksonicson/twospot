package org.prot.app.services.security;

import java.io.FilePermission;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.PropertyPermission;
import java.util.Set;

import org.apache.log4j.Logger;

public class HardPolicy extends Policy
{
	private static final Logger logger = Logger.getLogger(HardPolicy.class);

	private AsPermissionCollection javaPermissions = new AsPermissionCollection();

	private AsPermissionCollection appPermissions = new AsPermissionCollection();

	private AsPermissionCollection serverPermissions = new AsPermissionCollection();

	private Set<CodeSource> javaSources = new HashSet<CodeSource>();

	private Set<CodeSource> serverSources = new HashSet<CodeSource>();

	private Set<CodeSource> appSource = new HashSet<CodeSource>();

	public void refresh()
	{
		String extDirs = "C:/Program Files (x86)/Java/jre6/lib/ext/-";
		try
		{
			URL url = new URL("file:/" + extDirs);
			javaSources.add(new CodeSource(url, (CodeSigner[]) null));
		} catch (MalformedURLException e)
		{
			System.err.println("Could not set permissions"); 
			System.exit(1);
		}
		
		// Set the permissions
		javaPermissions.add(new AllPermission());
	}

	public void activateApplication(String dir) throws MalformedURLException
	{

		// Activate the codesource
		URL url = new URL("file:/" + dir);
		logger.info("Activating application codesource: " + url);

		appSource.add(new CodeSource(url, (CodeSigner[]) null));

		// Activate the permissions
		appPermissions.add(new FilePermission(dir, "read"));

		appPermissions.add(new RuntimePermission("getClassLoader"));
		appPermissions.add(new RuntimePermission("createClassLoader"));
		appPermissions.add(new RuntimePermission("setContextClassLoader"));
		appPermissions.add(new RuntimePermission("loadLibrary.*"));
		appPermissions.add(new RuntimePermission("accessClassInPackage.*"));
		appPermissions.add(new RuntimePermission("defineClassInPackage.*"));
		appPermissions.add(new RuntimePermission("accessDeclareMembers"));

		appPermissions.add(new PropertyPermission("*", "read"));

		// TODO: DataNucleus still does not work without this (Hadoop
		// connectors)
		appPermissions.add(new SocketPermission("*", "accept,connect,listen,resolve"));
	}

	public void activateServer(List<String> dirs) throws MalformedURLException
	{
		for (String dir : dirs)
		{
			URL url = new URL("file:/" + dir);
			logger.info("Activating server codesource: " + url);

			serverSources.add(new CodeSource(url, (CodeSigner[]) null));

			// Set the server permissions
			serverPermissions.add(new AllPermission());
		}
	}

	private boolean checkServerPermissions(ProtectionDomain domain, Permission permission)
	{
		return serverPermissions.implies(permission);
	}

	private boolean checkAppPermissions(ProtectionDomain domain, Permission permission)
	{
		return appPermissions.implies(permission);
	}
	
	private boolean checkJavaPermissions(ProtectionDomain domain, Permission permission)
	{
		return javaPermissions.implies(permission);
	}

	public boolean implies(ProtectionDomain domain, Permission permission)
	{
		// Check the codesource
		CodeSource cs = domain.getCodeSource();

		// Java permissions
		for(CodeSource java : javaSources)
		{
			if(java.implies(cs))
			{
				boolean check = checkJavaPermissions(domain, permission);
				
				if(check == false)
					logger.debug("java refused: " + permission);
				
				return check;
			}
		}
		
		// Server permissions
		for (CodeSource server : serverSources)
		{
			if (server.implies(cs))
			{
				boolean check = checkServerPermissions(domain, permission);

				if (check == false)
					logger.debug("server refused: " + permission);

				return check;
			}
		}

		// Application permissions
		for (CodeSource app : appSource)
		{
			if (app.implies(cs))
			{
				boolean check = checkAppPermissions(domain, permission);

				if (check == false)
					logger.debug("app refused: " + permission);

				return check;
			}
		}

		// Did not find any matching permissions (this is risky)
		logger.debug("fallthrough: " + cs.getLocation());
		return true;
	}
}
