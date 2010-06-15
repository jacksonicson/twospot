/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.storage.tools;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class SchemaTool
{
	public SchemaTool()
	{
		HBaseConfiguration config = new HBaseConfiguration();
		try
		{
			HBaseAdmin admin = new HBaseAdmin(config);
			SchemaCreator creator = new SchemaCreator(admin);
			creator.checkAndCreate();

		} catch (MasterNotRunningException e)
		{
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String arg[])
	{
		new SchemaTool();
	}
}
