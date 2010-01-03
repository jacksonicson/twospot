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
	public static final int INDEX_OFFSET = 100;

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
	public Object getValue(StorageType requiredType)
	{
		if (this.type != requiredType)
			return null;

		return getValue();
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
		} else if (type == Key.class)
		{
			return StorageType.KEY;
		} else if (type == Integer.class || type == int.class)
		{
			return StorageType.INTEGER;
		} else if (type == Long.class || type == long.class)
		{
			return StorageType.LONG;
		} else if (type == Double.class || type == double.class)
		{
			return StorageType.DOUBLE;
		} else if (type == Boolean.class || type == boolean.class)
		{
			return StorageType.BOOLEAN;
		} else
		{
			throw new NucleusException("Unknown field type: " + type);
		}
	}

	public static final byte[] bytesFrom(Class<?> cls, Object value)
	{
		StorageType type = newType(cls);
		switch (type)
		{
		case STRING:
			return Bytes.toBytes((String) value);

		case KEY:
			return Bytes.toBytes((String) value);

		case BOOLEAN:
			return Bytes.toBytes((Boolean) value);

		case BYTE:
			return Bytes.toBytes((Byte) value);

		case CHAR:
			return Bytes.toBytes((Character) value);

		case DOUBLE:
			return Bytes.toBytes((Double) value);

		case FLOAT:
			return Bytes.toBytes((Float) value);

		case INTEGER:
			return Bytes.toBytes((Integer) value);

		case LONG:
			return Bytes.toBytes((Long) value);

		case SHORT:
			return Bytes.toBytes((Short) value);

		default:
			throw new NucleusException("Unknown type");
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
		case KEY:
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
		default:
			throw new NucleusException("Unknown type");
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
		case KEY:
			out.writeString(fieldNumber, (String) value);
			break;
		case BOOLEAN:
			out.writeBool(fieldNumber, (Boolean) value);
			break;
		case BYTE:
			out.writeInt32(fieldNumber, (Byte) value);
			break;
		case CHAR:
			out.writeInt32(fieldNumber, ((Character) value));
			break;
		case DOUBLE:
			out.writeDouble(fieldNumber, (Double) value);
			break;
		case FLOAT:
			out.writeFloat(fieldNumber, (Float) value);
			break;
		case INTEGER:
			out.writeInt32(fieldNumber, (Integer) value);
			break;
		case LONG:
			out.writeInt64(fieldNumber, (Long) value);
			break;
		case SHORT:
			out.writeInt32(fieldNumber, ((Short) value).intValue());
			break;
		}
	}
}
