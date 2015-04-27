package com.victor.springsec.example.domain;

public class User
{
	private Long id;
	private String name;
	
	public User(Long pId, String pName)
	{
		this.id = pId;
		this.name = pName;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
