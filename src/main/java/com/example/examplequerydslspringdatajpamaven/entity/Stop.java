package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Stop {
	
	private int deviceId;
	
	private String deviceName;
	
	private Number distance;
	
	private Number averageSpeed;

	private Number maxSpeed;

	private Number spentFuel;

	private Number startOdometer;

	private Number endOdometer;

    private int positionId;
	
	private Number latitude;

	private Number longitude;

	private Number startTime;

	private Number endTime;

	private String address;
	
	private Number duration;

	private Number engineHours;

	public Stop() {
		
	}
	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Number getDistance() {
		return distance;
	}

	public void setDistance(Number distance) {
		this.distance = distance;
	}

	public Number getAverageSpeed() {
		return averageSpeed;
	}

	public void setAverageSpeed(Number averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

	public Number getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(Number maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Number getSpentFuel() {
		return spentFuel;
	}

	public void setSpentFuel(Number spentFuel) {
		this.spentFuel = spentFuel;
	}

	public Number getStartOdometer() {
		return startOdometer;
	}

	public void setStartOdometer(Number startOdometer) {
		this.startOdometer = startOdometer;
	}

	public Number getEndOdometer() {
		return endOdometer;
	}

	public void setEndOdometer(Number endOdometer) {
		this.endOdometer = endOdometer;
	}

	public int getPositionId() {
		return positionId;
	}

	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}

	public Number getLatitude() {
		return latitude;
	}

	public void setLatitude(Number latitude) {
		this.latitude = latitude;
	}

	public Number getLongitude() {
		return longitude;
	}

	public void setLongitude(Number longitude) {
		this.longitude = longitude;
	}

	public Number getStartTime() {
		return startTime;
	}

	public void setStartTime(Number startTime) {
		this.startTime = startTime;
	}

	public Number getEndTime() {
		return endTime;
	}

	public void setEndTime(Number endTime) {
		this.endTime = endTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Number getDuration() {
		return duration;
	}

	public void setDuration(Number duration) {
		this.duration = duration;
	}

	public Number getEngineHours() {
		return engineHours;
	}

	public void setEngineHours(Number engineHours) {
		this.engineHours = engineHours;
	}

	


}
