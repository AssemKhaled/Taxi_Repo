package com.example.examplequerydslspringdatajpamaven.entity;

/**
 * Bind data from summary report in this model
 * @author fuinco
 *
 */
public class SummaryReport {
	
	private String averageSpeed;
	private Long deviceId;
	private String deviceName;
	private String distance;
	private String endOdometer;
	private String engineHours;
	private String maxSpeed;
	private String spentFuel;
	private String startOdometer;
	private String driverName;
	
	
	
	public SummaryReport() {
		
	}
	public SummaryReport(String averageSpeed, Long deviceId, String deviceName, String distance, String endOdometer,
			String engineHours, String maxSpeed, String spentFuel, String startOdometer, String driverName) {
		super();
		this.averageSpeed = averageSpeed;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.distance = distance;
		this.endOdometer = endOdometer;
		this.engineHours = engineHours;
		this.maxSpeed = maxSpeed;
		this.spentFuel = spentFuel;
		this.startOdometer = startOdometer;
		this.driverName = driverName;
	}
	public String getAverageSpeed() {
		return averageSpeed;
	}
	public void setAverageSpeed(String averageSpeed) {
		this.averageSpeed = averageSpeed;
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
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getEndOdometer() {
		return endOdometer;
	}
	public void setEndOdometer(String endOdometer) {
		this.endOdometer = endOdometer;
	}
	public String getEngineHours() {
		return engineHours;
	}
	public void setEngineHours(String engineHours) {
		this.engineHours = engineHours;
	}
	public String getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(String maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public String getSpentFuel() {
		return spentFuel;
	}
	public void setSpentFuel(String spentFuel) {
		this.spentFuel = spentFuel;
	}
	public String getStartOdometer() {
		return startOdometer;
	}
	public void setStartOdometer(String startOdometer) {
		this.startOdometer = startOdometer;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	
	
}
