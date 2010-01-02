package org.prot.jdo.storage.messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.prot.jdo.storage.messages.types.IStorageProperty;
import org.prot.jdo.storage.messages.types.StorageProperty;
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

	// Full qualified class name of the entity
	private static final int FIELD_NUMBER_CLASSNAME = 2;
	private String className;

	// List of all index messages
	private static final int FIELD_NUMBER_INDEXMESSAGE = 1;
	private List<IndexMessage> indexMessages = new ArrayList<IndexMessage>();

	// List of all properties
	private List<IStorageProperty> allProperties = new ArrayList<IStorageProperty>();

	// Lookup-table to get a property by its field name
	private Map<String, IStorageProperty> propertiesByName = new HashMap<String, IStorageProperty>();

	/*
	 * Getter & Setter
	 */

	public List<IndexMessage> getIndexMessages()
	{
		return indexMessages;
	}

	public String getClassName()
	{
		return className;
	}

	public IStorageProperty getProperty(String name)
	{
		return propertiesByName.get(name);
	}

	/*
	 * Implementation
	 */

	@Override
	public int getSerializedSize()
	{
		throw new NotImplementedException();
	}

	@Override
	public void writeTo(CodedOutputStream out) throws IOException
	{
		// Write all index messages
		for (IndexMessage index : indexMessages)
			out.writeMessage(FIELD_NUMBER_INDEXMESSAGE, index);

		// Write the classname
		out.writeString(FIELD_NUMBER_CLASSNAME, className);

		// Write all properties
		for (IStorageProperty type : allProperties)
			type.writeTo(out);
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
			Map<Integer, IndexMessage> index = new HashMap<Integer, IndexMessage>();
			while (true)
			{
				tag = input.readTag();
				if (tag == 0)
					break;

				fieldNumber = WireFormat.getTagFieldNumber(tag);
				switch (fieldNumber)
				{
				case 1:
					// Deserialize the index message
					IndexMessage.Builder subBuilder = IndexMessage.newBuilder();
					input.readMessage(subBuilder, extensionRegistry);
					IndexMessage msg = subBuilder.build();

					// Save the index message
					current.indexMessages.add(msg);

					// Update the lookup table
					index.put(msg.getFieldNumber(), msg);
					continue;

				case 2:
					// Read the classname
					current.className = input.readString();
					logger.debug("Class: " + current.className);
					continue;

				default:
					if (fieldNumber >= 100)
					{
						int fieldIndex = StorageProperty.classFieldNumber(fieldNumber);

						// Get index info for this field entry
						IndexMessage indexMsg = index.get(fieldIndex);
						StorageProperty property = new StorageProperty(indexMsg);
						property.mergeFrom(input);

						current.allProperties.add(property);
						current.propertiesByName.put(indexMsg.getFieldName(), property);

					} else
					{
						logger.warn("Skipping field: " + fieldNumber);
						input.skipField(tag);
					}
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
			current.allProperties.add(property);
		}
	}
}
