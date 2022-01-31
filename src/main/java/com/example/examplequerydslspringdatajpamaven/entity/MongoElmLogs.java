package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Map;

import javax.persistence.Id;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection to save any action in service elm
 * @author fuinco
 *
 */
@Document(collection = "tc_elmLogs")
public class MongoElmLogs {

	
	@Id
	private ObjectId _id;
	
	private Long userId;
	
	private String userName;
	
	private Long driverId;
	
	private String driverName;
	
	private Long deviceId;
	
	private String deviceName;

	private String time;

	private String type;

	private Map requet;
	
	private Map response;

	
	public MongoElmLogs(){
		
	}
	
	public MongoElmLogs(ObjectId _id, Long userId, String userName, Long driverId, String driverName, Long deviceId,
			String deviceName, String time, String type, Map requet, Map response) {
		
		super();
		this._id = _id;
		this.userId = userId;
		this.userName = userName;
		this.driverId = driverId;
		this.driverName = driverName;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.time = time;
		this.type = type;
		this.requet = requet;
		this.response = response;
	}


	public ObjectId get_id() {
		return _id;
	}


	public void set_id(ObjectId _id) {
		this._id = _id;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public String getUserName() {
		return userName;
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

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Map getRequet() {
		return requet;
	}


	public void setRequet(Map requet) {
		this.requet = requet;
	}


	public Map getResponse() {
		return response;
	}


	public void setResponse(Map response) {
		this.response = response;
	}
	
	
	
	
}
