package org.prot.jdo.storage.messages;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.prot.jdo.storage.messages.types.StorageType;
import org.prot.storage.NotImplementedException;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.WireFormat;

public class IndexMessage extends AbstractMessageLite
{
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(IndexMessage.class);

	// Field number of the storage property in the message
	private int fieldNumber;

	// Name of the storage property at the field number
	private String fieldName;

	// Data type of the storage property at the fieldNumber
	private StorageType fieldType;

	public int getFieldNumber()
	{
		return fieldNumber;
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public StorageType getFieldType()
	{
		return fieldType;
	}

	@Override
	public int getSerializedSize()
	{
		int size = 0;

		// Write the field number
		size += CodedOutputStream.computeInt32Size(1, fieldNumber);

		// Write the field name
		size += CodedOutputStream.computeStringSize(2, fieldName);

		// Write the field type
		size += CodedOutputStream.computeInt32Size(3, fieldType.getCode());

		return size;
	}

	@Override
	public void writeTo(CodedOutputStream out) throws IOException
	{
		// Write the message
		out.writeInt32(1, fieldNumber);
		out.writeString(2, fieldName);
		out.writeInt32(3, fieldType.getCode());
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
			while (true)
			{
				int tag = input.readTag();
				int fieldNumber = WireFormat.getTagFieldNumber(tag);

				if (tag == 0)
					break;

				switch (fieldNumber)
				{
				case 1:
					current.fieldNumber = input.readInt32();
					break;
				case 2:
					current.fieldName = input.readString();
					break;
				case 3:
					current.fieldType = StorageType.fromCode(input.readInt32());
					break;
				default:
					input.skipField(tag);
					break;
				}
			}

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

		public void setFieldType(StorageType fieldType)
		{
			current.fieldType = fieldType;
		}
	}
}
