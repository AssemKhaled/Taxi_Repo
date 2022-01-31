package com.example.examplequerydslspringdatajpamaven.entity;

/**
 * Get expired vehicles to set in DB and prevent from sending to elm
 * @author fuinco
 *
 */
public class ExpiredVehicles {

	private Long deviceId;
	private Long userId;
	private String vehicle_referenceKey;
	private String user_referenceKey;
	private String deviceName;
	private String userName;

	
	public ExpiredVehicles() {
		super();
	}
	
	public ExpiredVehicles(Long deviceId, Long userId, String vehicle_referenceKey, String user_referenceKey,
			String deviceName, String userName) {
		super();
		this.deviceId = deviceId;
		this.userId = userId;
		this.vehicle_referenceKey = vehicle_referenceKey;
		this.user_referenceKey = user_referenceKey;
		this.deviceName = deviceName;
		this.userName = userName;
	}

	public ExpiredVehicles(Long deviceId, Long userId, String vehicle_referenceKey, String user_referenceKey) {
		super();
		this.deviceId = deviceId;
		this.userId = userId;
		this.vehicle_referenceKey = vehicle_referenceKey;
		this.user_referenceKey = user_referenceKey;
	}
	
	public Long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getVehicle_referenceKey() {
		return vehicle_referenceKey;
	}
	public void setVehicle_referenceKey(String vehicle_referenceKey) {
		this.vehicle_referenceKey = vehicle_referenceKey;
	}
	public String getUser_referenceKey() {
		return user_referenceKey;
	}
	public void setUser_referenceKey(String user_referenceKey) {
		this.user_referenceKey = user_referenceKey;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	

}
