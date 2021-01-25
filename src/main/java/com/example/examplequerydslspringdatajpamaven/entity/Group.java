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
 * Model of table tc_groups in DB
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_groups")
public class Group extends Attributes{
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "name") 
	private String name;
	
	@Column(name = "groupid")
	private Long groupid;
	
	@Column(name = "is_deleted")
	private String is_deleted=null;

	@Column(name = "type")
	private String type;
	
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

	public Long getGroupid() {
		return groupid;
	}

	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}

	public String getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(String is_deleted) {
		this.is_deleted = is_deleted;
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}



	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_user_group",
            joinColumns = { @JoinColumn(name = "groupid") },
            inverseJoinColumns = { @JoinColumn(name = "userid") }
    )
	private Set<User> userGroup = new HashSet<>();

	public Set<User> getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(Set<User> userGroup) {
		this.userGroup = userGroup;
	}
	
	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_group_driver",
            joinColumns = { @JoinColumn(name = "groupid") },
            inverseJoinColumns = { @JoinColumn(name = "driverid") }
    )
	private Set<Driver> driverGroup = new HashSet<>();

	public Set<Driver> getDriverGroup() {
		return driverGroup;
	}

	public void setDriverGroup(Set<Driver> driverGroup) {
		this.driverGroup = driverGroup;
	}
	
	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_group_geofence",
            joinColumns = { @JoinColumn(name = "groupid") },
            inverseJoinColumns = { @JoinColumn(name = "geofenceid") }
    )
	private Set<Geofence> geofenceGroup = new HashSet<>();

	public Set<Geofence> getGeofenceGroup() {
		return geofenceGroup;
	}

	public void setGeofenceGroup(Set<Geofence> geofenceGroup) {
		this.geofenceGroup = geofenceGroup;
	}

	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_group_device",
            joinColumns = { @JoinColumn(name = "groupid") },
            inverseJoinColumns = { @JoinColumn(name = "deviceid") }
    )
	private Set<Device> deviceGroup = new HashSet<>();

	public Set<Device> getDeviceGroup() {
		return deviceGroup;
	}

	public void setDeviceGroup(Set<Device> deviceGroup) {
		this.deviceGroup = deviceGroup;
	}
	
	
	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_group_notification",
            joinColumns = { @JoinColumn(name = "groupid") },
            inverseJoinColumns = { @JoinColumn(name = "notificationid") }
    )
	private Set<Notification> notificationGroup = new HashSet<>();

	public Set<Notification> getNotificationGroup() {
		return notificationGroup;
	}

	public void setNotificationGroup(Set<Notification> notificationGroup) {
		this.notificationGroup = notificationGroup;
	}
	
	
	@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_group_attribute",
            joinColumns = { @JoinColumn(name = "groupid") },
            inverseJoinColumns = { @JoinColumn(name = "attributeid") }
    )
	private Set<Attribute> attributeGroup = new HashSet<>();

	public Set<Attribute> getAttributeGroup() {
		return attributeGroup;
	}

	public void setAttributeGroup(Set<Attribute> attributeGroup) {
		this.attributeGroup = attributeGroup;
	}
	
	
	
	
}
