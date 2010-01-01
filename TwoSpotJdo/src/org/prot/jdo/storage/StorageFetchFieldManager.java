package org.prot.jdo.storage;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

import com.google.protobuf.CodedInputStream;

public class StorageFetchFieldManager extends AbstractFieldManager
{
	private static final Logger logger = Logger.getLogger(StorageFetchFieldManager.class);

	private CodedInputStream input;

	private ClassLoaderResolver clr;

	public Object get() throws IOException
	{
		while (true)
		{
			int tag = input.readTag();
			if (tag == 0)
				break;

			if(tag == 1)
				break;
			
			if (tag >= 100)
			{
//				for (int test : memberPositions)
//					if (test + 100 == tag)
//					{
//						AbstractMemberMetaData ammd = acmd.getMetaDataForMemberAtRelativePosition(test);
//						logger.debug("Restoring field " + ammd.getName());
//						continue;
//					}
			}

			logger.debug("skipping field");
			input.skipField(tag);
		}

		return null;
	}

	public StorageFetchFieldManager(CodedInputStream input, ClassLoaderResolver clr) throws IOException
	{
		this.input = input;
		this.clr = clr;
	}

	public String fetchStringField(int fieldNumber)
	{
		return null;
	}

}
