package org.prot.controller.app;

public class AppProcess
{
	private transient Process process;
	
	public Process getProcess()
	{
		return this.process; 
	}
	
	public void setProcess(Process process)
	{
		this.process = process;
	}
}
