package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Id;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection to save elm data after sending
 * @author fuinco
 *
 */
@Document(collection = "tc_elm_last_locations_tbl")
public class MongoElmLastLocations {

	@Id
	private ObjectId _id;

	private String positionid;

	private Object elm_data;
	
	private String sendtime;
	
	private String responsetime;
	
	private Integer responsetype;

	private Long vehicleid;

	private String vehiclename;

	private String vehicleReferenceKey;
	
	private Long driverid;

	private String drivername;

	private String driverReferenceKey;
	
	private Long user_id;

	private String username;

	private String userReferenceKey;

	private String reason;

	public MongoElmLastLocations() {
		
	}
	
	public MongoElmLastLocations(ObjectId _id, String positionid, Object elm_data, String sendtime, String responsetime,
			Integer responsetype, Long vehicleid, String vehiclename, String vehicleReferenceKey, Long driverid,
			String drivername, String driverReferenceKey, Long user_id, String username, String userReferenceKey,
			String reason) {
		super();
		this._id = _id;
		this.positionid = positionid;
		this.elm_data = elm_data;
		this.sendtime = sendtime;
		this.responsetime = responsetime;
		this.responsetype = responsetype;
		this.vehicleid = vehicleid;
		this.vehiclename = vehiclename;
		this.vehicleReferenceKey = vehicleReferenceKey;
		this.driverid = driverid;
		this.drivername = drivername;
		this.driverReferenceKey = driverReferenceKey;
		this.user_id = user_id;
		this.username = username;
		this.userReferenceKey = userReferenceKey;
		this.reason = reason;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getPositionid() {
		return positionid;
	}

	public void setPositionid(String positionid) {
		this.positionid = positionid;
	}

	public Object getElm_data() {
		return elm_data;
	}

	public void setElm_data(Object elm_data) {
		this.elm_data = elm_data;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getResponsetime() {
		return responsetime;
	}

	public void setResponsetime(String responsetime) {
		this.responsetime = responsetime;
	}

	public Integer getResponsetype() {
		return responsetype;
	}

	public void setResponsetype(Integer responsetype) {
		this.responsetype = responsetype;
	}

	public Long getVehicleid() {
		return vehicleid;
	}

	public void setVehicleid(Long vehicleid) {
		this.vehicleid = vehicleid;
	}

	public String getVehiclename() {
		return vehiclename;
	}

	public void setVehiclename(String vehiclename) {
		this.vehiclename = vehiclename;
	}

	public String getVehicleReferenceKey() {
		return vehicleReferenceKey;
	}

	public void setVehicleReferenceKey(String vehicleReferenceKey) {
		this.vehicleReferenceKey = vehicleReferenceKey;
	}

	public Long getDriverid() {
		return driverid;
	}

	public void setDriverid(Long driverid) {
		this.driverid = driverid;
	}

	public String getDrivername() {
		return drivername;
	}

	public void setDrivername(String drivername) {
		this.drivername = drivername;
	}

	public String getDriverReferenceKey() {
		return driverReferenceKey;
	}

	public void setDriverReferenceKey(String driverReferenceKey) {
		this.driverReferenceKey = driverReferenceKey;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserReferenceKey() {
		return userReferenceKey;
	}

	public void setUserReferenceKey(String userReferenceKey) {
		this.userReferenceKey = userReferenceKey;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	

}
