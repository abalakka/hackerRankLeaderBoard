package com.wissen.util;

public class LeadBoardUrlUtil
{
	public String leadboardUrlFor(String questionUrl)
	{
		return "https://www.hackerrank.com/rest/contests/master/challenges/" + questionUrl
				+ "/leaderboard/filter?offset=0&limit=100&include_practice=true&friends=follows&filter_kinds=friends";
	}

	public String questionsUrlFor(String profile)
	{
		return "https://www.hackerrank.com/rest/hackers/" + profile
				+ "/recent_challenges?limit=1000&cursor=&response_version=v1";
	}
}
