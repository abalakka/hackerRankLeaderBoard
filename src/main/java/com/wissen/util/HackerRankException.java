package com.wissen.util;

public class HackerRankException extends Exception
{
	private static final long serialVersionUID = 1L;

	public HackerRankException(Exception e)
	{
		super(e);
	}

	public HackerRankException(String message)
	{
		super(message);
	}

}
