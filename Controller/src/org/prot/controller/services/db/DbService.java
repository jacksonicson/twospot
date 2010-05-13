package org.prot.controller.services.db;

import java.util.List;

import org.prot.controller.services.PrivilegedAppServer;

public interface DbService {
	@PrivilegedAppServer
	public List<String> getTables(String token, String appId);

	@PrivilegedAppServer
	public List<byte[]> getTableData(String token, String appId, String kind);
}
