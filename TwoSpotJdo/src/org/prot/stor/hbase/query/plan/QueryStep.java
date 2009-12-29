package org.prot.stor.hbase.query.plan;

import java.util.List;

import org.prot.stor.hbase.HBaseManagedConnection;

public abstract class QueryStep
{
	public abstract void exeucte(HBaseManagedConnection connection, List<Object> candidates);
	
}
