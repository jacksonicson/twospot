package org.prot.stor.hbase;

import java.util.Properties;

import org.datanucleus.store.valuegenerator.ValueGenerator;

public class KeyValueGenerator implements ValueGenerator
{
	private long counter = 0;

	private Key current;

	public KeyValueGenerator(String s, Properties p)
	{

	}

	@Override
	public void allocate(int additional)
	{
	}

	@Override
	public Object current()
	{
		return current;
	}

	@Override
	public long currentValue()
	{
		return counter;
	}

	@Override
	public String getName()
	{
		return "keygenerator";
	}

	@Override
	public Object next()
	{
		long time = System.currentTimeMillis();
		current = new Key();
		current.setKey(("tt" + time).getBytes());
		return current;
	}

	@Override
	public long nextValue()
	{
		return ++counter;
	}

}
