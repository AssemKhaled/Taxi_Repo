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
	private Long driverId;
	private String driverPhoto;
	private String driverUniqueId;
	private String plateType;
	private String plateNum;
	private String rightLetter;
	private String middleLetter;
	private String leftLetter;
	private String ownerName;
	private String ownerId;
	private String userName;
	private String brand;
	private String model;
	private String madeYear;
	private String color;
	private String licenceExptDate;
	private String carWeight;
	private String vehiclePlate;
	private String companyName;

	
	public CustomDeviceList(int id ,String deviceName,String uniqueId , String sequenceNumber, String referenceKey , String companyName, String driverName ,String geofenceName ,Date lastUpdate ) {
		this.id = id;
		this.deviceName = deviceName;
		this.uniqueId = uniqueId;
		this.sequenceNumber = sequenceNumber;
		this.geofenceName = geofenceName;
		this.referenceKey = referenceKey;
		this.driverName = driverName;
	    this.lastUpdate = lastUpdate;
	    this.companyName = companyName;

		
	}

	public CustomDeviceList(int id,String uniqueId, String sequenceNumber, String driverName, Long driverId,
			String driverPhoto,String driverUniqueId ,String plateType, String vehiclePlate, String ownerName, String ownerId, String userName, String brand, String model,
			String madeYear, String color, String licenceExptDate, String carWeight) {
		super();
		this.id=id;
		this.uniqueId = uniqueId;
		this.sequenceNumber = sequenceNumber;
		this.driverName = driverName;
		this.driverId = driverId;
		this.driverPhoto = driverPhoto;
		this.driverUniqueId = driverUniqueId;
		this.plateType = plateType;
		this.vehiclePlate=vehiclePlate;
		this.ownerName = ownerName;
		this.ownerId = ownerId;
		this.userName = userName;
		this.brand = brand;
		this.model = model;
		this.madeYear = madeYear;
		this.color = color;
		this.licenceExptDate = licenceExptDate;
		this.carWeight = carWeight;
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

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public String getDriverPhoto() {
		return driverPhoto;
	}

	public void setDriverPhoto(String driverPhoto) {
		this.driverPhoto = driverPhoto;
	}

	public String getPlateType() {
		return plateType;
	}

	public void setPlateType(String plateType) {
		this.plateType = plateType;
	}

	public String getPlateNum() {
		return plateNum;
	}

	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}

	public String getRightLetter() {
		return rightLetter;
	}

	public void setRightLetter(String rightLetter) {
		this.rightLetter = rightLetter;
	}

	public String getMiddleLetter() {
		return middleLetter;
	}

	public void setMiddleLetter(String middleLetter) {
		this.middleLetter = middleLetter;
	}

	public String getLeftLetter() {
		return leftLetter;
	}

	public void setLeftLetter(String leftLetter) {
		this.leftLetter = leftLetter;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerID(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getMadeYear() {
		return madeYear;
	}

	public void setMadeYear(String madeYear) {
		this.madeYear = madeYear;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getLicenceExptDate() {
		return licenceExptDate;
	}

	public void setLicenceExptDate(String licenceExptDate) {
		this.licenceExptDate = licenceExptDate;
	}

	public String getCarWeight() {
		return carWeight;
	}

	public void setCarWeight(String carWeight) {
		this.carWeight = carWeight;
	}

	public String getVehiclePlate() {
		return vehiclePlate;
	}

	public void setVehiclePlate(String vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getDriverUniqueId() {
		return driverUniqueId;
	}
	public void setDriverUniqueId(String driverUniqueId) {
		this.driverUniqueId = driverUniqueId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	

}
