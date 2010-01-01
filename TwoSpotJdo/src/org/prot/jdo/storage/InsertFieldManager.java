package org.prot.jdo.storage;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;
import org.prot.storage.Key;

import com.google.protobuf.CodedOutputStream;

public class InsertFieldManager extends AbstractFieldManager
{
	private static final Logger logger = Logger.getLogger(InsertFieldManager.class);

	private final CodedOutputStream codedOut;

	public InsertFieldManager(CodedOutputStream codedOut)
	{
		this.codedOut = codedOut;
	}

	public void storeObjectField(int fieldNumber, Object value)
	{
		if (value == null)
			return;

		try
		{
			if (value instanceof Key)
			{
				Key key = (Key) value;
				String skey = key.toString();
				codedOut.writeString(fieldNumber + 100, skey);

			} else
			{
				throw new NucleusException("Cannot store object fields");
			}
		} catch (IOException e)
		{
			throw new NucleusException("Could not store string field", e);
		}
	}

	public void storeStringField(int fieldNumber, String value)
	{
		if (value == null)
			return;

		try
		{
			codedOut.writeString(fieldNumber + 100, value);
		} catch (IOException e)
		{
			throw new NucleusException("Could not store string field", e);
		}
	}

	public void storeBooleanField(int fieldNumber, boolean value)
	{
		try
		{
			codedOut.writeBool(fieldNumber + 100, value);
		} catch (IOException e)
		{
			throw new NucleusException("Could not store string field", e);
		}
	}

	public void storeCharField(int fieldNumber, char value)
	{
		try
		{
			codedOut.writeInt32(fieldNumber + 100, value);
		} catch (IOException e)
		{
			throw new NucleusException("Could not store string field", e);
		}
	}

	public void storeByteField(int fieldNumber, byte value)
	{
		try
		{
			codedOut.writeInt32(fieldNumber + 100, value);
		} catch (IOException e)
		{
			throw new NucleusException("Could not store string field", e);
		}
	}

	public void storeShortField(int fieldNumber, short value)
	{
		try
		{
			codedOut.writeInt32(fieldNumber + 100, value);
		} catch (IOException e)
		{
			throw new NucleusException("Could not store string field", e);
		}
	}

	public void storeIntField(int fieldNumber, int value)
	{
		try
		{
			codedOut.writeInt32(fieldNumber + 100, value);
		} catch (IOException e)
		{
			throw new NucleusException("Could not store string field", e);
		}
	}

	public void storeLongField(int fieldNumber, long value)
	{
		try
		{
			codedOut.writeInt64(fieldNumber + 100, value);
		} catch (IOException e)
		{
			throw new NucleusException("Could not store string field", e);
		}
	}

	public void storeFloatField(int fieldNumber, float value)
	{
		try
		{
			codedOut.writeFloat(fieldNumber + 100, value);
		} catch (IOException e)
		{
			throw new NucleusException("Could not store string field", e);
		}
	}

	public void storeDoubleField(int fieldNumber, double value)
	{
		try
		{
			codedOut.writeDouble(fieldNumber + 100, value);
		} catch (IOException e)
		{
			throw new NucleusException("Could not store string field", e);
		}
	}
}
