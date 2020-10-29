package com.example.examplequerydslspringdatajpamaven.entity;


public class LastPositionData {


	private String servertime;
	
	private String devicetime;
	
	private String fixtime;
		
	private Double latitude;
	
	private Double longitude;
	
	private Float speed;
		
	private Object attributes;
	
	
	public LastPositionData() {
		super();

	}


	public String getServertime() {
		return servertime;
	}


	public void setServertime(String servertime) {
		this.servertime = servertime;
	}


	public String getDevicetime() {
		return devicetime;
	}


	public void setDevicetime(String devicetime) {
		this.devicetime = devicetime;
	}


	public String getFixtime() {
		return fixtime;
	}


	public void setFixtime(String fixtime) {
		this.fixtime = fixtime;
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


	public LastPositionData(String servertime, String devicetime, String fixtime, Double latitude, Double longitude,
			Float speed, Object attributes) {
		super();
		this.servertime = servertime;
		this.devicetime = devicetime;
		this.fixtime = fixtime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.attributes = attributes;
	}

	
	
}
