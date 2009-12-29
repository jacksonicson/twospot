package org.prot.stor.hbase.query.plan;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.stor.hbase.HBaseManagedConnection;
import org.prot.stor.hbase.HBaseUtils;

public class KindExpression extends QueryStep
{
	private static final Logger logger = Logger.getLogger(KindExpression.class);

	private String kind;

	public KindExpression(String kind)
	{
		this.kind = kind;
	}

	@Override
	public void exeucte(HBaseManagedConnection connection, List<Object> candidates)
	{
		HTable index = connection.getHTable(HBaseUtils.INDEX_BY_KIND_TABLE);
		HTable entities = connection.getHTable(HBaseUtils.ENTITY_TABLE);

		logger.debug("Using kind: " + kind);

		byte[] bAppId = Bytes.toBytes(HBaseUtils.APP_ID);
		byte[] bKind = Bytes.toBytes(kind);

		byte[] startKey = Bytes.add(bAppId, "/".getBytes(), bKind);
		startKey = Bytes.add(startKey, "/".getBytes());

		logger.debug("StartKey is: " + new String(startKey));

		try
		{
			Scan scan = new Scan(startKey);
			ResultScanner scanner = index.getScanner(scan);
			Iterator<Result> resultIterator = scanner.iterator();

			while (resultIterator.hasNext())
			{
				Result result = resultIterator.next();

				byte[] nothing = Bytes.toBytes("key");
				byte[] key = result.getValue(nothing, nothing);

				// logger.debug("Key: " + key);

				Get get = new Get(key);
				Result rr = entities.get(get);

				if (rr != null)
				{
					// Rematerialize the entity
					// logger.debug("Value: " + rr.getMap());
					if (rr.getMap() != null)
					{
						byte[] data = rr.getMap().get("entity".getBytes()).get("serialized".getBytes())
								.firstEntry().getValue();

						ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
						Object obj = in.readObject();

						candidates.add(obj);

						logger.debug("something was found " + obj.getClass());
					}
				}

			}

		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		logger.debug("Executing kind expression - find everything in the index");
	}
}
