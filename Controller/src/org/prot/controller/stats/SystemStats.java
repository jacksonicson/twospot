package org.prot.controller.stats;

import org.apache.log4j.Logger;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarProxyCache;

public class SystemStats
{
	private static final Logger logger = Logger.getLogger(SystemStats.class);

	private SigarProxy sigar;

	public SystemStats()
	{
		Sigar sigarImpl = new Sigar();
		sigar = SigarProxyCache.newInstance(sigarImpl, 100);
	}

	public double getSystemLoad()
	{
		try
		{
			return sigar.getCpuPerc().getUser() + sigar.getCpuPerc().getSys();
		} catch (SigarException e)
		{
			logger.error("Could not load system load average", e);
		}

		return -1;
	}

	public double getProcessLoad()
	{
		try
		{
			long pid = sigar.getPid();
			double myCpu = sigar.getProcCpu(pid).getPercent();
			return myCpu;
		} catch (SigarException e)
		{
			logger.error("Could not load system load average", e);
		}

		return -1;
	}

	public long getFreePhysicalMemorySize()
	{
		try
		{
			return sigar.getMem().getFree();
		} catch (SigarException e)
		{
			logger.error("Culd not load free physical memory size", e);
		}

		return -1;
	}

	public long getTotalPhysicalMemorySize()
	{
		try
		{
			return sigar.getMem().getRam() * 1024 * 1024;
		} catch (SigarException e)
		{
			logger.error("Could not load total physical memory size", e);
		}

		return -1;
	}
}
