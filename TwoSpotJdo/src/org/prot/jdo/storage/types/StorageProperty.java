package org.prot.jdo.storage.types;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class StorageProperty implements IStorageProperty
{
	private static final Logger logger = Logger.getLogger(StorageProperty.class);

	private int fieldNumber;

	private String name;

	private StorageType type;

	private Object value;

	public StorageProperty(int fieldNumber, String name, StorageType type)
	{
		this(fieldNumber, name, type, null);
	}

	public StorageProperty(int fieldNumber, String name, StorageType type, Object value)
	{
		this.fieldNumber = fieldNumber;
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public String getName()
	{
		return name;
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
	public void mergeFrom(CodedInputStream input) throws IOException
	{
		switch (type)
		{
		case STRING:
			value = input.readString();
			break;
		case BOOLEAN:
			value = input.readBool();
			break;
		case BYTE:
			value = (byte) input.readInt32();
			break;
		case CHAR:
			value = (char) input.readInt32();
			break;
		case DOUBLE:
			value = input.readDouble();
			break;
		case FLOAT:
			value = input.readFloat();
			break;
		case INTEGER:
			value = input.readInt32();
			break;
		case LONG:
			value = input.readInt64();
			break;
		case SHORT:
			value = (short) input.readInt32();
			break;
		}
	}

	@Override
	public void writeTo(CodedOutputStream out) throws IOException
	{
		logger.debug("Writing property");

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
