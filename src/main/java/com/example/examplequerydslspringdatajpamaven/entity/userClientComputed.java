package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model of tc_user_client_computed used in type 4 to save his attributes
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_user_client_computed")
public class userClientComputed {
	
	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "computedid")
	private Long computedid;

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

	public Long getComputedid() {
		return computedid;
	}

	public void setComputedid(Long computedid) {
		this.computedid = computedid;
	}
	
	
}
