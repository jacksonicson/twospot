package org.prot.jdo.storage.messages.types;

import java.io.IOException;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public interface IStorageProperty
{
	public StorageType getType();

	public Object getValue();
	
	public byte[] getValueAsBytes();
	
	public void writeTo(CodedOutputStream out) throws IOException;

	public void mergeFrom(CodedInputStream input) throws IOException;
}
