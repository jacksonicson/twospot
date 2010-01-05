package org.prot.storage.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

public class SelectCondition implements Serializable
{
	private static final long serialVersionUID = -1163609480212762858L;

	private static final Logger logger = Logger.getLogger(SelectCondition.class);

	// List of all atomar condition. The intersection of all results is
	// returned!
	private List<AtomarCondition> atoms = new ArrayList<AtomarCondition>();

	public void addCondition(AtomarCondition atom)
	{
		this.atoms.add(atom);
	}

	public boolean isEmpty()
	{
		return atoms.isEmpty();
	}

	@SuppressWarnings("serial")
	class ArrayWrapperSet extends HashSet
	{
		@Override
		public boolean add(Object o)
		{
			byte[] b = (byte[]) o;
			ArrayWrapper w = new ArrayWrapper(b);
			return super.add(w);
		}
	}

	class ArrayWrapperList extends ArrayList
	{
		@Override
		public boolean add(Object o)
		{
			byte[] b = (byte[]) o;
			ArrayWrapper w = new ArrayWrapper(b);
			return super.add(w);
		}
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
			return Bytes.hashCode(bytes);
		}

		public boolean equals(Object o)
		{
			if (o == this)
				return true;

			if (!(o instanceof ArrayWrapper))
				return false;

			ArrayWrapper comp = (ArrayWrapper) o;
			return Bytes.equals(bytes, comp.bytes);
		}
	}

	void run(QueryHandler handler, StorageQuery query, List<byte[]> result, LimitCondition limit)
			throws IOException
	{
		logger.debug("Number of Atoms: " + atoms.size());

		ArrayWrapperList tmpResult = new ArrayWrapperList();
		ArrayWrapperSet tmp = new ArrayWrapperSet();

		// Run each atom
		boolean first = true;
		for (AtomarCondition atom : atoms)
		{
			if (first)
			{
				first = false;
				atom.run(handler, query, tmpResult, limit);
			} else
			{
				tmp.clear();
				atom.run(handler, query, tmp, limit);

				for (Iterator<ArrayWrapper> it = tmpResult.iterator(); it.hasNext();)
				{
					ArrayWrapper test = it.next();
					if (!tmp.contains(test))
					{
						it.remove();
					}
				}
			}
		}

		// Add all results to the result list
		for (Object entity : tmpResult)
			result.add(((ArrayWrapper) entity).bytes);
	}
}
