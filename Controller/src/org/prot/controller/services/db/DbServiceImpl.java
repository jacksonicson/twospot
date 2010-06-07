package org.prot.controller.services.db;

import java.util.List;

import org.apache.log4j.Logger;
import org.prot.controller.app.TokenChecker;
import org.prot.controller.services.gen.Services.DbService;
import org.prot.controller.services.gen.Services.FetchTable;
import org.prot.controller.services.gen.Services.TableData;
import org.prot.controller.services.gen.Services.TableList;
import org.prot.storage.Storage;
import org.prot.storage.StorageImpl;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class DbServiceImpl extends DbService {
	private static final Logger logger = Logger.getLogger(DbServiceImpl.class);

	private TokenChecker tokenChecker;

	private DbDao dbDao;

	@Override
	public void getTableData(RpcController controller, FetchTable request, RpcCallback<TableData> done) {
		// if (tokenChecker.checkToken(token) == false)
		// return null;

		Storage storage = new StorageImpl();
		List<byte[]> data = storage.scanEntities(request.getAppId(), request.getKind());

		TableData.Builder builder = TableData.newBuilder();
		for (byte[] d : data)
			builder.addTableData(ByteString.copyFrom(d));

		done.run(builder.build());
	}

	@Override
	public void getTables(RpcController controller, FetchTable request, RpcCallback<TableList> done) {
		// if (tokenChecker.checkToken(token) == false)
		// return null;

		List<String> tables = dbDao.getTables(request.getAppId());
		TableList.Builder builder = TableList.newBuilder();
		for (String table : tables)
			builder.addTableNames(table);

		done.run(builder.build());
	}

	public void setDbDao(DbDao dbDao) {
		this.dbDao = dbDao;
	}

	public void setTokenChecker(TokenChecker tokenChecker) {
		this.tokenChecker = tokenChecker;
	}
}
