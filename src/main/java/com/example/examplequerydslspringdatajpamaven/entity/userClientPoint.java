package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model of tc_user_client_point used in type 4 to save his points
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_user_client_point")
public class userClientPoint {

	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "pointid")
	private Long pointid;

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

	public Long getPointid() {
		return pointid;
	}

	public void setPointid(Long pointid) {
		this.pointid = pointid;
	}
	
	
}
