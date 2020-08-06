package com.example.examplequerydslspringdatajpamaven.entity;

public class LastLocationsList {


	private Long id;
	private String lasttime;
	private Long deviceid;
	private String latitude;
	private String longitude;
	private Double speed;
	private String attributes;
	private String devicetime;
	private String deviceRK;
	private String driver_RK;
	private Long driverid;
	private String drivername;
	private Double weight;
	private String address;
	private Double is_offline;
	private String devicename;
	private Long userid;
	private String username;
	private String userRK;
	
	
	
	
	public LastLocationsList() {
		
	}
	
	public LastLocationsList(Long id, String lasttime, Long deviceid, String latitude, String longitude, Double speed,
			String attributes, String devicetime, String deviceRK, String driver_RK, Long driverid, String drivername,
			Double weight, String address, Double is_offline, String devicename, Long userid, String username,
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLasttime() {
		return lasttime;
	}
	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}
	public Long getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(Long deviceid) {
		this.deviceid = deviceid;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public Double getSpeed() {
		return speed;
	}
	public void setSpeed(Double speed) {
		this.speed = speed;
	}
	public String getAttributes() {
		return attributes;
	}
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	public String getDevicetime() {
		return devicetime;
	}
	public void setDevicetime(String devicetime) {
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
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Double getIs_offline() {
		return is_offline;
	}
	public void setIs_offline(Double is_offline) {
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
