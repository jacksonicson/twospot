package org.prot.jdo.storage;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

public class IndexMessage
{
	private static final Logger logger = Logger.getLogger(IndexMessage.class);
	
	private final int fieldNumber;
	private final String fieldName;
	private final int fieldType;

	public IndexMessage(int fieldNumber, String fieldName, int fieldType)
	{
		this.fieldNumber = fieldNumber;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}

	public int getFieldNumber()
	{
		return fieldNumber;
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public int getFieldType()
	{
		return fieldType;
	}

	int getSerializedSize()
	{
		int size = 0;

		size += CodedOutputStream.computeInt32Size(1, fieldNumber);
		size += CodedOutputStream.computeStringSize(2, fieldName);
		size += CodedOutputStream.computeInt32Size(3, fieldType);

		return size;
	}

	static IndexMessage parseFrom(CodedInputStream input) throws IOException
	{
		final int length = input.readRawVarint32();
		logger.debug("Length: " + length);
		final int oldLimit = input.pushLimit(length);
		logger.debug("Pushing limit");

		int fieldNumber = 0;
		String fieldName = null;
		int fieldType = 0;

		while (true)
		{
			int tag = input.readTag();

			if (tag == 0)
			{
				break;
			} else if (1 == WireFormat.getTagFieldNumber(tag))
			{
				logger.debug("field number"); 
				fieldNumber = input.readInt32();
			} else if (2 == WireFormat.getTagFieldNumber(tag))
			{
				logger.debug("field name"); 
				fieldName = input.readString();
			} else if (3 == WireFormat.getTagFieldNumber(tag))
			{
				logger.debug("field type");
				fieldType = input.readInt32();
			} else
			{
				input.skipField(tag);
			}
		}

		input.checkLastTagWas(0);
		input.popLimit(oldLimit);

		return new IndexMessage(fieldNumber, fieldName, fieldType);
	}

	void writeTo(int messageFieldNumber, CodedOutputStream out) throws IOException
	{
		// Write the tag
		int size = getSerializedSize();
		out.writeTag(messageFieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED);
		logger.debug("Size: " + size);
		out.writeRawVarint32(size);

		// Write the message
		out.writeInt32(1, fieldNumber);
		out.writeString(2, fieldName);
		out.writeInt32(3, fieldType);
	}
}
