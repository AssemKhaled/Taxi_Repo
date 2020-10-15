package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tc_user_client_notification")
public class userClientNotification {

	@Column(name = "userid")
	private Long userid;
	
	@Column(name = "notificationid")
	private Long notificationid;

	public Long getUserid() {
		return userid;
	}
	
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
	
	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getNotificationid() {
		return notificationid;
	}

	public void setNotificationid(Long notificationid) {
		this.notificationid = notificationid;
	}
	
	
}
