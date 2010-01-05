package org.prot.storage.query;

import java.io.IOException;
import java.util.List;

public interface QueryHandler
{
	public void execute(List<byte[]> result, StorageQuery query) throws IOException;

	public void execute(List<byte[]> result, StorageQuery query, AtomarCondition condition)
			throws IOException;

}
