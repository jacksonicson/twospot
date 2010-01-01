package org.prot.jdo.storage.types;

import java.io.IOException;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class StorageProperty implements IStorageProperty
{
	private int fieldNumber;

	private String name;

	private StorageType type;

	private Object value;

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
	public void mergeFrom(CodedInputStream input)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void writeTo(CodedOutputStream out) throws IOException
	{
		switch (type)
		{
		case STRING:
			out.writeString(fieldNumber, (String) value);
			break;
		}
	}
}
