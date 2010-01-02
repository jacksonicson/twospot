package org.prot.jdo.storage;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ObjectManager;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

import com.google.protobuf.CodedInputStream;

public class FetchFieldManager extends AbstractFieldManager
{
	private static final Logger logger = Logger.getLogger(FetchFieldManager.class);

	private CodedInputStream input;

	private ClassLoaderResolver clr;

	private ObjectManager om;

	private EntityMessage msg;

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
		EntityMessage.Builder builder = EntityMessage.newBuilder();
		builder.mergeFrom(input);
		this.msg = builder.build();
	}

	public FetchFieldManager(CodedInputStream input, ClassLoaderResolver clr, ObjectManager om)
			throws IOException
	{
		this.input = input;
		this.clr = clr;
		this.om = om;

		parseFrom(input);
	}

	public String fetchStringField(int fieldNumber)
	{
		return null;
	}
}
