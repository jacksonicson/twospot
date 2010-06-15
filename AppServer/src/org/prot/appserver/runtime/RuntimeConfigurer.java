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
package org.prot.appserver.runtime;

import java.util.Map;

import org.prot.appserver.app.AppInfo;

public interface RuntimeConfigurer
{
	public void configure(AppInfo appInfo, Map<?, ?> yamlObj);
}
