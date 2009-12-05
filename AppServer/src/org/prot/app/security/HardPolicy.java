package org.prot.app.security;

import java.io.FilePermission;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.PropertyPermission;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public class HardPolicy extends Policy
{
	private static final Logger logger = Logger.getLogger(HardPolicy.class);

	private CodeSource csJava;

	private CodeSource csServer;

	private CodeSource csApp;

	private AsPermissionCollection globalPermission = new AsPermissionCollection();

	private List<ProtectionDomain> pds = new ArrayList<ProtectionDomain>();

	private final URL getJavaUrl() throws MalformedURLException
	{
		Properties props = Configuration.getProperties();

		String javaUrl = props.getProperty("appServer.security.manager.java.url");
		if (javaUrl == null)
		{
			// Autodetecting the java directory
			javaUrl = "file:/" + System.getProperty("java.home") + "/lib/-";
			// Replace all backslashes with slashes
			javaUrl = javaUrl.replace('\\', '/');
			// Replace all whitespaces with %20 (URL)
			javaUrl = javaUrl.replaceAll("\\s", "%20");
		}

		logger.info("Using java url: " + javaUrl);

		return new URL(javaUrl);
	}

	private final String getJavaDir()
	{
		Properties props = Configuration.getProperties();

		String javaDir = props.getProperty("appServer.security.manager.java.dir");
		if (javaDir == null)
		{
			javaDir = System.getProperty("java.home") + "/-";

			// Replace all backslashes with slashes
			javaDir = javaDir.replace('\\', '/');
		}

		logger.info("Using java dir: " + javaDir);

		return javaDir;
	}

	private final String getLibsDir()
	{
		Properties props = Configuration.getProperties();

		String libsDir = props.getProperty("appserver.security.manager.server");
		if (libsDir == null)
		{
			libsDir = System.getProperty("user.dir") + "/../Libs/-";

			// Replace all backslashes with slashes
			libsDir = libsDir.replace('\\', '/');
		}

		logger.info("Using libs dir: " + libsDir);

		return libsDir;
	}

	private final void createCodeSources() throws MalformedURLException
	{
		CodeSigner[] signer = null;

		URL urlJava = getJavaUrl();
		csJava = new CodeSource(urlJava, signer);
		logger.info("CodeSource java: " + urlJava);

		URL urlServer = new URL("file:/D:/work/mscWolke/-");
		csServer = new CodeSource(urlServer, signer);
		logger.info("CodeSource server: " + urlServer);

		String appDir = Configuration.getInstance().getAppDirectory();
		if (appDir.endsWith("/"))
			appDir = appDir.substring(0, appDir.length() - 1);
		URL urlApp = new URL("file:/" + appDir + "/-");
		csApp = new CodeSource(urlApp, signer);
		logger.info("CodeSource app: " + urlApp);
	}

	private final void createGlobalPermissions()
	{
		globalPermission.add(new FilePermission(getJavaDir(), "read"));
		globalPermission.add(new FilePermission(getLibsDir(), "read"));
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
		logger.info("AppDir: " + Configuration.getInstance().getAppDirectory() + "/-");

		appPermissions.add(new FilePermission(Configuration.getInstance().getAppDirectory() + "/-", "read"));
		appPermissions.add(new FilePermission(Configuration.getInstance().getAppScratchDir() + "/-",
				"read,write,execute,delete"));
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

		appPermissions.add(new AllPermission());

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

	public PermissionCollection getPermissions(CodeSource codesource)
	{
		logger.info("get permissions called - unsupported by this policy");
		return globalPermission;
	}

	public PermissionCollection getPermissions(ProtectionDomain domain)
	{
		logger.info("get permissions called - unsupported by this policy");
		return globalPermission;
	}

	public boolean implies(ProtectionDomain domain, Permission permission)
	{
		if (globalPermission.implies(permission))
			return true;

		CodeSource cs = domain.getCodeSource();

		for (ProtectionDomain pd : pds)
		{
			if (pd.getCodeSource().implies(cs))
			{
				if (pd.implies(permission))
					return true;
			}
		}

		logger.debug("Permission not granted: " + permission + " on: " + cs.getLocation());
		return true;
	}
}
