package org.prot.storage.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.apache.log4j.Logger;
import org.datanucleus.ClassLoaderResolver;

public class MyStream extends ObjectInputStream
{
	private static final Logger logger = Logger.getLogger(MyStream.class);

	public MyStream(InputStream in, ClassLoaderResolver cls) throws IOException
	{
		super(in);
		assert (cls != null);
		setClassLoader(cls);
	}

	private ClassLoaderResolver classLoader;

	public void setClassLoader(ClassLoaderResolver cls)
	{
		this.classLoader = cls;
	}

	protected Class<?> resolveClass(ObjectStreamClass desc)
	{
		return classLoader.classForName(desc.getName());
	}
}