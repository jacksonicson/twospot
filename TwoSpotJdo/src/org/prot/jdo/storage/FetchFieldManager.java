package org.prot.jdo.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ObjectManager;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;

public class FetchFieldManager extends AbstractFieldManager
{
	private static final Logger logger = Logger.getLogger(FetchFieldManager.class);

	private CodedInputStream input;

	private ClassLoaderResolver clr;

	private ObjectManager om;

	private Object createObject(String className)
	{
		try
		{
			Class<?> cls = clr.classForName(className);
			return cls.newInstance();
		} catch (InstantiationException e)
		{
			logger.error("", e);
		} catch (IllegalAccessException e)
		{
			logger.error("", e);
		}

		return null;
	}

	private void parseFrom(CodedInputStream input) throws IOException
	{
		Map<String, IndexMessage> index = new HashMap<String, IndexMessage>();
		Object obj = null;

//		while (true)
//		{
//			int tag = input.readTag();
//			if (tag == 0)
//				break;
//
//			int fieldNumber = WireFormat.getTagFieldNumber(tag);
//			switch (fieldNumber)
//			{
//			case 1:
//				IndexMessage indexMessage = IndexMessage.parseFrom(input);
//				index.put(indexMessage.getFieldName(), indexMessage);
//				continue;
//
//			case 2:
//				String classname = input.readString();
//				obj = createObject(classname);
//				continue;
//
//			default:
//				// if (fieldNumber > 100 && obj != null)
//				// {
//				// int fieldIndex = fieldNumber - 100;
//				//
//				// // Fill the object
//				// om.
//				// }
//			}
//		}
	}

	public FetchFieldManager(CodedInputStream input, ClassLoaderResolver clr, ObjectManager om)
			throws IOException
	{
		this.input = input;
		this.clr = clr;
		this.om = om;
	}

	public String fetchStringField(int fieldNumber)
	{
		return null;
	}
}
