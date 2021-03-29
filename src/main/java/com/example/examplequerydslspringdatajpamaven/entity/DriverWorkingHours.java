package com.example.examplequerydslspringdatajpamaven.entity;

/**
 * Bind data from position to fit model
 * @author fuinco
 *
 */
public class DriverWorkingHours {

	private String deviceTime;
	private String positionId;
	private Object attributes;
	private Long deviceId;
	private String driverName;
	private String hours;
	private String deviceName;
	private String startTime;
	private String endTime;


	
	public DriverWorkingHours() {
		
	}
	
	public DriverWorkingHours(String deviceTime, String positionId, Object attributes, Long deviceId, String deviceName) {
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
	public String getPositionId() {
		return positionId;
	}
	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}
	public Object getAttributes() {
		return attributes;
	}
	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}
	public Long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Long deviceId) {
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public DriverWorkingHours(String deviceTime, String positionId, Object attributes, Long deviceId, String driverName,
			String hours, String deviceName, String startTime, String endTime) {
		super();
		this.deviceTime = deviceTime;
		this.positionId = positionId;
		this.attributes = attributes;
		this.deviceId = deviceId;
		this.driverName = driverName;
		this.hours = hours;
		this.deviceName = deviceName;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	
	
}
