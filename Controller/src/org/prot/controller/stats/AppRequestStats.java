package org.prot.controller.stats;

public class AppRequestStats
{
	private RpsCounter rpsCounter = new RpsCounter();

	public void handle()
	{
		rpsCounter.count();
	}

	public double getRps()
	{
		return rpsCounter.getRps();
	}
}
