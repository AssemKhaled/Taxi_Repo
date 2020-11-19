package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;

/**
 * Bind data from mysql and mongo together to send for elm
 * @author fuinco
 *
 */
public class LastLocationsList {


	private String id;
	private Date lasttime;
	private Long deviceid;
	private Double latitude;
	private Double longitude;
	private Double speed;
	private Object attributes;
	private Date devicetime;
	private String deviceRK;
	private String driver_RK;
	private Long driverid;
	private String drivername;
	private Double weight;
	private String address;
	private String devicename;

	
	
	public LastLocationsList() {
		
	}
	
	public LastLocationsList(Long deviceid, String deviceRK, String driver_RK, Long driverid, String drivername,
		    String devicename) {
		this.deviceid = deviceid;
		this.deviceRK = deviceRK;
		this.driver_RK = driver_RK;
		this.driverid = driverid;
		this.drivername = drivername;
		this.devicename = devicename;
	}
	
	public LastLocationsList(String id, Date lasttime, Long deviceid, Double latitude, Double longitude, Double speed,
			Object attributes, Date devicetime, String deviceRK, String driver_RK, Long driverid, String drivername,
			Double weight, String address, String devicename) {
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
		this.devicename = devicename;
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
	public Double getSpeed() {
		return speed;
	}
	public void setSpeed(Double speed) {
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
	public String getDevicename() {
		return devicename;
	}
	public void setDevicename(String devicename) {
		this.devicename = devicename;
	}

	
	


}
