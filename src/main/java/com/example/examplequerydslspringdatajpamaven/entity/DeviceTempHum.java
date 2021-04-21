package com.example.examplequerydslspringdatajpamaven.entity;

public class DeviceTempHum {

	private String id;
	private Long deviceId;
	private String deviceName;
	private String driverName;
	private String servertime;
	private Object attributes;
	private Double speed;
	private Double weight;
	private Double temperature;
	private Double humidity;
	
	
	
	public DeviceTempHum() {
		super();
	}
	public DeviceTempHum(String id, Long deviceId, String deviceName, String driverName, String servertime,
			Object attributes, Double speed, Double weight, Double temperature, Double humidity) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.driverName = driverName;
		this.servertime = servertime;
		this.attributes = attributes;
		this.speed = speed;
		this.weight = weight;
		this.temperature = temperature;
		this.humidity = humidity;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
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
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Double getTemperature() {
		return temperature;
	}
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
	public Double getHumidity() {
		return humidity;
	}
	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}
	
	
}
