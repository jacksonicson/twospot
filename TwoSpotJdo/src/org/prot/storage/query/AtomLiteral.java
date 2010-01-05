package org.prot.storage.query;

public class AtomLiteral
{
	// Byte[] of the literal value
	private byte[] value;

	// True if value contains a stringified key
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
