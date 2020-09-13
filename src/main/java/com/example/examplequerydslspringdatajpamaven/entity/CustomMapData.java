package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.ArrayList;
import java.util.Map;

public class CustomMapData {

	private Long id;
	private String deviceName;
	private String lastUpdate;
	private String positionId;
	private Integer status;
	private Integer vehicleStatus;
	private Integer valid;
	private Integer ignition;
	private Double power;
	private Double operator;
	private Double latitude;
	private Double longitude;
	private Float speed ;

	public CustomMapData() {
		
	}
	
    public CustomMapData(Long id, String deviceName, String lastUpdate, String positionId) {
    	super();
		this.id = id;
		this.deviceName = deviceName;
		this.lastUpdate = lastUpdate;
		this.positionId = positionId;
	}

	public CustomMapData(Long id, String deviceName, String lastUpdate, String positionId, Integer status,
			Integer vehicleStatus, Integer valid, Integer ignition, Double power, Double operator, Double latitude,
			Double longitude, Float speed) {
		super();
		this.id = id;
		this.deviceName = deviceName;
		this.lastUpdate = lastUpdate;
		this.positionId = positionId;
		this.status = status;
		this.vehicleStatus = vehicleStatus;
		this.valid = valid;
		this.ignition = ignition;
		this.power = power;
		this.operator = operator;
		this.latitude = latitude;
		this.longitude = longitude;
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

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getPositionId() {
		return positionId;
	}

	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getVehicleStatus() {
		return vehicleStatus;
	}

	public void setVehicleStatus(Integer vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

	public Integer getIgnition() {
		return ignition;
	}

	public void setIgnition(Integer ignition) {
		this.ignition = ignition;
	}

	public Double getPower() {
		return power;
	}

	public void setPower(Double power) {
		this.power = power;
	}

	public Double getOperator() {
		return operator;
	}

	public void setOperator(Double operator) {
		this.operator = operator;
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



}
