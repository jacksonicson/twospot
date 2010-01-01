package org.prot.jdo.storage;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;
import org.prot.storage.Key;

import com.google.protobuf.CodedOutputStream;

public class StorageFieldManager extends AbstractFieldManager
{
	private static final Logger logger = Logger.getLogger(StorageFieldManager.class);

	private CodedOutputStream codedOut;
	private HashMap<Integer, String> keyMap = new HashMap<Integer, String>();

	public StorageFieldManager(CodedOutputStream codedOut)
	{
		this.codedOut = codedOut;
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
				logger.debug("storing field number: " + fieldNumber);
				codedOut.writeString(fieldNumber, value);
			}
		} catch (IOException e)
		{
			throw new NucleusException("Could not store field", e);
		}
	}
}
