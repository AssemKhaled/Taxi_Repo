package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Attributes {

	String attributes;

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	
	
}
