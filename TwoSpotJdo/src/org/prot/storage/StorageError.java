package org.prot.storage;

public class StorageError extends RuntimeException
{
	private static final long serialVersionUID = -693619974009109100L;

	public StorageError(String msg)
	{
		super(msg);
	}
}
