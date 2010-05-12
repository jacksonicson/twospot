package org.prot.util.zookeeper;

/**
 * States in which a job can be
 * 
 * @author Andreas Wolke
 * 
 */
public enum JobState {
	OK,

	RETRY,

	RETRY_LATER,

	FAILED,
}
