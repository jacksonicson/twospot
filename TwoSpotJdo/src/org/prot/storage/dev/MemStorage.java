package org.prot.storage.dev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.util.Bytes;
import org.prot.storage.Key;

public class MemStorage
{
	private HashMap<String, MemTable> tables = new HashMap<String, MemTable>();

	public MemTable getTable(String tableName)
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

	public List<Key> createKey(long count)
	{
		MemTable table = getTable("counter");
		
		Key key = new Key();
		byte[] time = Bytes.toBytes((long)0);
		byte[] index = Bytes.toBytes((long)0);
		key.setKey(Bytes.add(time, index));
		
		byte[] data = table.get(key);

		long counter = 0; 
		if(data != null)
			counter = Bytes.toLong(data);
		
		counter += count;
		data = Bytes.toBytes(counter); 
		table.add(key, data);
		table.write("counter.table");

		List<Key> keyList = new ArrayList<Key>();
		while (count-- > 0)
		{
			byte[] bTime = Bytes.toBytes(System.currentTimeMillis());
			byte[] bCounter = Bytes.toBytes(counter - count);
			byte[] bKey = Bytes.add(bTime, bCounter);
			
			key = new Key();
			key.setKey(bKey);

			keyList.add(key);
		}

		return keyList;

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
