package org.prot.storage.query;

import java.io.Serializable;
import java.util.List;

import org.prot.stor.hbase.HBaseManagedConnection;

public class AtomarCondition implements Serializable
{
	private static final long serialVersionUID = 2659834747381329323L;

	private ConditionType type;

	private String property;
	private byte[] value;

	public AtomarCondition(ConditionType type, String property, byte[] value)
	{
		this.type = type;
		this.property = property;
		this.value = value;
	}

	void run(HBaseManagedConnection connection, List<Object> result, LimitCondition limit)
	{

	}
	
	
}
