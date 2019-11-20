package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.ColumnResult;
import javax.persistence.TemporalType;
import javax.swing.Spring;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.SqlResultSetMapping;
import javax.persistence.ConstructorResult;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "tc_positions")
public class NewPosition{
	public NewPosition() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NewPosition(String id, String protocol) {
		super();
		this.id = id;
		this.protocol = protocol;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Id
	private String id;
	
	private String protocol;
	
}
//@SqlResultSetMappings({
//	@SqlResultSetMapping(
//        name="positionlist",
//        classes={
//           @ConstructorResult(
//                targetClass=NewPosition.class,
//                  columns={
//                 		 @ColumnResult(name="address",type=String.class),
//                 		 @ColumnResult(name="deviceid",type=Integer.class),
// 	                     @ColumnResult(name="attributes",type=String.class),
// 	                     @ColumnResult(name="latitude",type=Double.class),
// 	                     @ColumnResult(name="longitude",type=Double.class),
// 	                     @ColumnResult(name="speed",type=Float.class),
//                }    
//                )
//        }
//      )
//	})  

//@NamedNativeQuery(name="getPositionsLiveDataMap", 
//resultSetMapping="positionlist", 
//query="SELECT  tc_positions.address,tc_positions.deviceid "
//		+ ", tc_positions.attributes ,tc_positions.latitude , "
//		+ " tc_positions.longitude ,"
//		+ " tc_positions.speed from tc_positions WHERE tc_positions.deviceid IN (:deviceId) ")

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "tc_positions" , schema = "sareb")
//public class NewPosition {
//	public NewPosition(String address,Integer deviceid,String attributes
//			, Double latitude, Double longitude, float speed) {	
//		this.deviceid = deviceid;		
//		this.latitude = latitude;
//		this.longitude = longitude;
//		this.speed = speed;
//		this.address = address;
//		this.attributes = attributes;
//		
//	}
//	@Id
//	@GeneratedValue
	//@Column(name="id")

