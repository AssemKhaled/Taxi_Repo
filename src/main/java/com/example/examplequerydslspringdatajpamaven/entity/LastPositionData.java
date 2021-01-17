package com.example.examplequerydslspringdatajpamaven.entity;

/**
 * Bind data to elm URL by sequence number
 * @author fuinco
 *
 */
public class LastPositionData {

	private String positionId;

	private String servertime;
	
	private String devicetime;
	
	private String fixtime;
		
	private Double latitude;
	
	private Double longitude;
	
	private Double speed;
	
	private Double weight;

		
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


	public Double getWeight() {
		return weight;
	}


	public void setWeight(Double weight) {
		this.weight = weight;
	}


	public LastPositionData(String servertime, String devicetime, String fixtime, Double latitude, Double longitude,
			Double speed,Double weight, Object attributes) {
		super();
		this.servertime = servertime;
		this.devicetime = devicetime;
		this.fixtime = fixtime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.weight = weight;
		this.attributes = attributes;
	}


	public String getPositionId() {
		return positionId;
	}


	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}


	public LastPositionData(String positionId, String servertime, String devicetime, String fixtime, Double latitude,
			Double longitude, Double speed, Double weight, Object attributes) {
		super();
		this.positionId = positionId;
		this.servertime = servertime;
		this.devicetime = devicetime;
		this.fixtime = fixtime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.weight = weight;
		this.attributes = attributes;
	}

	
	
}
