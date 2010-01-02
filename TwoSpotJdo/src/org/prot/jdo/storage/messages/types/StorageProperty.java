package org.prot.jdo.storage.messages.types;

import java.io.IOException;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.datanucleus.exceptions.NucleusException;
import org.prot.jdo.storage.messages.IndexMessage;
import org.prot.storage.Key;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class StorageProperty implements IStorageProperty
{
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(StorageProperty.class);

	// Field number in the message
	private int fieldNumber;

	// Type of the property value
	private StorageType type;

	// Value
	private Object value;

	// Binary value
	private byte[] bValue;

	// The fieldNumber of a storage property is the fieldNumber of the property
	// in the persistent class plus an offset
	private static final int INDEX_OFFSET = 100;

	public static final int messageFieldNumber(final int classFieldNumber)
	{
		return classFieldNumber + INDEX_OFFSET;
	}

	public static final int classFieldNumber(final int messageFieldNumber)
	{
		return messageFieldNumber - INDEX_OFFSET;
	}

	public StorageProperty(IndexMessage indexMsg)
	{
		this.fieldNumber = indexMsg.getFieldNumber();
		this.type = indexMsg.getFieldType();
	}

	public StorageProperty(int classFieldNumber, StorageType type, Object value)
	{
		this.fieldNumber = messageFieldNumber(classFieldNumber);
		this.type = type;
		this.value = value;
	}

	@Override
	public StorageType getType()
	{
		return type;
	}

	@Override
	public Object getValue()
	{
		return this.value;
	}

	@Override
	public byte[] getValueAsBytes()
	{
		return this.bValue;
	}

	public static final StorageType newType(Class<?> type)
	{
		if (type == String.class)
		{
			return StorageType.STRING;
		} else if (type == Integer.class)
		{
			return StorageType.INTEGER;
		} else if (type == Long.class)
		{
			return StorageType.LONG;
		} else if (type == Double.class)
		{
			return StorageType.DOUBLE;
		} else if (type == Boolean.class)
		{
			return StorageType.BOOLEAN;
		} else if (type == Key.class)
		{
			return StorageType.STRING;
		} else
		{
			throw new NucleusException("Unknown field type");
		}
	}

	@Override
	public void mergeFrom(CodedInputStream input) throws IOException
	{
		switch (type)
		{
		case STRING:
			value = input.readString();
			bValue = Bytes.toBytes((String) value);
			break;
		case BOOLEAN:
			value = input.readBool();
			bValue = Bytes.toBytes((Boolean) value);
			break;
		case BYTE:
			value = (byte) input.readInt32();
			bValue = Bytes.toBytes((Byte) value);
			break;
		case CHAR:
			value = (char) input.readInt32();
			bValue = Bytes.toBytes((Character) value);
			break;
		case DOUBLE:
			value = input.readDouble();
			bValue = Bytes.toBytes((Double) value);
			break;
		case FLOAT:
			value = input.readFloat();
			bValue = Bytes.toBytes((Float) value);
			break;
		case INTEGER:
			value = input.readInt32();
			bValue = Bytes.toBytes((Integer) value);
			break;
		case LONG:
			value = input.readInt64();
			bValue = Bytes.toBytes((Long) value);
			break;
		case SHORT:
			value = (short) input.readInt32();
			bValue = Bytes.toBytes((Short) value);
			break;
		}
	}

	@Override
	public void writeTo(CodedOutputStream out) throws IOException
	{
		switch (type)
		{
		case STRING:
			out.writeString(fieldNumber, (String) value);
			break;
		case BOOLEAN:
			out.writeBool(fieldNumber, (Boolean) value);
		case BYTE:
			out.writeInt32(fieldNumber, (Byte) value);
		case CHAR:
			out.writeInt32(fieldNumber, ((Character) value));
		case DOUBLE:
			out.writeDouble(fieldNumber, (Double) value);
		case FLOAT:
			out.writeFloat(fieldNumber, (Float) value);
		case INTEGER:
			out.writeInt32(fieldNumber, (Integer) value);
		case LONG:
			out.writeInt64(fieldNumber, (Long) value);
		case SHORT:
			out.writeInt32(fieldNumber, ((Short) value).intValue());
		}
	}
}
