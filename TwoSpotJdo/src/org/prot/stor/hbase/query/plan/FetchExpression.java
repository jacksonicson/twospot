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

public class FetchExpression extends QueryStep
{
	private static final Logger logger = Logger.getLogger(FetchExpression.class);

	private FetchType type;

	private String kind;

	private LiteralParameter left;
	private LiteralParameter right;

	public FetchExpression(FetchType type, String kind, LiteralParameter left, LiteralParameter right)
	{
		this.type = type;
		this.kind = kind;
		this.left = left;
		this.right = right;
	}

	@Override
	public void exeucte(HBaseManagedConnection connection, List<Object> candidates)
	{
		logger.debug("Executing fetch expression");

		byte[] bLeft = left.getValue();
		byte[] bRight = right.getValue();

		logger.debug("Scanning for left: " + new String(bLeft));
		logger.debug("Scanning for right: " + new String(bRight));

		// Scanning mechanism depends on the fetch type
		HTable index = connection.getHTable(HBaseUtils.INDEX_BY_PROPERTY_TABLE);
		HTable entities = connection.getHTable(HBaseUtils.ENTITY_TABLE);

		byte[] bAppId = Bytes.toBytes(HBaseUtils.APP_ID);
		byte[] bKind = Bytes.toBytes(kind);

		byte[] ones = new byte[1024];
		for (int i = 0; i < ones.length; i++)
			ones[i] = (byte) 0xFF;

		if (type == FetchType.EQUALS || type == FetchType.EQUALS_GREATER || type == FetchType.GREATER)
		{

			Scan scan = null;
			if (type == FetchType.EQUALS)
			{
				// Schema: appId/Kind/property/value/entityKey
				// Start: gogo/Person/username/Bob/0x00
				// Stop: gogo/Person/username/Bob/0xFFFF
				byte[] startKey = Bytes.add(bAppId, "/".getBytes(), bKind);
				startKey = Bytes.add(startKey, "/".getBytes(), bLeft);
				startKey = Bytes.add(startKey, "/".getBytes(), bRight);
				startKey = Bytes.add(startKey, "/".getBytes());

				byte[] stopKey = Bytes.add(startKey, ones);

				scan = new Scan(startKey, stopKey);
			} else if (type == FetchType.EQUALS_GREATER)
			{
				// Schema: appId/Kind/property/value/entityKey
				// Start: gogo/Person/username/Bob/0x00
				// Stop: gogo/Person/username/0xFFFF
				byte[] startKey = Bytes.add(bAppId, "/".getBytes(), bKind);
				startKey = Bytes.add(startKey, "/".getBytes(), bLeft);
				startKey = Bytes.add(startKey, "/".getBytes(), bRight);

				byte[] stopKey = Bytes.add(bAppId, "/".getBytes(), bKind);
				stopKey = Bytes.add(stopKey, "/".getBytes(), bLeft);
				stopKey = Bytes.add(stopKey, "/".getBytes(), ones);

				scan = new Scan(startKey, stopKey);
			} else if (type == FetchType.GREATER)
			{
				// Schema: appId/Kind/property/value/entityKey
				// Start: gogo/Person/username/Bob[++]/0x00
				// Stop: gogo/Person/username/0xFFFF

				boolean match = false;
				for (int i = bRight.length - 1; i >= 0; i--)
				{
					if (bRight[i] == 0xFF)
						continue;
					else
					{
						bRight[i]++;
						match = true;
						break;
					}
				}
				if (!match)
					bRight = Bytes.add(bRight, new byte[] { 0x00 });

				byte[] startKey = Bytes.add(bAppId, "/".getBytes(), bKind);
				startKey = Bytes.add(startKey, "/".getBytes(), bLeft);
				startKey = Bytes.add(startKey, "/".getBytes(), bRight);

				byte[] stopKey = Bytes.add(bAppId, "/".getBytes(), bKind);
				stopKey = Bytes.add(stopKey, "/".getBytes(), bLeft);
				stopKey = Bytes.add(stopKey, "/".getBytes(), ones);

				scan = new Scan(startKey, stopKey);
			}

			try
			{
				ResultScanner resultScanner = index.getScanner(scan);
				for (Iterator<Result> it = resultScanner.iterator(); it.hasNext();)
				{
					Result result = it.next();
					if (result == null)
						continue;

					if (result.getMap() == null)
						continue;

					// byte[] key = result.getRow();
					// if (Bytes.compareTo(startKey, 0, startKey.length, key, 0,
					// startKey.length) == 0)
					// {
					// logger.debug("Compare");
					// }
					// else
					// continue;

					byte[] nothing = Bytes.toBytes("key");
					byte[] entityKey = result.getMap().get(nothing).get(nothing).lastEntry().getValue();
					logger.debug("Entity key found: " + new String(entityKey));

					// Fetch the entity
					Get get = new Get(entityKey);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
