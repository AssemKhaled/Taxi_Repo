package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;

import javax.persistence.Id;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tc_positions_elm")
public class MongoPositionsElm {

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
	
	private Float altitude;
	
	private Float speed;
	
	private Float course;
	
	private String address;
	
	private Object attributes;
	
	private Double accuracy;
	
	private String network;
	
	private Integer is_sent;
	
	private Integer is_offline;
	
	private Float weight;

	
	
	public MongoPositionsElm() {
		
	}



	public MongoPositionsElm(ObjectId _id, String protocol, Long deviceid, Date servertime, Date devicetime, Date fixtime,
			Boolean valid, Double latitude, Double longitude, Float altitude, Float speed, Float course, String address,
			Object attributes, Double accuracy, String network, Integer is_sent, Integer is_offline, Float weight) {
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
		this.is_sent = is_sent;
		this.is_offline = is_offline;
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



	public Float getAltitude() {
		return altitude;
	}



	public void setAltitude(Float altitude) {
		this.altitude = altitude;
	}



	public Float getSpeed() {
		return speed;
	}



	public void setSpeed(Float speed) {
		this.speed = speed;
	}



	public Float getCourse() {
		return course;
	}



	public void setCourse(Float course) {
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



	public Integer getIs_sent() {
		return is_sent;
	}



	public void setIs_sent(Integer is_sent) {
		this.is_sent = is_sent;
	}



	public Integer getIs_offline() {
		return is_offline;
	}



	public void setIs_offline(Integer is_offline) {
		this.is_offline = is_offline;
	}



	public Float getWeight() {
		return weight;
	}



	public void setWeight(Float weight) {
		this.weight = weight;
	}
	
	
	
	
	
}
