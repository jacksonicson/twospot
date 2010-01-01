package org.prot.jdo.storage;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;
import org.prot.storage.Key;

import com.google.protobuf.CodedOutputStream;

public class StorageInsertFieldManager extends AbstractFieldManager
{
	private static final Logger logger = Logger.getLogger(StorageInsertFieldManager.class);

	private CodedOutputStream codedOut;
	private HashMap<String, byte[]> index;
	private AbstractClassMetaData acmd;

	public StorageInsertFieldManager(CodedOutputStream codedOut, HashMap<String, byte[]> index,
			AbstractClassMetaData acmd)
	{
		this.codedOut = codedOut;
		this.index = index;
		this.acmd = acmd;
	}

	public void storeObjectField(int fieldNumber, Object value)
	{
		logger.debug("Storing an object field: " + fieldNumber);

		if (value instanceof Key)
		{
			logger.debug("Storing a key field");
		} else
		{

		}
	}

	public void storeStringField(int fieldNumber, String value)
	{
		logger.debug("Storing a string field");

		try
		{
			if (value != null)
			{
				String name = acmd.getMetaDataForManagedMemberAtPosition(fieldNumber).getName();
				logger.debug("Field name: " + name);
				index.put(name, value.getBytes());

				logger.debug("storing field number: " + fieldNumber);
				codedOut.writeString(fieldNumber + 100, "aslkdfjasjfklasdjfkaösdjfklasdjfklasdjf");
				logger.debug("Write DONE");
			}
		} catch (IOException e)
		{
			logger.error("", e);
			throw new NucleusException("Could not store field", e);
		}
	}
}
