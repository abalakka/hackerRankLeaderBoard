package com.wissen.dto;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class LeaderboardModel implements Comparable<LeaderboardModel>
{
	private String rank;
	private String hacker;
	@EqualsAndHashCode.Exclude
	private Long time_taken;

	public String getRank()
	{
		return rank;
	}

	public void setRank(String rank)
	{
		this.rank = rank;
	}

	public String getHacker()
	{
		return hacker;
	}

	public void setHacker(String hacker)
	{
		this.hacker = hacker;
	}

	public Long getTime_taken()
	{
		return time_taken;
	}

	public void setTime_taken(Long time_taken)
	{
		this.time_taken = time_taken;
	}

	@Override
	public String toString()
	{
		return "LeaderboardModel [rank=" + rank + ", hacker=" + hacker + ", timestamp=" + time_taken + "]";
	}

	@Override
	public int compareTo(LeaderboardModel o)
	{
		return this.hacker.compareTo(o.hacker);
	}

}