//	@Id
//	@Field(value = "id")
//	private String id;	
//	public NewPosition(String protocol, Double latitude) {
//		this.protocol = protocol;
//		this.latitude = latitude;
//	}	
//public NewPosition(String id, String protocol, Double latitude) {
//	this.id = id;
//	this.protocol = protocol;
//	this.latitude = latitude;
//}
////	public NewPosition(String id, Integer deviceid, String protocol, Date servertime, Date devicetime, Date fixtime,
////			boolean valid, Double latitude, Double longitude, float altitude, float speed, float course, String address,
////		String attributes, Double accuracy, String network, Integer is_sent, Integer is_offline, Float weight) {
////	this.id = id;
////	this.deviceid = deviceid;
////	this.protocol = protocol;
////	this.servertime = servertime;
////	this.devicetime = devicetime;
////	this.fixtime = fixtime;
////	this.valid = valid;
////	this.latitude = latitude;
////	this.longitude = longitude;
////	this.altitude = altitude;
////	this.speed = speed;
////	this.course = course;
////	this.address = address;
////	this.attributes = attributes;
////	this.accuracy = accuracy;
////	this.network = network;
////	this.is_sent = is_sent;
////	this.is_offline = is_offline;
////	this.weight = weight;
////}
//@Field(value = "protocol")
//	private String protocol;	
//
//	//@Column(name ="deviceid")
////	private Integer deviceid;
//	//@Column(name = "protocol")
////	@CsvBindByName
////	@CsvDate(value = "E MMM d HH:mm:ss zzz yyyy")
////	@Temporal(TemporalType.TIMESTAMP)
////	@Column(name = "servertime")
////	private Date servertime;	
////	@CsvBindByName
////	@CsvDate(value = "E MMM d HH:mm:ss zzz yyyy")
////	@Temporal(TemporalType.TIMESTAMP)
////	@Column(name = "devicetime")
////	private Date devicetime;
////	@CsvBindByName
////	@CsvDate(value = "E MMM d HH:mm:ss zzz yyyy")
////	@Temporal(TemporalType.TIMESTAMP)
////	@Column(name = "fixtime")
//	//private Date fixtime;
//	
//
//	//@Column(name = "valid")
////	private boolean valid;
//	
//	//@Column(name = "latitude")
//@Field(value = "latitude")
//	private Double latitude;
//	
//	//@Column(name = "longitude")
////	private Double longitude;
//	
//	//@Column(name = "altitude")
////	private float altitude;
//	
////	//@Column(name = "speed")
////	private float speed;
////	
////	//@Column(name = "course")
////	private float course;
////	
////	//@Column(name = "address")
////	private String address;
////	
////	//@Column(name = "attributes")
////	private String attributes;
////	
////	//@Column(name = "accuracy")
////	private Double accuracy;
////	
////	//@Column(name = "network")
////	private String network;
////	
////	//@Column(name = "is_sent")
////	private Integer is_sent;
////	
////	//@Column(name = "is_offline")
////	private Integer is_offline;
////	
////	//@Column(name = "weight")
////	private Float weight;
////	
////	
////	
//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
////
//	public String getProtocol() {
//		return protocol;
//	}
////
//	public void setProtocol(String protocol) {
//		this.protocol = protocol;
//	}
////
////	public Date getServertime() {
////		return servertime;
////	}
////
////	public void setServertime(Date servertime) {
////		this.servertime = servertime;
////	}
////
////	public Date getDevicetime() {
////		return devicetime;
////	}
////
////	public void setDevicetime(Date devicetime) {
////		this.devicetime = devicetime;
////	}
////
////	public Date getFixtime() {
////		return fixtime;
////	}
////
////	public void setFixtime(Date fixtime) {
////		this.fixtime = fixtime;
////	}
////
////	public boolean getValid() {
////		return valid;
////	}
////
////	public void setValid(boolean valid) {
////		this.valid = valid;
////	}
////
//	public Double getLatitude() {
//		return latitude;
//	}
////
//	public void setLatitude(Double latitude) {
//		this.latitude = latitude;
//	}
////
////	public Double getLongitude() {
////		return longitude;
////	}
////
////	public void setLongitude(Double longitude) {
////		this.longitude = longitude;
////	}
////
////	public float getAltitude() {
////		return altitude;
////	}
////
////	public void setAltitude(float altitude) {
////		this.altitude = altitude;
////	}
////
////	public float getSpeed() {
////		return speed;
////	}
////
////	public void setSpeed(float speed) {
////		this.speed = speed;
////	}
////
////	public float getCourse() {
////		return course;
////	}
////
////	public void setCourse(float course) {
////		this.course = course;
////	}
////
////	public String getAddress() {
////		return address;
////	}
////
////	public void setAddress(String address) {
////		this.address = address;
////	}
////
////	public String getAttributes() {
////		return attributes;
////	}
////
////	public void setAttributes(String attributes) {
////		this.attributes = attributes;
////	}
////
////	public Double getAccuracy() {
////		return accuracy;
////	}
////
////	public void setAccuracy(Double accuracy) {
////		this.accuracy = accuracy;
////	}
////
////	public String getNetwork() {
////		return network;
////	}
////
////	public void setNetwork(String network) {
////		this.network = network;
////	}
////
////	public Integer getIs_sent() {
////		return is_sent;
////	}
////
////	public void setIs_sent(Integer is_sent) {
////		this.is_sent = is_sent;
////	}
////
////	public Integer getIs_offline() {
////		return is_offline;
////	}
////
////	public void setIs_offline(Integer is_offline) {
////		this.is_offline = is_offline;
////	}
////
////	public Float getWeight() {
////		return weight;
////	}
////
////	public void setWeight(Float weight) {
////		this.weight = weight;
////	}
////	public Integer getDeviceid() {
////		return deviceid;
////	}
////
////	public void setDeviceid(Integer deviceid) {
////		this.deviceid = deviceid;
////	}
//
//	
//}
