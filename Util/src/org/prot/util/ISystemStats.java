package org.prot.util;

public interface ISystemStats {
	public double getProcessLoadSinceLastCall(); 
	public double getSystemIdle();
	public double getSystemLoad();
	public long getCpuTotal();
	public long getProcTotal();
	public long getTotalPhysicalMemorySize(); 
	public long getFreePhysicalMemorySize();
}
