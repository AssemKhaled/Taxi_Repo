package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;

public class LastLocationsList {


	private String id;
	private Date lasttime;
	private Long deviceid;
	private Double latitude;
	private Double longitude;
	private Float speed;
	private Object attributes;
	private Date devicetime;
	private String deviceRK;
	private String driver_RK;
	private Long driverid;
	private String drivername;
	private Float weight;
	private String address;
	private Integer is_offline;
	private String devicename;
	private Long userid;
	private String username;
	private String userRK;
	
	
	
	
	public LastLocationsList() {
		
	}
	
	public LastLocationsList(Long deviceid, String deviceRK, String driver_RK, Long driverid, String drivername,
		    String devicename, Long userid, String username,String userRK) {
		this.deviceid = deviceid;
		this.deviceRK = deviceRK;
		this.driver_RK = driver_RK;
		this.driverid = driverid;
		this.drivername = drivername;
		this.devicename = devicename;
		this.userid = userid;
		this.username = username;
		this.userRK = userRK;
	}
	
	public LastLocationsList(String id, Date lasttime, Long deviceid, Double latitude, Double longitude, Float speed,
			Object attributes, Date devicetime, String deviceRK, String driver_RK, Long driverid, String drivername,
			Float weight, String address, Integer is_offline, String devicename, Long userid, String username,
			String userRK) {
		super();
		this.id = id;
		this.lasttime = lasttime;
		this.deviceid = deviceid;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.attributes = attributes;
		this.devicetime = devicetime;
		this.deviceRK = deviceRK;
		this.driver_RK = driver_RK;
		this.driverid = driverid;
		this.drivername = drivername;
		this.weight = weight;
		this.address = address;
		this.is_offline = is_offline;
		this.devicename = devicename;
		this.userid = userid;
		this.username = username;
		this.userRK = userRK;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getLasttime() {
		return lasttime;
	}
	public void setLasttime(Date lasttime) {
		this.lasttime = lasttime;
	}
	public Long getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(Long deviceid) {
		this.deviceid = deviceid;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Float getSpeed() {
		return speed;
	}
	public void setSpeed(Float speed) {
		this.speed = speed;
	}
	public Object getAttributes() {
		return attributes;
	}
	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}
	public Date getDevicetime() {
		return devicetime;
	}
	public void setDevicetime(Date devicetime) {
		this.devicetime = devicetime;
	}
	public String getDeviceRK() {
		return deviceRK;
	}
	public void setDeviceRK(String deviceRK) {
		this.deviceRK = deviceRK;
	}
	public String getDriver_RK() {
		return driver_RK;
	}
	public void setDriver_RK(String driver_RK) {
		this.driver_RK = driver_RK;
	}
	public Long getDriverid() {
		return driverid;
	}
	public void setDriverid(Long driverid) {
		this.driverid = driverid;
	}
	public String getDrivername() {
		return drivername;
	}
	public void setDrivername(String drivername) {
		this.drivername = drivername;
	}
	public Float getWeight() {
		return weight;
	}
	public void setWeight(Float weight) {
		this.weight = weight;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getIs_offline() {
		return is_offline;
	}
	public void setIs_offline(Integer is_offline) {
		this.is_offline = is_offline;
	}
	public String getDevicename() {
		return devicename;
	}
	public void setDevicename(String devicename) {
		this.devicename = devicename;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserRK() {
		return userRK;
	}
	public void setUserRK(String userRK) {
		this.userRK = userRK;
	}
	
	


}
