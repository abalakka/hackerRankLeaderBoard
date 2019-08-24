package com.wissen.dto;

import java.util.List;

public class CustomDTO {
	
	List<Model> models;
	String total;
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
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
