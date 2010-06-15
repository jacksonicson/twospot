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
package org.prot.util.zookeeper;

/**
 * States in which a job can be
 * 
 * @author Andreas Wolke
 * 
 */
public enum JobState {
	// Ok
	OK,

	// Immediately execute this job again
	RETRY,

	// Execute this job again, after all other jobs have been executed
	RETRY_LATER,

	// This job has failed, don't execute it again
	FAILED,
}
