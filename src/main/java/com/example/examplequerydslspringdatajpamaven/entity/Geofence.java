package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model of table tc_geofences in DB
 * @author fuinco
 *
 */
@Entity
@Table(name = "tc_geofences")
@JsonIgnoreProperties(value = { "device" })
public class Geofence extends Attributes{
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "area")
	private String area;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "calendarid")
	private Integer calendarid=null;
	
	@Column(name = "is_deleted")
	private Integer is_deleted=null;
	
	@Column(name = "delete_date")
	private String delete_date=null;
	
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "geofence"
    )
    private Set<Device> device = new HashSet<>();
	
	public Geofence() {
		
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getCalendarid() {
		return calendarid;
	}

	public void setCalendarid(Integer calendarid) {
		this.calendarid = calendarid;
	}

	public Integer getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(Integer is_deleted) {
		this.is_deleted = is_deleted;
	}

	public String getDelete_date() {
		return delete_date;
	}

	public void setDelete_date(String delete_date) {
		this.delete_date = delete_date;
	}
	
	@JsonIgnore 
	@ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "tc_user_geofence",joinColumns = { @JoinColumn(name = "geofenceid") },
    inverseJoinColumns = { @JoinColumn(name = "userid") })
    private Set<User> userGeofence = new HashSet<>();

	public Set<User> getUserGeofence() {
		return userGeofence;
	}

	public void setUserGeofence(Set<User> userGeofence) {
		this.userGeofence = userGeofence;
	}

	public Set<Device> getDevice() {
		return device;
	}

	public void setDevice(Set<Device> device) {
		this.device = device;
	}
	

	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "geofenceGroup")
    private Set<Group> groups = new HashSet<>();

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
	 
	 

}
