package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tc_task")
public class Task {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "name") 
	private String name;
	
	@Column(name = "email") 
	private String email;
	
	@Column(name = "phone") 
	private String phone;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
