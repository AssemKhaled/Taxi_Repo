package com.example.examplequerydslspringdatajpamaven.entity;

public class CustomPositions {
	private Long id;
	private String deviceName;
	private String driverName;
	private String servertime;
	private String attributes;
	private String speed;
	private String weight;
	private String sensor1;
	private String sensor2;
	
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getSensor1() {
		return sensor1;
	}
	public void setSensor1(String sensor1) {
		this.sensor1 = sensor1;
	}
	public String getSensor2() {
		return sensor2;
	}
	public void setSensor2(String sensor2) {
		this.sensor2 = sensor2;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	public CustomPositions() {
		
	}
	public CustomPositions(Long id, String deviceName, String attributes) {
		this.id = id;
		this.deviceName = deviceName;
		this.attributes = attributes;
	}
	public CustomPositions(Long id, String deviceName,String driverName, String attributes) {
		this.id = id;
		this.deviceName = deviceName;
		this.driverName = driverName;
		this.attributes = attributes;
	}
	public CustomPositions(Long id, String deviceName, String servertime, String attributes, String speed) {
		this.id = id;
		this.deviceName = deviceName;
		this.servertime = servertime;
		this.attributes = attributes;
		this.speed = speed;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getServertime() {
		return servertime;
	}
	public void setServertime(String servertime) {
		this.servertime = servertime;
	}
	public String getAttributes() {
		return attributes;
	}
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	
	
}
