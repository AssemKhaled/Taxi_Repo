package com.example.examplequerydslspringdatajpamaven.entity;

/**
 * Bind data of events or notifications from mongo on this model
 * @author fuinco
 *
 */
public class EventReport {

	private String eventId;
	private String eventType;
	private String serverTime;
	private Object attributes;
	private Long deviceId;
	private String deviceName;
	private Long driverId;
	private String driverName;
	private Long geofenceId;
	private String geofenceName;
	private String positionId;
	private Double latitude;
	private Double longitude;
	private Long userId;
	
	public EventReport() {
		
	}

	public EventReport(String eventId, String eventType, String serverTime, Object attributes, Long deviceId,
			String deviceName, Long driverId, String driverName, Long geofenceId, String geofenceName, String positionId,
			Double latitude, Double longitude) {
		super();
		this.eventId = eventId;
		this.eventType = eventType;
		this.serverTime = serverTime;
		this.attributes = attributes;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.driverId = driverId;
		this.driverName = driverName;
		this.geofenceId = geofenceId;
		this.geofenceName = geofenceName;
		this.positionId = positionId;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public EventReport(String eventId, String eventType, String serverTime, Object attributes, Long deviceId,String deviceName,
			 Long driverId, String driverName, Long geofenceId, String geofenceName, String positionId	) {
		this.eventId = eventId;
		this.eventType = eventType;
		this.serverTime = serverTime;
		this.attributes = attributes;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.driverId = driverId;
		this.driverName = driverName;
		this.geofenceId = geofenceId;
		this.geofenceName = geofenceName;
		this.positionId = positionId;
	}
	public EventReport(String eventId, String eventType, String serverTime, Object attributes, Long deviceId,
			String deviceName, Long driverId, String driverName) {
		super();
		this.eventId = eventId;
		this.eventType = eventType;
		this.serverTime = serverTime;
		this.attributes = attributes;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.driverId = driverId;
		this.driverName = driverName;
	}
	public EventReport(String eventId,String eventType, String serverTime, Object attributes,Long deviceId) {
		super();
		this.eventId = eventId;
		this.eventType = eventType;
		this.serverTime = serverTime;
		this.attributes = attributes;
		this.deviceId = deviceId;

	}
	public EventReport(String eventId,String eventType, String serverTime, Object attributes) {
		super();
		this.eventId = eventId;
		this.eventType = eventType;
		this.serverTime = serverTime;
		this.attributes = attributes;

	}
	
	public EventReport(String eventId, String eventType, String serverTime, Object attributes, Long deviceId,
			String deviceName, Long driverId, String driverName, Long geofenceId, String geofenceName,
			String positionId, Double latitude, Double longitude, Long userId) {
		super();
		this.eventId = eventId;
		this.eventType = eventType;
		this.serverTime = serverTime;
		this.attributes = attributes;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.driverId = driverId;
		this.driverName = driverName;
		this.geofenceId = geofenceId;
		this.geofenceName = geofenceName;
		this.positionId = positionId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.userId = userId;
	}

	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getServerTime() {
		return serverTime;
	}
	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
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
	public Long getDriverId() {
		return driverId;
	}
	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public Long getGeofenceId() {
		return geofenceId;
	}
	public void setGeofenceId(Long geofenceId) {
		this.geofenceId = geofenceId;
	}
	public String getGeofenceName() {
		return geofenceName;
	}
	public void setGeofenceName(String geofenceName) {
		this.geofenceName = geofenceName;
	}
	public String getPositionId() {
		return positionId;
	}
	public void setPositionId(String positionId) {
		this.positionId = positionId;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	

	
	

	

	
	
	 
	 
}
