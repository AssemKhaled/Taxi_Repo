package com.example.examplequerydslspringdatajpamaven.entity;

public class DeviceElmDataUpdate {

	String sequenceNumber;
	String imeiNumber;
	
	
	
	
	public DeviceElmDataUpdate() {
		super();
	}

	public DeviceElmDataUpdate(String sequenceNumber, String imeiNumber) {
		super();
		this.sequenceNumber = sequenceNumber;
		this.imeiNumber = imeiNumber;
	}
	
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public String getImeiNumber() {
		return imeiNumber;
	}
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}
	
	
}
