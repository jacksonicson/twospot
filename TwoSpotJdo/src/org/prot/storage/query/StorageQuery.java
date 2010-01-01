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

	private String appId;

	private Key key;

	private String kind;

	private SelectCondition condition = new SelectCondition();

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

	List<byte[]> run(HBaseManagedConnection connection) throws IOException, ClassNotFoundException
	{
		List<byte[]> result = new ArrayList<byte[]>();

		if (key != null)
		{
			logger.debug("Fetching object by key: NOT IMPLEMENTED");
			// result.add(fetchObject(key));
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

	private void fetchByKind(HBaseManagedConnection connection, List<byte[]> result) throws IOException,
			ClassNotFoundException
	{
		HTable entityTable = getTableEntity(connection);
		HTable indexByKindTable = getTableIndexByKind(connection);

		// TODO: Create a stop key
		byte[] startKey = KeyHelper.createIndexByKindKey(appId, kind);

		Scan scan = new Scan(startKey);
		ResultScanner scanner = indexByKindTable.getScanner(scan);
		Set<byte[]> keySet = new HashSet<byte[]>();
		for (Iterator<Result> iterator = scanner.iterator(); iterator.hasNext();)
		{
			Result res = iterator.next();
			if (res.getMap() == null)
				continue;

			byte[] rowKey = res.getMap().get(StorageUtils.bKey).get(StorageUtils.bKey).lastEntry().getValue();
			keySet.add(rowKey);
		}

		// Materialize all entities
		AtomarCondition condition = new AtomarCondition(null, null, null);
		condition.materialize(entityTable, keySet, result);
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

	private HTable getTableEntity(HBaseManagedConnection connection)
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_ENTITIES);
		return table;
	}

	private HTable getTableIndexByKind(HBaseManagedConnection connection)
	{
		HTable table = connection.getHTable(StorageUtils.TABLE_INDEX_BY_KIND);
		return table;
	}
}
