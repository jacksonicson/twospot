package org.prot.controller.app;

public enum AppState
{
	NEW(AppLife.FIRST),

	STARTING(AppLife.FIRST),

	ONLINE(AppLife.FIRST),

	BANNED(AppLife.SECOND),

	KILLED(AppLife.SECOND),

	DEPLOYED(AppLife.SECOND),

	DEAD(AppLife.SECOND);

	private final AppLife life;

	private AppState(AppLife life)
	{
		this.life = life;
	}

	public AppLife getLife()
	{
		return this.life;
	}
}
