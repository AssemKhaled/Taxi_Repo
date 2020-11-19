package com.example.examplequerydslspringdatajpamaven.entity;

/**
 * Bind data of trip report in this model
 * @author fuinco
 *
 */
public class TripReport {
	
	private String averageSpeed;
	private Long deviceId;
	private String deviceName;
	private String distance;
	private String driverName;
	private String driverUniqueId;
	private String duration;
	private String endAddress;
	private String endLat;
	private String endLon;
	private String endOdometer;
	private String endPositionId;
	private String endTime;
	private String maxSpeed;
	private String spentFuel;
	private String startAddress;
	private String startLat;
	private String startLon;
	private String startOdometer;
	private String startPositionId;
	private String startTime;
	
	
	
	public TripReport() {
		
	}
	public TripReport(String averageSpeed, Long deviceId, String deviceName, String distance, String driverName,
			String driverUniqueId, String duration, String endAddress, String endLat, String endLon, String endOdometer,
			String endPositionId, String endTime, String maxSpeed, String spentFuel, String startAddress, String startLat,
			String startLon, String startOdometer, String startPositionId, String startTime) {
		super();
		this.averageSpeed = averageSpeed;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.distance = distance;
		this.driverName = driverName;
		this.driverUniqueId = driverUniqueId;
		this.duration = duration;
		this.endAddress = endAddress;
		this.endLat = endLat;
		this.endLon = endLon;
		this.endOdometer = endOdometer;
		this.endPositionId = endPositionId;
		this.endTime = endTime;
		this.maxSpeed = maxSpeed;
		this.spentFuel = spentFuel;
		this.startAddress = startAddress;
		this.startLat = startLat;
		this.startLon = startLon;
		this.startOdometer = startOdometer;
		this.startPositionId = startPositionId;
		this.startTime = startTime;
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
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getEndAddress() {
		return endAddress;
	}
	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}
	public String getEndLat() {
		return endLat;
	}
	public void setEndLat(String endLat) {
		this.endLat = endLat;
	}
	public String getEndLon() {
		return endLon;
	}
	public void setEndLon(String endLon) {
		this.endLon = endLon;
	}
	public String getEndOdometer() {
		return endOdometer;
	}
	public void setEndOdometer(String endOdometer) {
		this.endOdometer = endOdometer;
	}
	public String getEndPositionId() {
		return endPositionId;
	}
	public void setEndPositionId(String endPositionId) {
		this.endPositionId = endPositionId;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
	public String getStartAddress() {
		return startAddress;
	}
	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}
	public String getStartLat() {
		return startLat;
	}
	public void setStartLat(String startLat) {
		this.startLat = startLat;
	}
	public String getStartLon() {
		return startLon;
	}
	public void setStartLon(String startLon) {
		this.startLon = startLon;
	}
	public String getStartOdometer() {
		return startOdometer;
	}
	public void setStartOdometer(String startOdometer) {
		this.startOdometer = startOdometer;
	}
	public String getStartPositionId() {
		return startPositionId;
	}
	public void setStartPositionId(String startPositionId) {
		this.startPositionId = startPositionId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	
	
	

}
