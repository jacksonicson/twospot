package org.prot.jdo.storage.messages.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum StorageType
{
	STRING(0), INTEGER(1), DOUBLE(2), LONG(3), FLOAT(4), CHAR(5), BOOLEAN(6), BYTE(7), SHORT(8);

	private static final Map<Integer, StorageType> lookup = new HashMap<Integer, StorageType>();

	static
	{
		for (StorageType s : EnumSet.allOf(StorageType.class))
			lookup.put(s.getCode(), s);
	}

	private int code;

	public static StorageType fromCode(int code)
	{
		return lookup.get(code);
	}

	public int getCode()
	{
		return code;
	}

	private StorageType(int code)
	{
		this.code = code;
	}
}
