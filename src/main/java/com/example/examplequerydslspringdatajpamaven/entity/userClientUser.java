package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tc_user_client_user")
public class userClientUser {
	
	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "manageduserid")
	private Long manageduserid;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getManageduserid() {
		return manageduserid;
	}

	public void setManageduserid(Long manageduserid) {
		this.manageduserid = manageduserid;
	}
	
	
}
