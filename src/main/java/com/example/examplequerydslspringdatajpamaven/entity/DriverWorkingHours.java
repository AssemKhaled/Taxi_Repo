package com.example.examplequerydslspringdatajpamaven.entity;

public class DriverWorkingHours {

	private String deviceTime;
	private Long positionId;
	private String attributes;
	private Integer deviceId;
	private String driverName;
	private String hours;
	private String deviceName;

	
	
	public DriverWorkingHours(String deviceTime, Long positionId, String attributes, Integer deviceId, String deviceName) {
		super();
		this.deviceTime = deviceTime;
		this.positionId = positionId;
		this.attributes = attributes;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
	}
	
	

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceTime() {
		return deviceTime;
	}
	public void setDeviceTime(String deviceTime) {
		this.deviceTime = deviceTime;
	}
	public Long getPositionId() {
		return positionId;
	}
	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}
	public String getAttributes() {
		return attributes;
	}
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = hours;
	}

	
}
