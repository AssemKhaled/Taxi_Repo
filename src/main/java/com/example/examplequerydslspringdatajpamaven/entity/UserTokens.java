package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model of table tc_users_tokens in DB
 * @author fuinco
 *
 */
@Entity
@Table(name ="tc_users_tokens")
public class UserTokens {
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "tokenid")
	private String tokenid;

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

	public String getTokenid() {
		return tokenid;
	}

	public void setTokenid(String tokenid) {
		this.tokenid = tokenid;
	}

	

	
	
	
}
