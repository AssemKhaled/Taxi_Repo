package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;

public class CustomDeviceList{
	
	private int id;
	private String deviceName;
	private String uniqueId;
	private String sequenceNumber;
	private Date lastUpdate;
	private String referenceKey;
	private String driverName;
	private String geofenceName;
	
//	public CustomDeviceList(Long id , String deviceName , String uniqueId , String sequenceNumber ,
//			                String lastUpdate, String referenceKey, String driverName ,String geofenceName ) {
//		this.id = id;
//		this.uniqueId = uniqueId;
//		this.deviceName = deviceName;
//		this.geofenceName = geofenceName;
//		this.sequenceNumber = sequenceNumber;
//		this.referenceKey = referenceKey;
//		this.lastUpdate = lastUpdate;
//		
//	}
	public CustomDeviceList(int id ,String deviceName,String uniqueId , String sequenceNumber, String referenceKey , String driverName ,String geofenceName ,Date lastUpdate ) {
		this.id = id;
		this.deviceName = deviceName;
		this.uniqueId = uniqueId;
		this.sequenceNumber = sequenceNumber;
		this.geofenceName = geofenceName;
		this.referenceKey = referenceKey;
		this.driverName = driverName;
	    this.lastUpdate = lastUpdate;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getReferenceKey() {
		return referenceKey;
	}

	public void setReferenceKey(String referenceKey) {
		this.referenceKey = referenceKey;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getGeofenceName() {
		return geofenceName;
	}

	public void setGeofenceName(String geofenceName) {
		this.geofenceName = geofenceName;
	}
	

}
