package org.prot.storage.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.prot.stor.hbase.HBaseManagedConnection;

public class SelectCondition implements Serializable
{
	private static final long serialVersionUID = -1163609480212762858L;

	private List<AtomarCondition> atoms = new ArrayList<AtomarCondition>();

	public void addCondition(AtomarCondition atom)
	{
		this.atoms.add(atom);
	}

	void run(HBaseManagedConnection connection, List<Object> result, LimitCondition limit)
			throws IOException, ClassNotFoundException
	{
		for (AtomarCondition atom : atoms)
			atom.run(connection, result, limit);
	}
}
