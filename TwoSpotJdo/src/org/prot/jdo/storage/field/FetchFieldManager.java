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
	private static final Logger logger = Logger.getLogger(FetchFieldManager.class);

	private EntityMessage msg;

	private AbstractClassMetaData acmd;

	public FetchFieldManager(CodedInputStream input, ObjectManager om, ClassLoaderResolver clr)
			throws IOException
	{
		parseFrom(input);
		this.acmd = om.getMetaDataManager().getMetaDataForClass(getClassName(), clr);
	}

	public String getClassName()
	{
		return msg.getClassName();
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
		if(property != null)
		{
			if(property.getType() == StorageType.KEY)
			{
				Key key = new Key((String)property.getValue());
				return key;
			}
			else
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
			return (String) property.getValue();

		return null;
	}

	public double fetchDoubleField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
			return (Double) property.getValue();

		return 0;
	}

	public float fetchFloatField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
			return (Float) property.getValue();

		return 0;
	}

	public long fetchLongField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
			return (Long) property.getValue();

		return 0;
	}

	public int fetchIntField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
			return (Integer) property.getValue();

		return 0;
	}

	public short fetchShortField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
			return (Short) property.getValue();

		return 0;
	}

	public byte fetchByteField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
			return (Byte) property.getValue();

		return 0;
	}

	public char fetchCharField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
			return (Character) property.getValue();

		return 0;
	}

	public boolean fetchBooleanField(int fieldNumber)
	{
		IStorageProperty property = getProperty(fieldNumber);
		if (property != null)
			return (Boolean) property.getValue();

		return false;
	}
}
