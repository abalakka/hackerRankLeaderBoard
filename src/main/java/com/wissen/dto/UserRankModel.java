package com.wissen.dto;

import java.time.LocalDate;
import java.util.Map;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class UserRankModel
{

	private UserModel model;

	private Map<LocalDate, Integer> dateToCount;

	public UserModel getModel()
	{
		return model;
	}

	public void setModel(UserModel model)
	{
		this.model = model;
	}

	public Map<LocalDate, Integer> getDateToCount()
	{
		return dateToCount;
	}

	public void setDateToCount(Map<LocalDate, Integer> dateToCount)
	{
		this.dateToCount = dateToCount;
	}

}
