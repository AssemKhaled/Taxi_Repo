package com.example.examplequerydslspringdatajpamaven.entity;

public class CustomPositions {
	private String id;
	private Long deviceId;
	private String deviceName;
	private String driverName;
	private String servertime;
	private Object attributes;
	private Double speed;
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
	public CustomPositions(String id, String deviceName, Object attributes) {
		this.id = id;
		this.deviceName = deviceName;
		this.attributes = attributes;
	}
	public CustomPositions(String id, String deviceName,String driverName, Object attributes) {
		this.id = id;
		this.deviceName = deviceName;
		this.driverName = driverName;
		this.attributes = attributes;
	}
	public CustomPositions(String id, String deviceName, String servertime, Object attributes, Double speed) {
		this.id = id;
		this.deviceName = deviceName;
		this.servertime = servertime;
		this.attributes = attributes;
		this.speed = speed;
	}
	
	
	public CustomPositions(String id, Long deviceId, String deviceName, String driverName, String servertime,
			Object attributes, Double speed, String weight, String sensor1, String sensor2) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.driverName = driverName;
		this.servertime = servertime;
		this.attributes = attributes;
		this.speed = speed;
		this.weight = weight;
		this.sensor1 = sensor1;
		this.sensor2 = sensor2;
	}
	public Long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public Object getAttributes() {
		return attributes;
	}
	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}
	public Double getSpeed() {
		return speed;
	}
	public void setSpeed(Double speed) {
		this.speed = speed;
	}
	
	
}
