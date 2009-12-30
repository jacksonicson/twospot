package org.prot.storage.query;

public class AtomLiteral
{
	private byte[] value;

	private boolean key;

	public AtomLiteral(byte[] value)
	{
		this.value = value;
	}

	public AtomLiteral(byte[] value, boolean key)
	{
		this.value = value;
		this.key = key;
	}

	public byte[] getValue()
	{
		return value;
	}

	public boolean isKey()
	{
		return this.key;
	}
}
