package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;
import java.util.Objects;

import javax.jdo.annotations.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

@Document(collection = "tc_positions")
public class MongoPositions {

	@Id
	private String _id;
	
	private String protocol;
	
	private Date servertime;
	
	private Date devicetime;
	
	private Date fixtime;
	
	private Integer valid;
	
	private Double latitude;
	
	private Double longitude;
	
	private float altitude;
	
	private float speed;
	
	private float course;
	
	private String address;
	
	private String attributes;
	
	private Double accuracy;
	
	private String network;
	
	private Integer is_sent;
	
	private Integer is_offline;
	
	private float weight;

	
	
	
	
	public MongoPositions(String _id, String protocol, Date servertime, Date devicetime, Date fixtime, Integer valid,
			Double latitude, Double longitude, float altitude, float speed, float course, String address,
			String attributes, Double accuracy, String network, Integer is_sent, Integer is_offline, Float weight) {
		super();
		this._id = _id;
		this.protocol = protocol;
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

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
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

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
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

	public float getAltitude() {
		return altitude;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getCourse() {
		return course;
	}

	public void setCourse(float course) {
		this.course = course;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
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

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	
}
