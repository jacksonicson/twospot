/**********************************************************************
Copyright (c) 2009 Erik Bengtson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
    ...
 ***********************************************************************/
package org.prot.stor.hbase;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.datanucleus.StateManager;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

public class HBaseInsertFieldManager extends AbstractFieldManager
{
	Put put;
	Delete delete;
	StateManager sm;

	public HBaseInsertFieldManager(StateManager sm, Put put, Delete delete)
	{
		this.sm = sm;
		this.put = put;
		this.delete = delete;
	}

	public void storeBooleanField(int fieldNumber, boolean value)
	{
		// Never use this method
		assert (false);
	}

	public void storeByteField(int fieldNumber, byte value)
	{
		// Never use this method
		assert (false);
	}

	public void storeCharField(int fieldNumber, char value)
	{
		// Never use this method
		assert (false);
	}

	public void storeDoubleField(int fieldNumber, double value)
	{
		// Never use this method
		assert (false);
	}

	public void storeFloatField(int fieldNumber, float value)
	{
		// Never use this method
		assert (false);
	}

	public void storeIntField(int fieldNumber, int value)
	{
		// Never use this method
		assert (false);
	}

	public void storeLongField(int fieldNumber, long value)
	{
		// Never use this method
		assert (false);
	}

	public void storeObjectField(int fieldNumber, Object value)
	{
		// Never use this method
		assert (false);
	}

	public void storeShortField(int fieldNumber, short value)
	{
		// Never use this method
		assert (false);
	}

	public void storeStringField(int fieldNumber, String value)
	{
		// Never use this method
		assert (false);
	}
}
