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

import org.apache.hadoop.hbase.client.Result;
import org.datanucleus.StateManager;
import org.datanucleus.store.fieldmanager.AbstractFieldManager;

/**
 * Es werden keine einzelnen Felder für ein persistentes Objekt geladen. Das objekt
 * wird immer komplett serialisiert!
 * @author Andreas Wolke
 *
 */
public class HBaseFetchFieldManager extends AbstractFieldManager
{
	Result result;
	StateManager sm;

	public HBaseFetchFieldManager(StateManager sm, Result result)
	{
		this.sm = sm;
		this.result = result;
	}

	public boolean fetchBooleanField(int fieldNumber)
	{
		// Never use this method
		assert (false);
		return false;
	}

	public byte fetchByteField(int fieldNumber)
	{
		// Never use this method
		assert (false);
		return 0;
	}

	public char fetchCharField(int fieldNumber)
	{
		// Never use this method
		assert (false);
		return 0;
	}

	public double fetchDoubleField(int fieldNumber)
	{
		// Never use this method
		assert (false);
		return 0d;
	}

	public float fetchFloatField(int fieldNumber)
	{
		// Never use this method
		assert (false);
		return 0f;
	}

	public int fetchIntField(int fieldNumber)
	{
		// Never use this method
		assert (false);
		return 0;
	}

	public long fetchLongField(int fieldNumber)
	{
		// Never use this method
		assert (false);
		return 0l;
	}

	public Object fetchObjectField(int fieldNumber)
	{
		// Never use this method
		assert (false);
		return null;
	}

	public short fetchShortField(int fieldNumber)
	{
		// Never use this method
		assert (false);
		return 0;
	}

	public String fetchStringField(int fieldNumber)
	{
		// Never use this method
		assert (false);
		return null;
	}
}
