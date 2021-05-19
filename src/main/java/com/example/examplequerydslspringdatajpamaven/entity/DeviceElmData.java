package com.example.examplequerydslspringdatajpamaven.entity;


public class DeviceElmData {

	String sequenceNumber;
	Integer plateType;
	String imeiNumber;
	VehiclePlate vehiclePlate;
	String activity;

	public DeviceElmData() {
		super();
	}
	public DeviceElmData(String sequenceNumber, Integer plateType, String imeiNumber, VehiclePlate vehiclePlate) {
		super();
		this.sequenceNumber = sequenceNumber;
		this.plateType = plateType;
		this.imeiNumber = imeiNumber;
		this.vehiclePlate = vehiclePlate;
	}
	public DeviceElmData(String sequenceNumber, Integer plateType, String imeiNumber, VehiclePlate vehiclePlate,
			String activity) {
		super();
		this.sequenceNumber = sequenceNumber;
		this.plateType = plateType;
		this.imeiNumber = imeiNumber;
		this.vehiclePlate = vehiclePlate;
		this.activity = activity;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public Integer getPlateType() {
		return plateType;
	}
	public void setPlateType(Integer plateType) {
		this.plateType = plateType;
	}
	public String getImeiNumber() {
		return imeiNumber;
	}
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}
	public VehiclePlate getVehiclePlate() {
		return vehiclePlate;
	}
	public void setVehiclePlate(VehiclePlate vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}
	
	

}
