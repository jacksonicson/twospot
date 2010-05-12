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
