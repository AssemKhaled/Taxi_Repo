package com.example.examplequerydslspringdatajpamaven.entity;


import javax.persistence.Id;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Data Of Elm that sending from position live
 * @author fuinco
 *
 */
@Document(collection = "tc_elm_live_location")
public class MongoElmLiveLocation {

	@Id
	private ObjectId _id;
	
	private String referenceKey;
	
	private String driverReferenceKey;

	private Double latitude;
	
	private Double longitude;
	
	private Double velocity;
	
	private Double weight;
	
	private String locationTime;
	
	private String vehicleStatus;
	
	private String address;
	
	private String roleCode;

	
	
	public MongoElmLiveLocation() {
		
	}
	
	public MongoElmLiveLocation(ObjectId _id, String referenceKey, String driverReferenceKey, Double latitude,
			Double longitude, Double velocity, Double weight, String locationTime, String vehicleStatus, String address,
			String roleCode) {
		super();
		this._id = _id;
		this.referenceKey = referenceKey;
		this.driverReferenceKey = driverReferenceKey;
		this.latitude = latitude;
		this.longitude = longitude;
		this.velocity = velocity;
		this.weight = weight;
		this.locationTime = locationTime;
		this.vehicleStatus = vehicleStatus;
		this.address = address;
		this.roleCode = roleCode;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getReferenceKey() {
		return referenceKey;
	}

	public void setReferenceKey(String referenceKey) {
		this.referenceKey = referenceKey;
	}

	public String getDriverReferenceKey() {
		return driverReferenceKey;
	}

	public void setDriverReferenceKey(String driverReferenceKey) {
		this.driverReferenceKey = driverReferenceKey;
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

	public Double getVelocity() {
		return velocity;
	}

	public void setVelocity(Double velocity) {
		this.velocity = velocity;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getLocationTime() {
		return locationTime;
	}

	public void setLocationTime(String locationTime) {
		this.locationTime = locationTime;
	}

	public String getVehicleStatus() {
		return vehicleStatus;
	}

	public void setVehicleStatus(String vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	
}
