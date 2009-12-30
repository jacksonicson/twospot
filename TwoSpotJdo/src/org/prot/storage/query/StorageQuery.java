package org.prot.storage.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.prot.stor.hbase.HBaseManagedConnection;
import org.prot.stor.hbase.Key;

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

	private SelectCondition condition;

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

	public void setCondition(SelectCondition condition)
	{
		this.condition = condition;
	}

	List<Object> run(HBaseManagedConnection connection)
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
}
