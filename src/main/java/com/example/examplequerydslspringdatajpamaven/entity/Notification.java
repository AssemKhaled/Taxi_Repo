package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Model of table tc_notifications in DB
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_notifications")
public class Notification extends Attributes{
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "type")
	private String type;
	
	@Column(name = "always")
	private boolean always;

	@Column(name = "calendarid")
	private Long calendarid;

	@Column(name = "notificators")
	private String notificators;
	
	@Column(name = "delete_date")
	private String delete_date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAlways() {
		return always;
	}

	public void setAlways(boolean always) {
		this.always = always;
	}

	public Long getCalendarid() {
		return calendarid;
	}

	public void setCalendarid(Long calendarid) {
		this.calendarid = calendarid;
	}

	public String getNotificators() {
		return notificators;
	}

	public void setNotificators(String notificators) {
		this.notificators = notificators;
	}
	
	
	public String getDelete_date() {
		return delete_date;
	}

	public void setDelete_date(String delete_date) {
		this.delete_date = delete_date;
	}


	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_user_notification",
            joinColumns = { @JoinColumn(name = "notificationid") },
            inverseJoinColumns = { @JoinColumn(name = "userid") }
    )
	private Set<User> userNotification = new HashSet<>();

	public Set<User> getUserNotification() {
		return userNotification;
	}

	public void setUserNotification(Set<User> userNotification) {
		this.userNotification = userNotification;
	}
	
	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "notificationGroup")
    private Set<Group> groups = new HashSet<>();

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
	
	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "notificationDevice")
    private Set<Device> devices = new HashSet<>();

	public Set<Device> getDevices() {
		return devices;
	}

	public void setDevices(Set<Device> devices) {
		this.devices = devices;
	}
	
	
}
