package org.prot.app.services.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DataTablet implements Iterable<String[]>
{
	private static final Logger logger = Logger.getLogger(DataTablet.class);
	
	private int len = 0;

	private Map<String, Integer> indexMap = new HashMap<String, Integer>();

	private List<String[]> data = new ArrayList<String[]>();

	private String[] currentRow = new String[0];
	
	void nextRow()
	{
		data.add(currentRow);
		currentRow = new String[len]; 
	}

	private void expandCurrentRow()
	{
		String[] newCurrentRow = new String[len + 1];
		System.arraycopy(currentRow, 0, newCurrentRow, 0, len);
		currentRow = newCurrentRow;
	}
	
	void put(String key, String value)
	{
		assert(indexMap != null);

		int index = -1; 
		if(indexMap.containsKey(key) == false)
		{
			logger.info("Expand");
			expandCurrentRow();
			index = len; 
			len++; 
			indexMap.put(key, index);
		}
		else
			index = indexMap.get(key);
		
		currentRow[index] = value; 
	}
	
	public String[] getKeys()
	{
		return indexMap.keySet().toArray(new String[0]);
	}

	@Override
	public Iterator<String[]> iterator()
	{
		return data.iterator(); 
	}
}
