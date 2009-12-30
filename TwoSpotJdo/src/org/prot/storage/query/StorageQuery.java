package org.prot.storage.query;

import java.io.Serializable;

import org.prot.stor.hbase.Key;

public class StorageQuery implements Serializable
{
	private static final long serialVersionUID = 3402635680542012919L;

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
}
