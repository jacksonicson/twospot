package org.prot.jdo.storage;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ObjectManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;

public class StorageFetchFieldManager extends AbstractFieldManager
{
	private static final Logger logger = Logger.getLogger(StorageFetchFieldManager.class);

	private CodedInputStream input;

	private ClassLoaderResolver clr;

	private ObjectManager om;

	public Object get() throws IOException
	{
		logger.debug("getting object");

		Object instance = null;
		Class cls = null;

		int[] memberPos = null;

		main: while (true)
		{

			int tag = input.readTag();
			logger.debug("tag: " + tag);

			if (tag == 0)
			{
				logger.debug("done with this entity");
				break;
			}

			int field = WireFormat.getTagFieldNumber(tag);
			if (field == 2)
			{
				String className = input.readString();
				logger.debug("Classname is: " + className);
				cls = clr.classForName(className);

				memberPos = om.getMetaDataManager().getMetaDataForClass(cls, clr).getAllMemberPositions();

				logger.debug("Member positionts: " + memberPos);
				logger.debug("class resolved: " + cls.getName());

				try
				{
					instance = cls.newInstance();
				} catch (InstantiationException e)
				{
					e.printStackTrace();
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
				continue;
			}

			if (field >= 100)
			{
				logger.debug("Field detected: " + field);
				if (cls == null)
				{
					logger.debug("class is still null - we did not find a class name until now!");

				} else
				{
					for (int test : memberPos)
						if (test + 100 == field)
						{
							AbstractMemberMetaData ammd = om.getMetaDataManager().getMetaDataForClass(cls,
									clr).getMetaDataForMemberAtRelativePosition(test);
							logger.debug("Restoring field " + ammd.getName());

							Class type = ammd.getType();
							if (type == String.class)
							{
								logger.debug("String value is: " + input.readString());
								continue main;
							}
						}
				}
			}

			logger.debug("skipping field");
			input.skipField(tag);
		}

		return null;
	}

	public StorageFetchFieldManager(CodedInputStream input, ClassLoaderResolver clr, ObjectManager om)
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
