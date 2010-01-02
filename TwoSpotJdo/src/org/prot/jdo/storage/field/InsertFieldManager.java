package org.prot.jdo.storage.field;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;
import org.prot.jdo.storage.messages.EntityMessage;
import org.prot.jdo.storage.messages.types.StorageProperty;
import org.prot.jdo.storage.messages.types.StorageType;
import org.prot.storage.Key;

public class InsertFieldManager extends AbstractFieldManager
{
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
			// Get an string encoded version of the key
			Key key = (Key) value;
			String skey = key.toString();
			
			// Use the property type key
			entity.addProperty(new StorageProperty(fieldNumber, StorageType.KEY, skey));
		} else
		{
			throw new NucleusException("Cannot store object fields");
		}
	}

	public void storeStringField(int fieldNumber, String value)
	{
		if (value == null)
			return;

		entity.addProperty(new StorageProperty(fieldNumber, StorageType.STRING, value));
	}

	public void storeBooleanField(int fieldNumber, boolean value)
	{
		entity.addProperty(new StorageProperty(fieldNumber, StorageType.BOOLEAN, value));
	}

	public void storeCharField(int fieldNumber, char value)
	{
		entity.addProperty(new StorageProperty(fieldNumber, StorageType.CHAR, value));
	}

	public void storeByteField(int fieldNumber, byte value)
	{
		entity.addProperty(new StorageProperty(fieldNumber, StorageType.BYTE, value));
	}

	public void storeShortField(int fieldNumber, short value)
	{
		entity.addProperty(new StorageProperty(fieldNumber, StorageType.SHORT, value));
	}

	public void storeIntField(int fieldNumber, int value)
	{
		entity.addProperty(new StorageProperty(fieldNumber, StorageType.INTEGER, value));
	}

	public void storeLongField(int fieldNumber, long value)
	{
		entity.addProperty(new StorageProperty(fieldNumber, StorageType.LONG, value));
	}

	public void storeFloatField(int fieldNumber, float value)
	{
		entity.addProperty(new StorageProperty(fieldNumber, StorageType.FLOAT, value));
	}

	public void storeDoubleField(int fieldNumber, double value)
	{
		entity.addProperty(new StorageProperty(fieldNumber, StorageType.DOUBLE, value));
	}
}
