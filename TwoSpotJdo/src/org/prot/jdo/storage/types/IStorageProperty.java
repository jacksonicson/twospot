package org.prot.jdo.storage.types;

import java.io.IOException;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public interface IStorageProperty
{
	public StorageType getType();

	public String getName();

	public void writeTo(CodedOutputStream out) throws IOException;

	public void mergeFrom(CodedInputStream input);
}
