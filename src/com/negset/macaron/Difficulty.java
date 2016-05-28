package com.negset.macaron;

public enum Difficulty
{
	EASY("#EASY"),
	NORMAL("#NORMAL"),
	HARD("#HARD"),
	LUNATIC("#LUNATIC");

	private String command;

	Difficulty(String command)
	{
		this.command = command;
	}

	public String getCommand()
	{
		return command;
	}
}
