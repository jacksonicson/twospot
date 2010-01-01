package org.prot.jdo.storage;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.prot.storage.NotImplementedException;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.WireFormat;

public class IndexMessage extends AbstractMessageLite
{
	private static final Logger logger = Logger.getLogger(IndexMessage.class);

	// Field number in the entity message
	private int fieldNumber;

	// The name of the field
	private String fieldName;

	// The type of the field
	private int fieldType;

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

	@Override
	public int getSerializedSize()
	{
		int size = 0;

		size += CodedOutputStream.computeInt32Size(1, fieldNumber);
		size += CodedOutputStream.computeStringSize(2, fieldName);
		size += CodedOutputStream.computeInt32Size(3, fieldType);

		return size;
	}

	@Override
	public void writeTo(CodedOutputStream out) throws IOException
	{
		// Write the message
		out.writeInt32(1, fieldNumber);
		out.writeString(2, fieldName);
		out.writeInt32(3, fieldType);
	}

	private static final IndexMessage defaultInstance = new IndexMessage();

	@Override
	public IndexMessage getDefaultInstanceForType()
	{
		return defaultInstance;
	}

	@Override
	public boolean isInitialized()
	{
		return true;
	}

	@Override
	public Builder newBuilderForType()
	{
		return newBuilder();
	}

	@Override
	public Builder toBuilder()
	{
		return newBuilder(this);
	}

	public static Builder newBuilder(IndexMessage indexMessage)
	{
		throw new NotImplementedException();
	}

	public static Builder newBuilder()
	{
		return Builder.create();
	}

	public static class Builder extends AbstractMessageLite.Builder<Builder>
	{
		private IndexMessage current;

		private Builder()
		{
		}

		private static Builder create()
		{
			Builder builder = new Builder();
			builder.current = new IndexMessage();
			return builder;
		}

		@Override
		public Builder clone()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry)
				throws IOException
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

			current = new IndexMessage();
			current.fieldNumber = fieldNumber;
			current.fieldName = fieldName;
			current.fieldType = fieldType;
			return this;
		}

		@Override
		public IndexMessage build()
		{
			if (current != null && !isInitialized())
				throw newUninitializedMessageException(current);

			return buildPartial();
		}

		@Override
		public IndexMessage buildPartial()
		{
			if (current == null)
				throw new IllegalStateException("build() has already been called on this Builder.");

			IndexMessage result = current;
			this.current = null;
			return result;
		}

		@Override
		public Builder clear()
		{
			this.current = null;
			return this;
		}

		@Override
		public IndexMessage getDefaultInstanceForType()
		{
			return IndexMessage.defaultInstance;
		}

		@Override
		public boolean isInitialized()
		{
			return current.isInitialized();
		}

		public void setFieldNumber(int fieldNumber)
		{
			current.fieldNumber = fieldNumber;
		}

		public void setFieldName(String fieldName)
		{
			current.fieldName = fieldName;
		}

		public void setFieldType(int fieldType)
		{
			current.fieldType = fieldType;
		}
	}
}
