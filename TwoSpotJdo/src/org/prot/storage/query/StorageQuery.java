package org.prot.storage.query;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.storage.Key;
import org.prot.storage.KeyHelper;
import org.prot.storage.connection.HBaseManagedConnection;
import org.prot.storage.connection.StorageUtils;

/**
 * Query-Syntax (from google appengine: http://code.google.com/appengine/docs
 * /python/datastore/gqlreference.html):
 */
public class StorageQuery implements Serializable
{
	private static final long serialVersionUID = 3402635680542012919L;

	private static final Logger logger = Logger.getLogger(StorageQuery.class);

	// AppId to query
	private String appId;

	// Kind of the entity to query
	private String kind;

	// Key of the entity to query
	private Key key;

	// The select condition (could be empty)
	private SelectCondition condition = new SelectCondition();

	// Limit condition which counts the number of fetch operations
	private LimitCondition limit = new LimitCondition();

	public StorageQuery(String appId, String kind)
	{
		this(appId, kind, null);
	}

	public StorageQuery(String appId, String kind, Key key)
	{
		this.appId = appId;
		this.key = key;
		this.kind = kind;
	}

	public SelectCondition getCondition()
	{
		return this.condition;
	}

	List<byte[]> run(HBaseManagedConnection connection) throws IOException
	{
		// List which contains all results
		List<byte[]> result = new ArrayList<byte[]>();

		if (key != null)
		{
			logger.debug("Fetching object by key");
			fetchByKey(connection, result);
			return result;
		} else if (!condition.isEmpty())
		{
			logger.debug("Fetching object by condition");
			condition.run(connection, this, result, limit);
		} else if (kind != null)
		{
			logger.debug("Fetching object by kind");
			fetchByKind(connection, result);
		}

		logger.debug("Returning: " + result.size());
		return result;
	}

	private void fetchByKey(HBaseManagedConnection connection, List<byte[]> result) throws IOException
	{
		HTable entityTable = StorageUtils.getTableEntity(connection);

		byte[] rowKey = KeyHelper.createRowKey(appId, kind, key);
		Set<byte[]> keySet = new HashSet<byte[]>();
		keySet.add(rowKey);

		StorageUtils.materialize(entityTable, keySet, result, limit);
	}

	private void fetchByKind(HBaseManagedConnection connection, List<byte[]> result) throws IOException
	{
		HTable entityTable = StorageUtils.getTableEntity(connection);
		HTable indexByKindTable = StorageUtils.getTableIndexByKind(connection);

		byte[] startKey = KeyHelper.createIndexByKindKey(appId, kind);

		byte[] bAppId = Bytes.toBytes(appId);
		byte[] bKind = Bytes.toBytes(kind);
		bKind = KeyHelper.incrementByteArray(bKind);
		byte[] stopKey = Bytes.add(bAppId, StorageUtils.bSlash, bKind);

		Scan scan = new Scan(startKey, stopKey);
		ResultScanner scanner = indexByKindTable.getScanner(scan);
		Set<byte[]> keySet = new HashSet<byte[]>();
		for (Iterator<Result> iterator = scanner.iterator(); iterator.hasNext();)
		{
			// Update limits
			if (!limit.incrementIndex())
				break;

			// Fetch result
			Result res = iterator.next();
			if (res.getMap() == null)
				continue;

			// Update keyset
			byte[] rowKey = res.getMap().get(StorageUtils.bKey).get(StorageUtils.bKey).lastEntry().getValue();
			keySet.add(rowKey);
		}

		// Materialize all entities
		StorageUtils.materialize(entityTable, keySet, result, limit);
	}

	public LimitCondition getLimit()
	{
		return this.limit;
	}

	public void setKey(Key key)
	{
		this.key = key;
	}

	public void setKind(String kind)
	{
		this.kind = kind;
	}

	public Key getKey()
	{
		return key;
	}

	public String getKind()
	{
		return kind;
	}

	public String getAppId()
	{
		return appId;
	}
}
