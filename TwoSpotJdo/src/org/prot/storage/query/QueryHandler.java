package org.prot.storage.query;

import java.io.IOException;
import java.util.Collection;

public interface QueryHandler
{
	public void execute(Collection<byte[]> result, StorageQuery query) throws IOException;

	public void execute(Collection<byte[]> result, StorageQuery query, AtomarCondition condition)
			throws IOException;

}
