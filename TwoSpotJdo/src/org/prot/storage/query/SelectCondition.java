package org.prot.storage.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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

	class ArrayWrapper
	{
		byte[] bytes;

		public ArrayWrapper(byte[] bytes)
		{
			this.bytes = bytes;
		}

		public int hashCode()
		{
			return Arrays.hashCode(bytes);
		}

		public boolean equals(Object o)
		{
			if (o == this)
				return true;

			if (!(o instanceof ArrayWrapper))
				return false;

			ArrayWrapper comp = (ArrayWrapper) o;
			return Arrays.equals(bytes, comp.bytes);
		}
	}

	void run(HBaseManagedConnection connection, StorageQuery query, List<byte[]> result, LimitCondition limit)
			throws IOException
	{
		logger.debug("Number of Atoms: " + atoms.size());

		// Cannot contain duplicates
		Set<ArrayWrapper> last = new HashSet<ArrayWrapper>();
		Set<ArrayWrapper> tmp = new HashSet<ArrayWrapper>();

		// Run each atom
		boolean first = true;
		for (AtomarCondition atom : atoms)
		{
			List<byte[]> partialResult = new ArrayList<byte[]>();
			atom.run(connection, query, partialResult, limit);

			if (first)
			{
				first = false;

				for (byte[] entity : partialResult)
					last.add(new ArrayWrapper(entity));
			} else
			{
				tmp.clear();
				for (byte[] entity : partialResult)
				{
					ArrayWrapper wrapper = new ArrayWrapper(entity);
					if (last.contains(wrapper))
						tmp.add(wrapper);
				}

				last.clear();
				last.addAll(tmp);
			}
		}

		// Add all results to the result list
		for (ArrayWrapper entity : last)
			result.add(entity.bytes);
	}
}
