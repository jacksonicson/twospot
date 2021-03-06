/**********************************************************************
Copyright (c) 2009 Erik Bengtson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
    ...
 ***********************************************************************/
package org.datanucleus.store.hbase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.datanucleus.StateManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

public class HBaseInsertFieldManager extends AbstractFieldManager
{
	Put put;
	Delete delete;
	StateManager sm;

	public HBaseInsertFieldManager(StateManager sm, Put put, Delete delete)
	{
		this.sm = sm;
		this.put = put;
		this.delete = delete;
	}

	public void storeBooleanField(int fieldNumber, boolean value)
	{
		String familyName = HBaseUtils.getFamilyName(sm.getClassMetaData(), fieldNumber);
		String columnName = HBaseUtils.getQualifierName(sm.getClassMetaData(), fieldNumber);

		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeBoolean(value);
			oos.flush();
			put.add(familyName.getBytes(), columnName.getBytes(), bos.toByteArray());
			oos.close();
			bos.close();
		} catch (IOException e)
		{
			throw new NucleusException(e.getMessage(), e);
		}
	}

	public void storeByteField(int fieldNumber, byte value)
	{
		String familyName = HBaseUtils.getFamilyName(sm.getClassMetaData(), fieldNumber);
		String columnName = HBaseUtils.getQualifierName(sm.getClassMetaData(), fieldNumber);
		put.add(familyName.getBytes(), columnName.getBytes(), new byte[] { value });
	}

	public void storeCharField(int fieldNumber, char value)
	{
		String familyName = HBaseUtils.getFamilyName(sm.getClassMetaData(), fieldNumber);
		String columnName = HBaseUtils.getQualifierName(sm.getClassMetaData(), fieldNumber);
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeChar(value);
			oos.flush();
			put.add(familyName.getBytes(), columnName.getBytes(), bos.toByteArray());
			oos.close();
			bos.close();
		} catch (IOException e)
		{
			throw new NucleusException(e.getMessage(), e);
		}
	}

	public void storeDoubleField(int fieldNumber, double value)
	{
		String familyName = HBaseUtils.getFamilyName(sm.getClassMetaData(), fieldNumber);
		String columnName = HBaseUtils.getQualifierName(sm.getClassMetaData(), fieldNumber);
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeDouble(value);
			oos.flush();
			put.add(familyName.getBytes(), columnName.getBytes(), bos.toByteArray());
			oos.close();
			bos.close();
		} catch (IOException e)
		{
			throw new NucleusException(e.getMessage(), e);
		}
	}

	public void storeFloatField(int fieldNumber, float value)
	{
		String familyName = HBaseUtils.getFamilyName(sm.getClassMetaData(), fieldNumber);
		String columnName = HBaseUtils.getQualifierName(sm.getClassMetaData(), fieldNumber);
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeFloat(value);
			oos.flush();
			put.add(familyName.getBytes(), columnName.getBytes(), bos.toByteArray());
			oos.close();
			bos.close();
		} catch (IOException e)
		{
			throw new NucleusException(e.getMessage(), e);
		}
	}

	public void storeIntField(int fieldNumber, int value)
	{
		String familyName = HBaseUtils.getFamilyName(sm.getClassMetaData(), fieldNumber);
		String columnName = HBaseUtils.getQualifierName(sm.getClassMetaData(), fieldNumber);
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeInt(value);
			oos.flush();
			put.add(familyName.getBytes(), columnName.getBytes(), bos.toByteArray());
			oos.close();
			bos.close();
		} catch (IOException e)
		{
			throw new NucleusException(e.getMessage(), e);
		}
	}

	public void storeLongField(int fieldNumber, long value)
	{
		String familyName = HBaseUtils.getFamilyName(sm.getClassMetaData(), fieldNumber);
		String columnName = HBaseUtils.getQualifierName(sm.getClassMetaData(), fieldNumber);
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeLong(value);
			oos.flush();

			// Prevent oversized uploads
			if (!HBaseUtils.checkFieldSize(bos.size()))
				throw new IOException("Maximum field size exceeded");

			put.add(familyName.getBytes(), columnName.getBytes(), bos.toByteArray());

			oos.close();
			bos.close();
		} catch (IOException e)
		{
			throw new NucleusException(e.getMessage(), e);
		}
	}

	public void storeObjectField(int fieldNumber, Object value)
	{
		String familyName = HBaseUtils.getFamilyName(sm.getClassMetaData(), fieldNumber);
		String columnName = HBaseUtils.getQualifierName(sm.getClassMetaData(), fieldNumber);
		if (value == null)
		{
			delete.deleteColumn(familyName.getBytes(), columnName.getBytes());
		} else
		{
			try
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(value);

				// Prevent oversized uploads
				if (!HBaseUtils.checkFieldSize(bos.size()))
					throw new IOException("Maximum field size exceeded");

				put.add(familyName.getBytes(), columnName.getBytes(), bos.toByteArray());
				oos.close();
				bos.close();
			} catch (IOException e)
			{
				throw new NucleusException(e.getMessage(), e);
			}
		}
	}

	public void storeShortField(int fieldNumber, short value)
	{
		String familyName = HBaseUtils.getFamilyName(sm.getClassMetaData(), fieldNumber);
		String columnName = HBaseUtils.getQualifierName(sm.getClassMetaData(), fieldNumber);
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeShort(value);
			oos.flush();
			put.add(familyName.getBytes(), columnName.getBytes(), bos.toByteArray());
			oos.close();
			bos.close();
		} catch (IOException e)
		{
			throw new NucleusException(e.getMessage(), e);
		}
	}

	public void storeStringField(int fieldNumber, String value)
	{
		String familyName = HBaseUtils.getFamilyName(sm.getClassMetaData(), fieldNumber);
		String columnName = HBaseUtils.getQualifierName(sm.getClassMetaData(), fieldNumber);
		if (value == null)
		{
			delete.deleteColumn(familyName.getBytes(), columnName.getBytes());
		} else
		{
			try
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(value);

				// Prevent oversized uploads
				if (!HBaseUtils.checkFieldSize(bos.size()))
					throw new IOException("Maximum field size exceeded");

				put.add(familyName.getBytes(), columnName.getBytes(), bos.toByteArray());
				oos.close();
				bos.close();
			} catch (IOException e)
			{
				throw new NucleusException(e.getMessage(), e);
			}
		}
	}
}
