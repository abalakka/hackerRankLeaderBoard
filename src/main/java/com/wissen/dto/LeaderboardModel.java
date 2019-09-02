package com.wissen.dto;

public class LeaderboardModel {
	private String rank;
	private String hacker;
	private Long time_taken;
	
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getHacker() {
		return hacker;
	}
	public void setHacker(String hacker) {
		this.hacker = hacker;
	}
	
	public Long getTime_taken() {
		return time_taken;
	}
	public void setTime_taken(Long time_taken) {
		this.time_taken = time_taken;
	}
	@Override
	public String toString() {
		return "LeaderboardModel [rank=" + rank + ", hacker=" + hacker + ", timestamp=" + time_taken + "]";
	}
	
	
}
