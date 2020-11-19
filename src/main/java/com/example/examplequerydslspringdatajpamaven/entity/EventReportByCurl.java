package com.example.examplequerydslspringdatajpamaven.entity;


/**
 * Bind data of events or notifications from mongo by type on this model
 * @author fuinco
 *
 */
public class EventReportByCurl {
	
	private Long id;
	private Object attributes;
	private Long deviceId;
	private String type;
	private String serverTime;
	private Long positionId;
	private Long geofenceId;
	private Long maintenanceId;
	private String deviceName;
	private String driverName;
	
	
	
	
	public EventReportByCurl() {
			
	}
	
	

	public EventReportByCurl(Long id, Object attributes, Long deviceId, String type, String serverTime,
			Long positionId, Long geofenceId, Long maintenanceId, String deviceName, String driverName) {
		super();
		this.id = id;
		this.attributes = attributes;
		this.deviceId = deviceId;
		this.type = type;
		this.serverTime = serverTime;
		this.positionId = positionId;
		this.geofenceId = geofenceId;
		this.maintenanceId = maintenanceId;
		this.deviceName = deviceName;
		this.driverName = driverName;
	}



	public Long getId() {
		return id;
	}




	public void setId(Long id) {
		this.id = id;
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




	public String getType() {
		return type;
	}




	public void setType(String type) {
		this.type = type;
	}




	public String getServerTime() {
		return serverTime;
	}




	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}




	public Long getPositionId() {
		return positionId;
	}




	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}




	public Long getGeofenceId() {
		return geofenceId;
	}




	public void setGeofenceId(Long geofenceId) {
		this.geofenceId = geofenceId;
	}




	public Long getMaintenanceId() {
		return maintenanceId;
	}




	public void setMaintenanceId(Long maintenanceId) {
		this.maintenanceId = maintenanceId;
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
	
	
	
	
	

}
