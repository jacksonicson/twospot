package org.prot.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IndexDefinition implements Serializable
{
	private static final long serialVersionUID = 1415816807703277880L;

	private List<List<String>> indices = new ArrayList<List<String>>();

	public void addIndex(List<String> index)
	{
		this.indices.add(index);
	}

	public List<List<String>> getIndexProperties()
	{
		return indices;
	}
}
