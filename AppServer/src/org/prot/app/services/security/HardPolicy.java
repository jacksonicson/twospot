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
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

import org.apache.log4j.Logger;

public class HardPolicy extends Policy
{
	private static final Logger logger = Logger.getLogger(HardPolicy.class);

	private CodeSource csJava;

	private CodeSource csServer;

	private CodeSource csApp;

	private AsPermissionCollection globalPermission = new AsPermissionCollection();

	private List<ProtectionDomain> pds = new ArrayList<ProtectionDomain>();

	private final void createCodeSources() throws MalformedURLException
	{
		CodeSigner[] signer = null;

		URL urlJava = new URL("file:/C:/Program%20Files%20(x86)/Java/jre6/lib/-");
		csJava = new CodeSource(urlJava, signer);
		logger.info("CodeSource java: " + urlJava);

		URL urlServer = new URL("file:/D:/work/mscWolke/-");
		csServer = new CodeSource(urlServer, signer);
		logger.info("CodeSource server: " + urlServer);

		String appDir = "C:/temp/";
		if (appDir.endsWith("/"))
			appDir = appDir.substring(0, appDir.length() - 1);
		URL urlApp = new URL("file:/" + appDir + "/-");
		csApp = new CodeSource(urlApp, signer);
		logger.info("CodeSource app: " + urlApp);
	}

	private final void createGlobalPermissions()
	{
		globalPermission.add(new FilePermission("C:/Program Files(x86)/Java/jdk1.6.0_14/-", "read"));
		globalPermission.add(new FilePermission("D:/work/mscWolke/trunk/dev/Libs/-", "read"));
		globalPermission.add(new FilePermission("D:/work/mscWolke/trunk/dev/HbasePlugin/-", "read"));
	}

	private final void createJavaProtectionDomain()
	{
		AsPermissionCollection javaPermissions = new AsPermissionCollection();

		javaPermissions.add(new AllPermission());

		ProtectionDomain pdJava = new ProtectionDomain(csJava, javaPermissions);
		pds.add(pdJava);
	}
	
	private final void createServerProtectionDomain()
	{
		AsPermissionCollection serverPermissions = new AsPermissionCollection();

		serverPermissions.add(new AllPermission());

		ProtectionDomain pdServer = new ProtectionDomain(csServer, serverPermissions);
		pds.add(pdServer);
	}

	private final void createAppProtectionDomain()
	{
		AsPermissionCollection appPermissions = new AsPermissionCollection();

		// TODO: Critial permissios which should not be granted
		appPermissions.add(new FilePermission("C:/temp/-", "read,write,delete,execute"));
		appPermissions.add(new SocketPermission("*", "connect,resolve"));

		// Generic permissions
		appPermissions.add(new RuntimePermission("getClassLoader"));
		appPermissions.add(new RuntimePermission("createClassLoader"));
		appPermissions.add(new RuntimePermission("setContextClassLoader"));
		appPermissions.add(new RuntimePermission("loadLibrary.*"));
		appPermissions.add(new RuntimePermission("accessClassInPackage.*"));
		appPermissions.add(new RuntimePermission("defineClassInPackage.*"));
		appPermissions.add(new RuntimePermission("accessDeclaredMembers"));
		appPermissions.add(new PropertyPermission("*", "read"));
		
		ProtectionDomain pdApp = new ProtectionDomain(csApp, appPermissions);
		pds.add(pdApp);
	}

	private final void createProtectionDomains()
	{
		createGlobalPermissions();

		createJavaProtectionDomain();
		createAppProtectionDomain();
		createServerProtectionDomain();
	}

	public void refresh()
	{
		try
		{
			createCodeSources();
		} catch (MalformedURLException e)
		{
			logger.error("Malformed URL in the policy", e);
			System.exit(1);
		}

		createProtectionDomains();
	}

	public boolean implies(ProtectionDomain domain, Permission permission)
	{
		if(globalPermission.implies(permission))
			return true; 
		
		CodeSource cs = domain.getCodeSource(); 
		
		for(ProtectionDomain pd : pds)
		{
			if(pd.getCodeSource().implies(cs))
			{
				if(pd.implies(permission))
					return true; 
			}
		}
		
		logger.debug("Permission not granted: " + permission + " on: " + cs.getLocation());
		return false;
	}
}
