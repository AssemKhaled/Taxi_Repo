package com.example.examplequerydslspringdatajpamaven.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Bind data from position to be fit with this model
 * @author fuinco
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceWorkingHours {
	
	private String deviceTime;
	private String positionId;
	private Object attributes;
	private Long deviceId;
	private String deviceName;
	private String hours;
	private String startTime;
	private String endTime;

	
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

	public DeviceWorkingHours() {
		
	}
	
	public DeviceWorkingHours(String deviceTime, String positionId, Object attributes, Long deviceId, String deviceName) {
		super();
		this.deviceTime = deviceTime;
		this.positionId = positionId;
		this.attributes = attributes;
		this.deviceId = deviceId;
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
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = hours;
	}

	public DeviceWorkingHours(String deviceTime, String positionId, Object attributes, Long deviceId, String deviceName,
			String hours, String startTime, String endTime) {
		super();
		this.deviceTime = deviceTime;
		this.positionId = positionId;
		this.attributes = attributes;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.hours = hours;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	
	
	
}
