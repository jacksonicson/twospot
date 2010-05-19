package org.prot.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.storage.connection.ConnectionFactory;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;

public class Analyzer {

	private static final Logger logger = Logger.getLogger(Analyzer.class);

	private HBaseManagedConnection connection;

	private ObjectCreator creator;

	private ObjectRemover remover;

	public Analyzer(ConnectionFactory connectionFactory) {
		this.connection = connectionFactory.createManagedConnection();

		this.creator = new ObjectCreator(connection);

		this.remover = new ObjectRemover(connection);
	}

	public List<String> listKinds(String appId) throws IOException {
		HTable tableIndexByKind = StorageUtils.getTableIndexByKind(connection);

		List<String> kinds = new ArrayList<String>();

		byte[] key = (appId + "/").getBytes();
		byte[] scanKey = key;
		byte[] scanEnd = KeyHelper.incrementByteArray(appId.getBytes());
		int i = 0;
		while (i++ < 100) {
			Scan scan = new Scan(scanKey, scanEnd);
			ResultScanner scanner = tableIndexByKind.getScanner(scan);
			Iterator<Result> it = scanner.iterator();
			if (it.hasNext()) {
				Result result = it.next();
				String sKey = new String(result.getRow());
				sKey = sKey.substring(sKey.indexOf("/") + 1);
				sKey = sKey.substring(0, sKey.indexOf("/"));
				kinds.add(sKey);

				byte[] kind = sKey.getBytes();
				scanKey = Bytes.add(Bytes.add(Bytes.add(key, kind), "/".getBytes()), KeyHelper
						.getArrayOfOnes());

				logger.info("Scanned: " + new String(scanKey));

			} else
				break;
		}

		return kinds;
	}

	public List<byte[]> scanEntities(String appId, String kind) throws IOException {

		HTable tableEntities = StorageUtils.getTableEntity(connection);
		HTable tableIndexByKind = StorageUtils.getTableIndexByKind(connection);

		byte[] scanStart = (appId + "/" + kind + "/").getBytes();
		byte[] scanEnd = Bytes.add((appId + "/" + kind + "/").getBytes(), KeyHelper.getArrayOfOnes());

		List<byte[]> entities = new ArrayList<byte[]>();

		Scan scan = new Scan(scanStart, scanEnd);
		ResultScanner scanner = tableIndexByKind.getScanner(scan);
		for (Iterator<Result> it = scanner.iterator(); it.hasNext();) {
			Result result = it.next();
			byte[] entityKey = result.getMap().get(StorageUtils.bKey).get(StorageUtils.bKey).lastEntry()
					.getValue();

			Get get = new Get(entityKey);
			result = tableEntities.get(get);
			byte[] entity = result.getMap().get(StorageUtils.bEntity).get(StorageUtils.bSerialized)
					.lastEntry().getValue();
			entities.add(entity);
		}

		logger.info("Fetched: " + entities.size());
		
		return entities;
	}
}
