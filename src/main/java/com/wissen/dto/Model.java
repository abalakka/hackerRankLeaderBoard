package com.wissen.dto;

public class Model {
	String id;
	String created_at;
	String name;
	String ch_slug;
	String dynamic;
	String con_slug;
	String url;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCh_slug() {
		return ch_slug;
	}
	public void setCh_slug(String ch_slug) {
		this.ch_slug = ch_slug;
	}
	public String getDynamic() {
		return dynamic;
	}
	public void setDynamic(String dynamic) {
		this.dynamic = dynamic;
	}
	public String getCon_slug() {
		return con_slug;
	}
	public void setCon_slug(String con_slug) {
		this.con_slug = con_slug;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	@Override
	public String toString() {
		return "Model [created_at=" + created_at + ", name=" + name + "]";
	}
	
	
}
