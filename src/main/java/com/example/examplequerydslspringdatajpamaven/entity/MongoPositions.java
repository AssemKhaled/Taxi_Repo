package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;
import javax.persistence.Id;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Positions Collection in Mongo DB
 * @author fuinco
 *
 */
@Document(collection = "tc_positions")
public class MongoPositions {
	
	@Id
	private ObjectId _id;
	
	private String protocol;
	
	private Long deviceid;

	private Date servertime;
	
	private Date devicetime;
	
	private Date fixtime;
	
	private Boolean valid;
	
	private Double latitude;
	
	private Double longitude;
	
	private Double altitude;
	
	private Double speed;
	
	private Double course;
	
	private String address;
	
	private Object attributes;
	
	private Double accuracy;
	
	private String network;
	
	private String deviceName;

	private String deviceReferenceKey;

	private String driverReferenceKey;

	private String driverName;

	private Long driverid;

	private Double weight;
	
	
	public MongoPositions() {
		
	}


	public MongoPositions(ObjectId _id, String protocol, Long deviceid, Date servertime, Date devicetime, Date fixtime,
			Boolean valid, Double latitude, Double longitude, Double altitude, Double speed, Double course,
			String address, Object attributes, Double accuracy, String network, String deviceName,
			String deviceReferenceKey, String driverReferenceKey, String driverName, Long driverid, Double weight) {
		super();
		this._id = _id;
		this.protocol = protocol;
		this.deviceid = deviceid;
		this.servertime = servertime;
		this.devicetime = devicetime;
		this.fixtime = fixtime;
		this.valid = valid;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.speed = speed;
		this.course = course;
		this.address = address;
		this.attributes = attributes;
		this.accuracy = accuracy;
		this.network = network;
		this.deviceName = deviceName;
		this.deviceReferenceKey = deviceReferenceKey;
		this.driverReferenceKey = driverReferenceKey;
		this.driverName = driverName;
		this.driverid = driverid;
		this.weight = weight;
	}


	public ObjectId get_id() {
		return _id;
	}


	public void set_id(ObjectId _id) {
		this._id = _id;
	}


	public String getProtocol() {
		return protocol;
	}


	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}


	public Long getDeviceid() {
		return deviceid;
	}


	public void setDeviceid(Long deviceid) {
		this.deviceid = deviceid;
	}


	public Date getServertime() {
		return servertime;
	}


	public void setServertime(Date servertime) {
		this.servertime = servertime;
	}


	public Date getDevicetime() {
		return devicetime;
	}


	public void setDevicetime(Date devicetime) {
		this.devicetime = devicetime;
	}


	public Date getFixtime() {
		return fixtime;
	}


	public void setFixtime(Date fixtime) {
		this.fixtime = fixtime;
	}


	public Boolean getValid() {
		return valid;
	}


	public void setValid(Boolean valid) {
		this.valid = valid;
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


	public Double getAltitude() {
		return altitude;
	}


	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}


	public Double getSpeed() {
		return speed;
	}


	public void setSpeed(Double speed) {
		this.speed = speed;
	}


	public Double getCourse() {
		return course;
	}


	public void setCourse(Double course) {
		this.course = course;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public Object getAttributes() {
		return attributes;
	}


	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}


	public Double getAccuracy() {
		return accuracy;
	}


	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}


	public String getNetwork() {
		return network;
	}


	public void setNetwork(String network) {
		this.network = network;
	}


	public String getDeviceName() {
		return deviceName;
	}


	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}


	public String getDeviceReferenceKey() {
		return deviceReferenceKey;
	}


	public void setDeviceReferenceKey(String deviceReferenceKey) {
		this.deviceReferenceKey = deviceReferenceKey;
	}


	public String getDriverReferenceKey() {
		return driverReferenceKey;
	}


	public void setDriverReferenceKey(String driverReferenceKey) {
		this.driverReferenceKey = driverReferenceKey;
	}


	public String getDriverName() {
		return driverName;
	}


	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}


	public Long getDriverid() {
		return driverid;
	}


	public void setDriverid(Long driverid) {
		this.driverid = driverid;
	}


	public Double getWeight() {
		return weight;
	}


	public void setWeight(Double weight) {
		this.weight = weight;
	}

	
	


	
	
	
}
