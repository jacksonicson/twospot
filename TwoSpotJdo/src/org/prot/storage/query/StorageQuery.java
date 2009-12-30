package org.prot.storage.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.prot.storage.Key;
import org.prot.storage.connection.HBaseManagedConnection;

public class StorageQuery implements Serializable
{
	private static final long serialVersionUID = 3402635680542012919L;

	/**
	 * Query-Syntax (from google appengine:
	 * http://code.google.com/appengine/docs
	 * /python/datastore/gqlreference.html):
	 * 
	 * SELECT [* | __key__ ] FROM <kind> [WHERE <condition> [AND <condition>
	 * ...]] [ORDER BY <property> [ASC | DESC] [, <property> [ASC | DESC] ...]]
	 * [LIMIT [<offset>,]<count>] [OFFSET <offset>]
	 * 
	 * <condition> := <property> {< | <= | = | => | > | !=} <value>
	 */

	private Key key;

	private String kind;

	private SelectCondition condition = new SelectCondition();

	private LimitCondition limit;

	public StorageQuery(String kind)
	{
		this(kind, null);
	}

	public StorageQuery(String kind, Key key)
	{
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
			result.add(fetchObject(key));
			return result;
		}

		if (condition != null)
			condition.run(connection, result, limit);

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
}
