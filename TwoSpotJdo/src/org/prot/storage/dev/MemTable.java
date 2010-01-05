package org.prot.storage.dev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.prot.storage.Key;

public class MemTable
{
	private static final Logger logger = Logger.getLogger(MemTable.class);

	private static final int KEY_LENGTH = 16;

	private TreeMap<Key, byte[]> tableData = new TreeMap<Key, byte[]>();

	public void read(String file)
	{
		RandomAccessFile rfile = null;
		try
		{
			rfile = new RandomAccessFile(file, "r");
			rfile.seek(0);

			// Read counter
			byte[] longBuffer = new byte[8];
			rfile.readFully(longBuffer);
			int length = (int) Bytes.toLong(longBuffer);

			// Read offset table
			byte entityOffset[][] = new byte[length][8];
			for (int i = 0; i < length; i++)
				rfile.readFully(entityOffset[i]);

			// Read key and entities
			for (int i = 1; i < entityOffset.length; i++)
			{
				long startSeek = Bytes.toLong(entityOffset[i - 1]);
				long stopSeek = Bytes.toLong(entityOffset[i]);

				logger.trace("start: " + startSeek + " stop: " + stopSeek);
				rfile.seek(startSeek);

				// Read the key
				byte[] keyBuffer = new byte[KEY_LENGTH];
				rfile.readFully(keyBuffer);

				// Read the entity
				byte[] buffer = new byte[(int) (stopSeek - startSeek - KEY_LENGTH)];
				rfile.readFully(buffer);

				// Store
				Key key = new Key();
				key.setKey(keyBuffer);
				this.tableData.put(key, buffer);
			}

		} catch (FileNotFoundException e)
		{
		} catch (IOException e)
		{
			logger.error("Coult not write table file", e);
		} finally
		{
			if (rfile != null)
				try
				{
					rfile.close();
				} catch (IOException e)
				{
					// Do nothing
				}
		}
	}

	public void write(String file)
	{
		RandomAccessFile rfile = null;
		try
		{
			// Delete the file if it exists
			File check = new File(file);
			if (check.exists())
				check.delete();

			// Open the file
			rfile = new RandomAccessFile(file, "rw");

			// Get all keys
			Set<Key> keySet = tableData.keySet();
			Key[] keys = keySet.toArray(new Key[0]);

			// Entity offset from number of index entries and the counter
			long entityOffset = (keys.length + 1) * 8 + 8;

			// Wriet the keys and entities
			rfile.seek(entityOffset);
			long[] entityOffsets = new long[keys.length + 1];
			long offsetCounter = entityOffset;
			for (int i = 0; i < keys.length; i++)
			{
				entityOffsets[i] = offsetCounter;

				byte[] entity = tableData.get(keys[i]);
				offsetCounter += entity.length;

				byte[] key = keys[i].getKey();
				offsetCounter += key.length;

				// Write key and entity
				rfile.write(key);
				rfile.write(entity);
			}
			// End of file
			entityOffsets[entityOffsets.length - 1] = offsetCounter;

			// Write the header at the beginning of the file
			rfile.seek(0);

			// Write size of offset index
			long keyLength = entityOffsets.length;
			rfile.write(Bytes.toBytes(keyLength));

			// Write the entitity index
			for (int i = 0; i < entityOffsets.length; i++)
				rfile.write(Bytes.toBytes(entityOffsets[i]));

		} catch (FileNotFoundException e)
		{
			logger.error("Coult not find table file: " + file, e);
		} catch (IOException e)
		{
			logger.error("Coult not write table file", e);
		} finally
		{
			if (rfile != null)
				try
				{
					rfile.close();
				} catch (IOException e)
				{
					// Do nothing
				}
		}
	}

	public Collection<byte[]> getAll()
	{
		return tableData.values();
	}

	public byte[] get(Key key)
	{
		return tableData.get(key);
	}

	public void add(Key key, byte[] value)
	{
		tableData.put(key, value);
	}

	public boolean remove(Key key)
	{
		if (tableData.containsKey(key))
		{
			tableData.remove(key);
			return true;
		}
		return false;
	}
}
