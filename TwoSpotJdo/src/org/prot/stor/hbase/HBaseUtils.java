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

import org.apache.log4j.Logger;

public class HBaseUtils
{
	private static final Logger logger = Logger.getLogger(HBaseUtils.class);

	public static String APP_ID = "null";

	public static void setAppId(String appId)
	{
		APP_ID = appId;
	}
}