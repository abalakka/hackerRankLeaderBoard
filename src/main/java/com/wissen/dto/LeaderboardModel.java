package com.wissen.dto;

public class LeaderboardModel {
	private String rank;
	private String hacker;
	private Long timestamp;
	
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
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return "LeaderboardModel [rank=" + rank + ", hacker=" + hacker + ", timestamp=" + timestamp + "]";
	}
	
	
}
