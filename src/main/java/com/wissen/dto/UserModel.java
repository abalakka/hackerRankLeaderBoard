package com.wissen.dto;

public class UserModel
{
	private String rank;
	private String profile;
	private String hacker;
	private String college = "";
	private Long time_taken;
	private int unSolvedReqdQuestions;

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

	public String getCollege()
	{
		return college;
	}

	public void setCollege(String college)
	{
		this.college = college;
	}

	public String getProfile()
	{
		return profile;
	}

	public void setProfile(String profile)
	{
		this.profile = profile;
	}

	public int getUnSolvedReqdQuestions() { return unSolvedReqdQuestions; }

	public void setUnSolvedReqdQuestions(int unSolvedReqdQuestions) {
		this.unSolvedReqdQuestions = unSolvedReqdQuestions;
	}
}
