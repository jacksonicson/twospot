package org.prot.storage.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.storage.connection.HBaseManagedConnection;

public class SelectCondition implements Serializable
{
	private static final long serialVersionUID = -1163609480212762858L;

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SelectCondition.class);

	// List of all atomar condition. The intersection of all results is
	// returned!
	private List<AtomarCondition> atoms = new ArrayList<AtomarCondition>();

	public void addCondition(AtomarCondition atom)
	{
		this.atoms.add(atom);
	}

	boolean isEmpty()
	{
		return atoms.isEmpty();
	}

	void run(HBaseManagedConnection connection, StorageQuery query, List<byte[]> result, LimitCondition limit)
			throws IOException, ClassNotFoundException
	{
		// Cannot contain duplicates
		Set<byte[]> intersection = new HashSet<byte[]>();

		// Run each atom
		for (AtomarCondition atom : atoms)
		{
			List<byte[]> partialResult = new ArrayList<byte[]>();
			atom.run(connection, query, partialResult, limit);
			intersection.addAll(partialResult);
		}

		// Add all results to the result list
		result.addAll(intersection);
	}
}
