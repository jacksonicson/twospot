package org.prot.storage.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.Key;
import org.prot.storage.connection.HBaseManagedConnection;

/**
 * Query-Syntax (from google appengine: http://code.google.com/appengine/docs
 * /python/datastore/gqlreference.html):
 */
public class StorageQuery implements Serializable
{
	private static final long serialVersionUID = 3402635680542012919L;

	private static final Logger logger = Logger.getLogger(StorageQuery.class);

	private String appId;
	
	private Key key;

	private String kind;

	private SelectCondition condition = new SelectCondition();

	private LimitCondition limit;

	public StorageQuery(String appId, String kind)
	{
		this(appId, kind, null);
	}

	public StorageQuery(String appId, String kind, Key key)
	{
		this.appId = appId; 
		this.key = key;
		this.kind = kind;
	}

	public SelectCondition getCondition()
	{
		return this.condition;
	}

	List<Object> run(HBaseManagedConnection connection) throws IOException, ClassNotFoundException
	{
		List<Object> result = new ArrayList<Object>();

		if (key != null)
		{
			logger.debug("Fetching object by key");
			result.add(fetchObject(key));
			return result;
		}

		if (condition != null)
		{
			logger.debug("Fetching object by condition");
			condition.run(connection, this, result, limit);
		}

		logger.debug("Returning: " + result.size());
		return result;
	}

	private Object fetchObject(Key key)
	{
		return null;
	}

	public void setKey(Key key)
	{
		this.key = key;
	}

	public void setKind(String kind)
	{
		this.kind = kind;
	}

	public void setLimit(LimitCondition limit)
	{
		this.limit = limit;
	}

	public Key getKey()
	{
		return key;
	}

	public String getKind()
	{
		return kind;
	}

	public String getAppId()
	{
		return appId;
	}
}
