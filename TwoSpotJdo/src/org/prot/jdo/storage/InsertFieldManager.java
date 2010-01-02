package org.prot.jdo.storage;

import org.apache.log4j.Logger;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;
import org.prot.jdo.storage.messages.EntityMessage;
import org.prot.jdo.storage.types.StorageProperty;
import org.prot.jdo.storage.types.StorageType;
import org.prot.storage.Key;

public class InsertFieldManager extends AbstractFieldManager
{
	private static final Logger logger = Logger.getLogger(InsertFieldManager.class);

	private final EntityMessage.Builder entity;

	public InsertFieldManager(EntityMessage.Builder entity)
	{
		this.entity = entity;
	}

	public void storeObjectField(int fieldNumber, Object value)
	{
		if (value == null)
			return;

		if (value instanceof Key)
		{
			Key key = (Key) value;
			String skey = key.toString();
			entity.addProperty(new StorageProperty(fieldNumber + 100, "", StorageType.STRING, skey));
		} else
		{
			throw new NucleusException("Cannot store object fields");
		}
	}

	public void storeStringField(int fieldNumber, String value)
	{
		if (value == null)
			return;

		entity
				.addProperty(new StorageProperty(fieldNumber + 100, fieldNumber + "", StorageType.STRING,
						value));
	}

	public void storeBooleanField(int fieldNumber, boolean value)
	{
		entity.addProperty(new StorageProperty(fieldNumber + 100, fieldNumber + "", StorageType.BOOLEAN,
				value));
	}

	public void storeCharField(int fieldNumber, char value)
	{
		entity.addProperty(new StorageProperty(fieldNumber + 100, fieldNumber + "", StorageType.CHAR, value));
	}

	public void storeByteField(int fieldNumber, byte value)
	{
		entity.addProperty(new StorageProperty(fieldNumber + 100, fieldNumber + "", StorageType.BYTE, value));
	}

	public void storeShortField(int fieldNumber, short value)
	{
		entity
				.addProperty(new StorageProperty(fieldNumber + 100, fieldNumber + "", StorageType.SHORT,
						value));
	}

	public void storeIntField(int fieldNumber, int value)
	{
		entity.addProperty(new StorageProperty(fieldNumber + 100, fieldNumber + "", StorageType.INTEGER,
				value));
	}

	public void storeLongField(int fieldNumber, long value)
	{
		entity.addProperty(new StorageProperty(fieldNumber + 100, fieldNumber + "", StorageType.LONG, value));
	}

	public void storeFloatField(int fieldNumber, float value)
	{
		entity
				.addProperty(new StorageProperty(fieldNumber + 100, fieldNumber + "", StorageType.FLOAT,
						value));
	}

	public void storeDoubleField(int fieldNumber, double value)
	{
		entity
.addProperty(new StorageProperty(fieldNumber + 100, fieldNumber + "", StorageType.DOUBLE,
						value));
	}
}
