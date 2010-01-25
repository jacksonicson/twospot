package org.prot.jdo.storage.field;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ObjectManager;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;
import org.prot.jdo.storage.messages.EntityMessage;
import org.prot.jdo.storage.messages.types.IStorageProperty;
import org.prot.jdo.storage.messages.types.StorageType;
import org.prot.storage.Key;

import com.google.protobuf.CodedInputStream;

public class FetchFieldManager extends AbstractFieldManager
{
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FetchFieldManager.class);

	private EntityMessage msg;

	private AbstractClassMetaData acmd;

	public FetchFieldManager(CodedInputStream input, ObjectManager om, ClassLoaderResolver clr, Class<?> cls)
			throws IOException
	{
		parseFrom(input);
		this.acmd = om.getMetaDataManager().getMetaDataForClass(cls, clr);
	}

	public FetchFieldManager(CodedInputStream input, ObjectManager om) throws IOException
	{
		parseFrom(input);
		this.acmd = om.getMetaDataManager().getMetaDataForClass(msg.getClassName(), om.getClassLoaderResolver());
	}
	
	public FetchFieldManager(CodedInputStream input, AbstractClassMetaData acmd) throws IOException
	{
		parseFrom(input); 
		this.acmd = acmd;
	}
	
	public String getMessageClass()
	{
		return msg.getClassName();
	}
	
	public AbstractClassMetaData getAcmd()
	{
		return acmd;
	}
	
	private void parseFrom(CodedInputStream input) throws IOException
	{
		EntityMessage.Builder builder = EntityMessage.newBuilder();
		builder.mergeFrom(input);
		this.msg = builder.build();
	}

	private IStorageProperty getProperty(int fieldNumber)
	{
		// Get the name of the field
		String fieldName = acmd.getMetaDataForManagedMemberAtPosition(fieldNumber).getName();

		// The the property by the field name
		IStorageProperty property = msg.getProperty(fieldName);

		// Return the property or null if no property was found
		return property;
	}

	public Object fetchObjectField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
		{
			if (property.getType() == StorageType.KEY)
			{
				String value = (String) property.getValue(StorageType.KEY);
				if (value != null)
				{
					Key key = new Key(value, true);
					return key;
				}
			} else
			{
				throw new NucleusException("Could not fetch object field - unknown type");
			}
		}

		return null;
	}

	public String fetchStringField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
		{
			String value = (String) property.getValue(StorageType.STRING);
			if (value != null)
				return value;
		}

		return null;
	}

	public double fetchDoubleField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
		{
			Double value = (Double) property.getValue(StorageType.DOUBLE);
			if (value != null)
				return value;
		}

		return 0;
	}

	public float fetchFloatField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
		{
			Float value = (Float) property.getValue(StorageType.FLOAT);
			if (value != null)
				return value;
		}

		return 0;
	}

	public long fetchLongField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
		{
			Long value = (Long) property.getValue(StorageType.LONG);
			if (value != null)
				return value;
		}

		return 0;
	}

	public int fetchIntField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
		{
			Integer value = (Integer) property.getValue(StorageType.INTEGER);
			if (value != null)
				return value;
		}

		return 0;
	}

	public short fetchShortField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
		{
			Short value = (Short) property.getValue(StorageType.SHORT);
			if (value != null)
				return value;
		}

		return 0;
	}

	public byte fetchByteField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
		{
			Byte value = (Byte) property.getValue(StorageType.BYTE);
			if (value != null)
				return value;
		}

		return 0;
	}

	public char fetchCharField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
		{
			Character value = (Character) property.getValue(StorageType.CHAR);
			if (value != null)
				return value;
		}

		return 0;
	}

	public boolean fetchBooleanField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
		{
			Boolean value = (Boolean) property.getValue(StorageType.BOOLEAN);
			if (value != null)
				return value;
		}

		return false;
	}
}
