package com.example.examplequerydslspringdatajpamaven.entity;


/**
 * Bind data of stop report in this model 
 * @author fuinco
 *
 */
public class StopReport {
	
	private String address;
	private String averageSpeed;
	private Long deviceId;
	private String deviceName;
	private String distance;
	private String duration;
	private String endOdometer;
	private String endTime;
	private String engineHours;
	private String latitude;
	private String longitude;
	private String maxSpeed;
	private String positionId;
	private String spentFuel;
	private String startOdometer;
	private String startTime;
	private String driverName;
	private String driverUniqueId;
	
	
	public StopReport() {
		
	}
	public StopReport(String address, String averageSpeed, Long deviceId, String deviceName, String distance,
			String duration, String endOdometer, String endTime, String engineHours, String latitude, String longitude,
			String maxSpeed, String positionId, String spentFuel, String startOdometer, String startTime) {
		super();
		this.address = address;
		this.averageSpeed = averageSpeed;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.distance = distance;
		this.duration = duration;
		this.endOdometer = endOdometer;
		this.endTime = endTime;
		this.engineHours = engineHours;
		this.latitude = latitude;
		this.longitude = longitude;
		this.maxSpeed = maxSpeed;
		this.positionId = positionId;
		this.spentFuel = spentFuel;
		this.startOdometer = startOdometer;
		this.startTime = startTime;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getEndOdometer() {
		return endOdometer;
	}
	public void setEndOdometer(String endOdometer) {
		this.endOdometer = endOdometer;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getEngineHours() {
		return engineHours;
	}
	public void setEngineHours(String engineHours) {
		this.engineHours = engineHours;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(String maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public String getPositionId() {
		return positionId;
	}
	public void setPositionId(String positionId) {
		this.positionId = positionId;
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
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverUniqueId() {
		return driverUniqueId;
	}
	public void setDriverUniqueId(String driverUniqueId) {
		this.driverUniqueId = driverUniqueId;
	}
	
	


	
	

}
