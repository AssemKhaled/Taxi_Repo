package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model of tc_user_client_geofence used in type 4 to save his geofences
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_user_client_geofence")
public class userClientGeofence {

	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "geofenceid")
	private Long geofenceid;

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

	public Long getGeofenceid() {
		return geofenceid;
	}

	public void setGeofenceid(Long geofenceid) {
		this.geofenceid = geofenceid;
	}
	
	
}
