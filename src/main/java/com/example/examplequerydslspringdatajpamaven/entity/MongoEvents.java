package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;

import javax.persistence.Id;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Events collection in Mongo DB
 * @author fuinco
 *
 */
@Document(collection = "tc_events")
public class MongoEvents {

	@Id
	private ObjectId _id;
	
	private String type;
	
	private Date servertime;
	
	private Long deviceid;

	private String positionid;
	
	private Long geofenceid;
	
	private Object attributes;
	
	private Long maintinanceid;
	
	private String deviceName;
	
	private Long driverid;
	
	private String driverName;
	
	public MongoEvents() {
		
	}

	public MongoEvents(ObjectId _id, String type, Date servertime, Long deviceid, String positionid, Long geofenceid,
			Object attributes, Long maintinanceid, String deviceName, Long driverid, String driverName) {
		super();
		this._id = _id;
		this.type = type;
		this.servertime = servertime;
		this.deviceid = deviceid;
		this.positionid = positionid;
		this.geofenceid = geofenceid;
		this.attributes = attributes;
		this.maintinanceid = maintinanceid;
		this.deviceName = deviceName;
		this.driverid = driverid;
		this.driverName = driverName;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getServertime() {
		return servertime;
	}

	public void setServertime(Date servertime) {
		this.servertime = servertime;
	}

	public Long getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(Long deviceid) {
		this.deviceid = deviceid;
	}

	public String getPositionid() {
		return positionid;
	}

	public void setPositionid(String positionid) {
		this.positionid = positionid;
	}

	public Long getGeofenceid() {
		return geofenceid;
	}

	public void setGeofenceid(Long geofenceid) {
		this.geofenceid = geofenceid;
	}

	public Object getAttributes() {
		return attributes;
	}

	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}

	public Long getMaintinanceid() {
		return maintinanceid;
	}

	public void setMaintinanceid(Long maintinanceid) {
		this.maintinanceid = maintinanceid;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Long getDriverid() {
		return driverid;
	}

	public void setDriverid(Long driverid) {
		this.driverid = driverid;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	
	
	

}
