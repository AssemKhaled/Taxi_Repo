package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name ="tc_users",schema="sareb_blue")
public class User {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "email")
	private String email;
	
	@Column (name = "hashedpassword")
	private String password;
	
	@Column (name = "salt")
	private String salt;
	
	@Column (name = "readonly")
	private int readOnly;
	
	@Column (name = "administrator")
	private int administrator;
	
	@Column (name = "map")
	private String map;
	
	@Column (name = "latitude")
	private double latitude;
	
	@Column (name = "longitude")
	private double longitude;
	
	@Column (name = "zoom")
	private int zoom;
	
	@Column (name = "twelvehourformat")
	private int twelveHourFormat;
	
	@JsonProperty
	@Column (name = "attributes")
	private String attributes;
	
	@Column (name = "photo")
	private String photo;
	
	@Column (name = "Iscompany")
	private int isCompany;
	
	
	/*@JsonIgnore 
	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
            name = "tc_user_device",
            joinColumns = { @JoinColumn(name = "userid") },
            inverseJoinColumns = { @JoinColumn(name = "deviceid") }
    )
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Device> devices = new HashSet<>();*/
	 @ManyToMany(
	            fetch = FetchType.LAZY,
	            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
	            mappedBy = "user"
	    )
//	    @OnDelete(action = OnDeleteAction.CASCADE)
	    private Set<Device> devices = new HashSet<>();
	
	
	/*@Column(name="active")
	@CsvBindByName
	private Boolean active;
	@Enumerated(EnumType.STRING)
	private UsersTypes type;*/
	public User() {
		
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public int getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(int readOnly) {
		this.readOnly = readOnly;
	}

	public int getAdministrator() {
		return administrator;
	}

	public void setAdministrator(int administrator) {
		this.administrator = administrator;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public int getTwelveHourFormat() {
		return twelveHourFormat;
	}

	public void setTwelveHourFormat(int twelveHourFormat) {
		this.twelveHourFormat = twelveHourFormat;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public int getIsCompany() {
		return isCompany;
	}

	public void setIsCompany(int isCompany) {
		this.isCompany = isCompany;
	}

    public Set<Device> getDevices() {
		return devices;
	}

	public void setDevices(Set<Device> devices) {
		this.devices = devices;
	}
	

	/*public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public UsersTypes getType() {
		return type;
	}

	public void setType(UsersTypes type) {
		this.type = type;
	}*/

	@ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "userDriver")
    private Set<Driver> drivers = new HashSet<>();


	public Set<Driver> getDrivers() {
		return drivers;
	}
	public void setDrivers(Set<Driver> drivers) {
		this.drivers = drivers;
	}

	
	@ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "userGeofence")
    private Set<Geofence> geofences = new HashSet<>();


	public Set<Geofence> getGeofences() {
		return geofences;
	}
	public void setGeofences(Set<Geofence> geofences) {
		this.geofences = geofences;
	}
	
	@JsonIgnore 
	@ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "tc_user_user",joinColumns = { @JoinColumn(name = "manageduserid") },
    inverseJoinColumns = { @JoinColumn(name = "userid") })
    private Set<User> userUser = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            mappedBy = "userUser")
    private Set<User> users = new HashSet<>();


	public Set<User> getUserUser() {
		return userUser;
	}
	public void setUserUser(Set<User> userUser) {
		this.userUser = userUser;
	}
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	



}
