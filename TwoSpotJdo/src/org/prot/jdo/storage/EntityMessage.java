package org.prot.jdo.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.prot.jdo.storage.types.IStorageProperty;
import org.prot.storage.NotImplementedException;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.WireFormat;

public class EntityMessage extends AbstractMessageLite
{
	private static final Logger logger = Logger.getLogger(EntityMessage.class);

	private static final EntityMessage defaultInstance = new EntityMessage();

	private List<IndexMessage> indexMessages = new ArrayList<IndexMessage>();

	private String className;

	private Map<String, IStorageProperty> properties = new HashMap<String, IStorageProperty>();

	// TODO: Getters

	@Override
	public int getSerializedSize()
	{
		throw new NotImplementedException();
	}

	@Override
	public void writeTo(CodedOutputStream out) throws IOException
	{
		for (IndexMessage index : indexMessages)
			out.writeMessage(1, index);

		out.writeString(2, className);

		for (Entry<String, IStorageProperty> entry : properties.entrySet())
		{
			IStorageProperty type = entry.getValue();
			type.writeTo(out);
		}
	}

	@Override
	public EntityMessage getDefaultInstanceForType()
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

	public static Builder newBuilder(EntityMessage indexMessage)
	{
		throw new NotImplementedException();
	}

	public static Builder newBuilder()
	{
		return Builder.create();
	}

	public static class Builder extends AbstractMessageLite.Builder<Builder>
	{
		private EntityMessage current;

		private Builder()
		{
		}

		private static Builder create()
		{
			Builder builder = new Builder();
			builder.current = new EntityMessage();
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
			int tag;
			int fieldNumber;
			while (true)
			{
				tag = input.readTag();
				if (tag == 0)
					break;

				fieldNumber = WireFormat.getTagFieldNumber(tag);
				switch (fieldNumber)
				{
				case 1:
					IndexMessage.Builder subBuilder = IndexMessage.newBuilder();
					input.readMessage(subBuilder, extensionRegistry);
					current.indexMessages.add(subBuilder.build());
					continue;
				case 2:
					current.className = input.readString();
					continue;
				default:
					// TODO:
				}
			}

			return this;
		}

		@Override
		public EntityMessage build()
		{
			if (current != null && !isInitialized())
				throw newUninitializedMessageException(current);

			return buildPartial();
		}

		@Override
		public EntityMessage buildPartial()
		{
			if (current == null)
				throw new IllegalStateException("build() has already been called on this Builder.");

			EntityMessage result = current;
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
		public EntityMessage getDefaultInstanceForType()
		{
			return EntityMessage.defaultInstance;
		}

		@Override
		public boolean isInitialized()
		{
			return current.isInitialized();
		}

		public void addAllIndexMessages(List<IndexMessage> toAdd)
		{
			current.indexMessages.addAll(toAdd);
		}

		public void setClassName(String className)
		{
			current.className = className;
		}

		public void addProperty(IStorageProperty property)
		{
			current.properties.put(property.getName(), property);
		}
	}
}
