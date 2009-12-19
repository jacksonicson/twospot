package org.prot.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;

public class ObjectSerializer
{
	private static final Logger logger = Logger.getLogger(ObjectSerializer.class);

	public Object deserialize(byte[] input)
	{
		if (input == null)
			return null;

		ByteArrayInputStream byteIn = new ByteArrayInputStream(input);
		try
		{
			ObjectInputStream in = new ObjectInputStream(byteIn);
			Object object = in.readObject();
			return object;
		} catch (IOException e)
		{
			logger.error(e);
		} catch (ClassNotFoundException e)
		{
			logger.error(e);
		}

		return null;
	}

	public byte[] serialize(Serializable object)
	{
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out;
		try
		{
			out = new ObjectOutputStream(byteOut);
			out.writeObject(object);
			return byteOut.toByteArray();
		} catch (IOException e)
		{
			logger.error(e);
		}

		return null;
	}
}
