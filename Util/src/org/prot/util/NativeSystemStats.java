package org.prot.util;

public class NativeSystemStats implements ISystemStats {

	@Override
	public long getCpuTotal() {
		return 0;
	}

	@Override
	public long getFreePhysicalMemorySize() {
		return 500;
	}

	@Override
	public long getProcTotal() {
		return 4000;
	}

	@Override
	public double getProcessLoadSinceLastCall() {
		return 0;
	}

	@Override
	public double getSystemIdle() {
		return 0;
	}

	@Override
	public double getSystemLoad() {
		return 0;
	}

	@Override
	public long getTotalPhysicalMemorySize() {
		return 0;
	}
}
