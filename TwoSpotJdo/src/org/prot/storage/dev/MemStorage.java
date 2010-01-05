package org.prot.storage.dev;

import java.util.HashMap;
import java.util.Map.Entry;

import org.prot.storage.Key;

public class MemStorage
{
	private HashMap<String, MemTable> tables = new HashMap<String, MemTable>();

	private MemTable getTable(String tableName)
	{
		if (!tables.containsKey(tableName))
		{
			MemTable table = new MemTable();
			table.read(tableName + ".table");
			tables.put(tableName, table);
		}

		return tables.get(tableName);
	}

	public void write()
	{
		for (Entry<String, MemTable> entry : tables.entrySet())
			entry.getValue().write(entry.getKey() + ".table");
	}

	public void addObj(String kind, Key key, byte[] obj)
	{
		MemTable table = getTable(kind);
		table.add(key, obj);
		table.write(kind + ".table");
	}

	public boolean removeObj(String kind, Key key)
	{
		MemTable table = getTable(kind);
		boolean state = table.remove(key);
		if (state)
			table.write(kind + ".table");

		return state;
	}

	public void updateObject(String kind, Key key, byte[] value)
	{
		MemTable table = getTable(kind);
		table.add(key, value);
		table.write(kind + ".table");
	}
}
