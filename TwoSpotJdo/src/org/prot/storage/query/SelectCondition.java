package org.prot.storage.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.storage.connection.HBaseManagedConnection;

public class SelectCondition implements Serializable
{
	private static final long serialVersionUID = -1163609480212762858L;

	private static final Logger logger = Logger.getLogger(SelectCondition.class);

	private List<AtomarCondition> atoms = new ArrayList<AtomarCondition>();

	public void addCondition(AtomarCondition atom)
	{
		this.atoms.add(atom);
	}

	void run(HBaseManagedConnection connection, StorageQuery query, List<Object> result, LimitCondition limit)
			throws IOException, ClassNotFoundException
	{
		logger.debug("Atomar conditions: " + atoms.size());

		for (AtomarCondition atom : atoms)
			atom.run(connection, query, result, limit);
	}
}
