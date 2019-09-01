package com.wissen.dto;

import java.util.List;

public class QuestionsDTO {
	
	List<QuestionsModel> models;
	String total;
	public List<QuestionsModel> getModels() {
		return models;
	}
	public void setModels(List<QuestionsModel> models) {
		this.models = models;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	@Override
	public String toString() {
		return "CustomDTO [total=" + total + "]";
	}
	
	

}
