package org.prot.app.security;

import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
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
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.PropertyPermission;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public class HardPolicy extends Policy
{
	private static final Logger logger = Logger.getLogger(HardPolicy.class);

	// If debug all permissions are granted!
	private static final boolean DEBUG = false;

	// Codesource of the java libs (rt.jar)
	private Set<CodeSource> csJava = new HashSet<CodeSource>();

	// Codesource of the server files (server libs like appserver or controller)
	private CodeSource csServer;

	// Codesource of the application files
	private CodeSource csApp;

	// Codesource of the scratchdir
	private CodeSource csScratch;

	// Global permissions
	private AsPermissionCollection globalPermission = new AsPermissionCollection();

	// Protection domains for different codesourcess
	private List<ProtectionDomain> pds = new ArrayList<ProtectionDomain>();

	/**
	 * The AppServer internally uses slashes as file seperator signs. For the
	 * FilePermission the system file seperator has to bee used. This method
	 * swaps all slash signs with the seperator from the system property
	 * file.seperator.
	 * 
	 * @param path
	 *            a appserver intern path (with slashes)
	 * @return a system path
	 */
	private final String swapSeparator(String path)
	{
		char separator = System.getProperty("file.separator").toCharArray()[0];
		path = path.replace('/', separator);
		return path;
	}

	/**
	 * Pepares a directory for use as a CodeSource URL. The CodeSource URL has
	 * the form: file:/directory The method guarantees:
	 * <ul>
	 * <li>There is only one slash after the scheme file:</li>
	 * <li>All slashes before the URL ending "/-" are removed</li>
	 * </ul>
	 * 
	 * @param directory
	 *            path to the directory which should be in the CodeSource
	 * @return a CodeSource URL which includes the given directory and all
	 *         subdirectorys (recursively)
	 * @throws MalformedURLException
	 */
	private final String prepareUrl(String directory) throws MalformedURLException
	{
		// Replace all backslashes with slashes
		directory = directory.replace('\\', '/');

		// Remove the fist slash if it exists
		if (directory.startsWith("/"))
			directory = directory.substring(1);

		// Remove the last slash if it exists
		if (directory.endsWith("/"))
			directory = directory.substring(0, directory.length() - 1);

		// Build the URL
		directory = "file:/" + directory + "/-";

		// Replace all whitespaces with %20
		directory = directory.replaceAll("\\s", "%20");

		return directory;
	}

	/**
	 * Prepares a directory for use in a FilePermission definition. The method
	 * guarantees:
	 * <ul>
	 * <li>The returned directory only consists of slashes (no backslashes)</li>
	 * <li>The returned directory does *not* end with a slash or backslash</li>
	 * </ul>
	 * 
	 * @param directory
	 *            path to the directory to which permissions should be granted
	 * @return a clean directory path
	 */
	private final String prepareDir(String directory)
	{
		// Replace all backslashes with slashes
		directory = directory.replace('\\', '/');

		// Remove trailing slash
		if (directory.endsWith("/"))
			directory = directory.substring(0, directory.length() - 1);

		return directory;
	}

	/**
	 * Returns a CodeSource URL which includes the Java directory. The method
	 * tries to read the path to the Java directory from the configuration. If
	 * that fails the CodeSource URL is by using the <code>getJdkUrl()</code>
	 * method.
	 * 
	 * @see HardPolicy#getJdkUrl() getJdkUrl
	 * @return CodeSource URL which implies the Java libs
	 * @throws MalformedURLException
	 */
	private final URL getJavaUrl() throws MalformedURLException
	{
		// Load properties
		Properties props = Configuration.getProperties();
		String javaUrl = props.getProperty("appServer.security.manager.java.url");

		// If the CodeSource is not configured in the properties - autodetect it
		if (javaUrl == null)
			return getJdkUrl();

		logger.info("Determined Java URL: " + javaUrl);

		return new URL(javaUrl);
	}

	/**
	 * Returns a CodeSource URL which includes the JDK directory. The method
	 * uses the <code>java.home</code> system variable to get the directory
	 * path.
	 * 
	 * @return CodeSource URL which implies the JDK libs
	 * @throws MalformedURLException
	 */
	private final URL getJdkUrl() throws MalformedURLException
	{
		logger.info("Autoconfigure JDK URL");

		// Get the java home directory (JRE)
		String javaHome = System.getProperty("java.home");
		javaHome = prepareUrl(javaHome);

		logger.info("Determined JDK (in addition to java) URL: " + javaHome);

		return new URL(javaHome);
	}

	/**
	 * Returns the a directory path to the Java directory. The method tries to
	 * read the path from the configuration file. If that fails it uses the
	 * <code>getJdkDir()</code> method to determine the directory path.
	 * 
	 * @see HardPolicy#getJdkDir() getJdkDir
	 * @return directory path to the Java directory
	 */
	private final String getJavaDir()
	{
		// Load the java dir from the propertiess
		Properties props = Configuration.getProperties();
		String javaDir = props.getProperty("appServer.security.manager.java.dir");

		// If java dir is not configured - autodetect it
		if (javaDir == null)
			javaDir = getJdkDir();

		logger.info("Determined java DIR: " + javaDir);

		return javaDir;
	}

	/**
	 * Returns a directory path to the JDK directory. The method reads the
	 * directory path from the <code>java.home</code> system variable.
	 * 
	 * @return a directory path to the JDK directory
	 */
	private final String getJdkDir()
	{
		logger.info("Autoconfigure JDK DIR");

		// Get the java home directory (JDK)
		String javaDir = System.getProperty("java.home");
		javaDir = prepareDir(javaDir);

		logger.info("Determined JDK (additionally to Java) DIR: " + javaDir);

		return javaDir;
	}

	/**
	 * Returns a CodeSource URL which include the Server libs. First the method
	 * tries to read the URL from the configuration file. If that fails it uses
	 * the <code>user.dir</code> system variable to get the current user
	 * directory. This directory is transformed into a CodeSource URL and
	 * returned.
	 * 
	 * @return a CodeSource URL which implies the Server libs
	 * @throws MalformedURLException
	 */
	private final URL getServerUrl() throws MalformedURLException
	{
		Properties props = Configuration.getProperties();

		String libsUrl = props.getProperty("appserver.security.manager.server.url");
		if (libsUrl == null)
		{
			logger.info("Autoconfigure Server libs URL");

			// Load the user directory
			libsUrl = System.getProperty("user.dir");
			libsUrl = prepareUrl(libsUrl);
		}

		logger.info("Determined server libs URL: " + libsUrl);

		return new URL(libsUrl);
	}

	/**
	 * Returns a directory path to the Server directory. First it tries to read
	 * the directory path from the configuration file. If that fails it uses the
	 * <code>user.dir</code> sytem variable to determine the Server directory.
	 * 
	 * @return directory path to the server directory
	 */
	private final String getServerDir()
	{
		Properties props = Configuration.getProperties();

		String libsDir = props.getProperty("appserver.security.manager.server.dir");
		if (libsDir == null)
		{
			logger.info("Autoconfigure server libs DIR");

			// Load the user directory
			libsDir = System.getProperty("user.dir");
			libsDir = prepareDir(libsDir);
		}

		logger.info("Determined Server libs DIR: " + libsDir);

		return libsDir;
	}

	/**
	 * Returns a CodeSource URL to the App directory. It reads the global
	 * configuration to determine where the App archive is extracted. This
	 * directory is transformed to a CodeSource URL.
	 * 
	 * @return a CodeSource URL which implies the App CodeSource
	 * @throws MalformedURLException
	 */
	private final URL getAppUrl() throws MalformedURLException
	{
		// Get the application directory (with the extracted WAR file)
		String appUrl = Configuration.getInstance().getAppDirectory();
		appUrl = prepareUrl(appUrl);

		logger.info("Determined application URL: " + appUrl);

		return new URL(appUrl);
	}

	/**
	 * Returns the directory path to the App directory. The
	 * <code>appDirectory</code> from the configuration is used.
	 * 
	 * @return directory path to the App directorys
	 */
	private final String getAppDir()
	{
		String appDir = Configuration.getInstance().getAppDirectory();
		appDir = prepareDir(appDir);

		logger.info("Determined application DIR: " + appDir);

		return appDir;
	}

	/**
	 * Returns the CodeSource URL of the scratch dir. The configured scratch
	 * directory path is used.
	 * 
	 * @return CodeSource URL which implies the scratch CodeSource
	 * @throws MalformedURLException
	 */
	private final URL getScratchUrl() throws MalformedURLException
	{
		// Get the scratch directory which contains compiled jsp files and other
		// stuff
		String scratchUrl = Configuration.getInstance().getAppScratchDir();
		scratchUrl = prepareUrl(scratchUrl);

		logger.info("Determined scratch URL" + scratchUrl);

		return new URL(scratchUrl);
	}

	/**
	 * Returns the directory path to the scratch directory.
	 * 
	 * @return directory path to teh scratch directory
	 */
	private final String getScratchDir()
	{
		String scratchDir = Configuration.getInstance().getAppScratchDir();
		scratchDir = prepareDir(scratchDir);

		logger.info("Determined scratch DIR: " + scratchDir);

		return scratchDir;
	}

	private final void createCodeSources() throws MalformedURLException
	{
		// Don't use any code signers
		CodeSigner[] signer = null;

		// Determine the CodeSource for java
		URL urlJava = getJavaUrl();
		logger.info("Using CodeSource java: " + urlJava);
		csJava.add(new CodeSource(urlJava, signer));
		urlJava = getJdkUrl(); // Always add this one
		logger.info("Using CodeSource java: " + urlJava);
		csJava.add(new CodeSource(urlJava, signer));

		// Determine the CodeSource for server
		URL urlServer = getServerUrl();
		csServer = new CodeSource(urlServer, signer);
		logger.info("Using CodeSource for server libs: " + urlServer);

		// Determine the CodeSource for application
		URL urlApp = getAppUrl();
		csApp = new CodeSource(urlApp, signer);
		logger.info("Using CodeSource for the App: " + urlApp);

		// Determine the CodeSource for the scratchdir
		URL urlScratch = getScratchUrl();
		csScratch = new CodeSource(urlScratch, signer);
		logger.info("Using CodeSource for the Scratch: " + urlScratch);
	}

	private final void createGlobalPermissions()
	{
		String javaDir = getJavaDir() + "/-";
		String jdkDir = getJdkDir() + "/-";
		String serverDir = getServerDir() + "/-";

		javaDir = swapSeparator(javaDir);
		jdkDir = swapSeparator(jdkDir);
		serverDir = swapSeparator(serverDir);

		logger.info("Granting permission to javaDir: " + javaDir);
		logger.info("Granting permission to jdkDir: " + jdkDir);
		logger.info("Granting permission to serverDir: " + serverDir);

		globalPermission.add(new FilePermission(swapSeparator(javaDir), "read"));
		globalPermission.add(new FilePermission(swapSeparator(jdkDir), "read"));
		globalPermission.add(new FilePermission(swapSeparator(serverDir), "read"));
	}

	private final void createJavaProtectionDomain()
	{
		AsPermissionCollection javaPermissions = new AsPermissionCollection();

		// Java libs are granted all permissions
		javaPermissions.add(new AllPermission());

		for (CodeSource cs : csJava)
		{
			ProtectionDomain pdJava = new ProtectionDomain(cs, javaPermissions);
			pds.add(pdJava);
		}
	}

	private final void createServerProtectionDomain()
	{
		AsPermissionCollection serverPermissions = new AsPermissionCollection();

		// Server libs are granted all permissions
		serverPermissions.add(new AllPermission());

		ProtectionDomain pdServer = new ProtectionDomain(csServer, serverPermissions);
		pds.add(pdServer);
	}

	private final void createAppProtectionDomain()
	{
		// Application directorys are the App-Dir and the Scratch-Dir
		final String appDir = swapSeparator(getAppDir() + "/-");
		final String scratchDir = swapSeparator(getScratchDir() + "/-");

		logger.info("Granting permissions to AppDir: " + appDir);
		logger.info("Granting permissions to ScratchDir: " + scratchDir);

		// Permissions
		AsPermissionCollection appPermissions = new AsPermissionCollection();

		// FilePermissions
		appPermissions.add(new FilePermission(appDir, "read"));
		appPermissions.add(new FilePermission(scratchDir, "read,write,delete"));

		// TODO: DON'T GRANT THIS!!!!!
		appPermissions.add(new SocketPermission("*", "connect,resolve"));

		// RuntimePermissions
		appPermissions.add(new RuntimePermission("getClassLoader"));
		appPermissions.add(new RuntimePermission("createClassLoader"));
		appPermissions.add(new RuntimePermission("setContextClassLoader"));
		appPermissions.add(new RuntimePermission("loadLibrary.*"));
		appPermissions.add(new RuntimePermission("accessClassInPackage.*"));
		appPermissions.add(new RuntimePermission("defineClassInPackage.*"));
		appPermissions.add(new RuntimePermission("accessDeclaredMembers"));
		appPermissions.add(new RuntimePermission("getClassLoader"));

		// PropertyPermissins
		appPermissions.add(new PropertyPermission("*", "read"));

		// ReflectionPermissions
		appPermissions.add(new ReflectPermission("suppressAccessChecks"));

		// Create the ProtectionDomains for the App-URL and Scratch-URL
		ProtectionDomain pdApp = new ProtectionDomain(csApp, appPermissions);
		ProtectionDomain pdScratch = new ProtectionDomain(csScratch, appPermissions);
		pds.add(pdApp);
		pds.add(pdScratch);
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
		return globalPermission;

		// AsPermissionCollection perms = new AsPermissionCollection();
		// perms.addAll(globalPermission);
		//
		// for (ProtectionDomain pd : pds)
		// {
		// if (pd.getCodeSource().implies(codesource))
		// {
		// perms.addAll(pd.getPermissions());
		// }
		// }
		//
		// return perms;
	}

	public PermissionCollection getPermissions(ProtectionDomain domain)
	{
		return globalPermission;
		// return getPermissions(domain.getCodeSource());
	}

	public boolean implies(ProtectionDomain domain, Permission permission)
	{
		// Check global permissions
		if (globalPermission.implies(permission))
			return true;

		// Get codesource
		CodeSource cs = domain.getCodeSource();

		// Iterate over all proctection domains and check codesource
		boolean implied = false;
		for (ProtectionDomain pd : pds)
		{
			// If codesource matches
			if (pd.getCodeSource().implies(cs))
			{
				// Codesource matched (used for logging)
				implied = true;

				// Check privileges
				if (pd.implies(permission))
				{
					// Permission is granted
					return true;
				} else
				{
					// Permission explicity refused
					logger.warn("Permission explicity refused: " + permission + " request: "
							+ cs.getLocation() + " against: " + pd.getCodeSource().getLocation());

					// Perhaps other ProtectionDomains include the CodeSource
					// and grant the permission
					continue;
				}
			}
		}

		// Requested CodeSource does not match any configured CodeSource.
		// Therefore no erros have been displayed until now.
		if (implied == false)
			logger.warn("CodeSource did not match: " + permission + " on: " + cs.getLocation());

		return DEBUG;
	}
}
