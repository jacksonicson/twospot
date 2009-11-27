package org.prot.app.services.db;

public class DbBrowserServiceFactory
{
	private static DbBrowserService service; 
	
	public static final DbBrowserService getDbBrowserService()
	{
		if(service == null)
		{
			DbDao dbDao = new JdoDbDao();
			service = new DbBrowserService(dbDao);
		}
		
		return service; 
	}
}
