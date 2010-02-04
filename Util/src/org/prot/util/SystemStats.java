package org.prot.util;

import org.apache.log4j.Logger;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;

public class SystemStats
{
	private static final Logger logger = Logger.getLogger(SystemStats.class);

	private SigarProxy sigar;

	public SystemStats()
	{
		Sigar sigarImpl = new Sigar();
		sigar = sigarImpl;
	}

	private long buffProcessLoadProcTotal;
	private long buffProcessLoadSysTotal;

	public double getProcessLoadSinceLastCall()
	{
		try
		{
			// Process load
			ProcCpu procCpu = sigar.getProcCpu(sigar.getPid());

			long procTotal = procCpu.getTotal();
			long sysTotal = sigar.getCpu().getTotal();

			long procDiff = procTotal - buffProcessLoadProcTotal;
			long sysDiff = sysTotal - buffProcessLoadSysTotal;

			double mine = (double) procDiff / (double) sysDiff;

			this.buffProcessLoadProcTotal = procTotal;
			this.buffProcessLoadSysTotal = sysTotal;

			return mine;
		} catch (SigarException e)
		{
			logger.error("Could not load system load average", e);
		}

		return -1;
	}

	private long buffSystemIdleIdle;
	private long buffSystemIdleSysTotalIdle;

	public double getSystemIdle()
	{
		try
		{
			long sysTotal = sigar.getCpu().getTotal();
			long sysDiff = sysTotal - buffSystemIdleSysTotalIdle;
			this.buffSystemIdleSysTotalIdle = sysTotal;

			long idle = sigar.getCpu().getIdle() + sigar.getCpu().getWait();
			long idleDiff = idle - buffSystemIdleIdle;
			this.buffSystemIdleIdle = idle;

			double idleProc = (double) idleDiff / (double) sysDiff;

			return idleProc;
		} catch (SigarException e)
		{
			logger.error("Could not load idle average", e);
		}

		return -1;
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

	public long getCpuTotal()
	{
		try
		{
			return sigar.getCpu().getTotal();
		} catch (SigarException e)
		{
			logger.error("SigarException", e);
		}

		return -1;
	}

	public long getProcTotal()
	{
		try
		{
			return sigar.getProcCpu(sigar.getPid()).getTotal();
		} catch (SigarException e)
		{
			logger.error("SigarException", e);
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
