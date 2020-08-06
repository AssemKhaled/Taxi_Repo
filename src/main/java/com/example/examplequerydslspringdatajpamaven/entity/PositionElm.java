package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;


@SqlResultSetMappings({
	@SqlResultSetMapping(
	        name="List",
	        classes={
	           @ConstructorResult(
	                targetClass=LastLocationsList.class,
	                  columns={
	                     @ColumnResult(name="id",type=Long.class),
	                     @ColumnResult(name="lasttime",type=String.class),
	                     @ColumnResult(name="deviceid",type=Long.class),
	                     @ColumnResult(name="latitude",type=String.class),
	                     @ColumnResult(name="longitude",type=String.class),
	                     @ColumnResult(name="speed",type=Double.class),
	                     @ColumnResult(name="attributes",type=String.class),
	                     @ColumnResult(name="devicetime",type=String.class),
	                     @ColumnResult(name="deviceRK",type=String.class),
	                     @ColumnResult(name="driver_RK",type=String.class),
	                     @ColumnResult(name="driverid",type=Long.class),
	                     @ColumnResult(name="drivername",type=String.class),
	                     @ColumnResult(name="weight",type=Double.class),
	                     @ColumnResult(name="address",type=String.class),
	                     @ColumnResult(name="is_offline",type=Double.class),
	                     @ColumnResult(name="devicename",type=String.class),
	                     @ColumnResult(name="userid",type=Long.class),
	                     @ColumnResult(name="username",type=String.class),
	                     @ColumnResult(name="userRK",type=String.class)
	                     
	                     }
	           )
	        }
	)
})

@NamedNativeQueries({
	
	@NamedNativeQuery(name="getList", 
		     resultSetMapping="List", 
		     query=  "SELECT tc_positions_elm.id as id,tc_positions_elm.servertime as lasttime,tc_positions_elm.deviceid as deviceid, " + 
			" tc_positions_elm.latitude as latitude,tc_positions_elm.longitude as longitude,tc_positions_elm.speed as speed, " + 
			" tc_positions_elm.attributes as attributes, tc_positions_elm.devicetime as devicetime,tc_devices.reference_key as deviceRK ," + 
			" tc_drivers.reference_key as driver_RK ,tc_drivers.id as driverid,tc_drivers.name as drivername, " + 
			" tc_positions_elm.weight as weight ,tc_positions_elm.address as address,tc_positions_elm.is_offline as is_offline , " + 
			" tc_devices.name as devicename, tc_users.id as userid ,tc_users.name as username ,tc_users.reference_key as userRK FROM tc_positions_elm " + 
			" INNER JOIN tc_devices ON tc_devices.id=tc_positions_elm.deviceid " + 
			" LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_positions_elm.deviceid " + 
			" LEFT JOIN tc_drivers ON tc_drivers.id=tc_device_driver.driverid " + 
			" INNER JOIN tc_user_device ON tc_user_device.deviceid=tc_positions_elm.deviceid " + 
			" INNER JOIN tc_users ON tc_user_device.userid=tc_users.id " + 
			" WHERE tc_positions_elm.is_sent IS NULL " + 
			" AND tc_devices.is_deleted IS NULL " + 
			" AND tc_devices.create_date Is NOT NULL " + 
			" AND tc_devices.expired IS False " + 
			" AND tc_drivers.is_deleted IS NULL " + 
			" AND tc_devices.reference_key IS NOT NULL " + 
			" LIMIT 1000" )
})

@Entity
@Table(name = "tc_positions_elm")
public class PositionElm {

	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@Column(name = "protocol")
	private String protocol;
	
	@Column(name = "deviceid")
	private Long deviceid;

	@Column(name = "servertime")
	private String servertime;
	
	@Column(name = "devicetime")
	private String devicetime;
	
	@Column(name = "fixtime")
	private String fixtime;
	
	@Column(name = "valid")
	private Integer valid;
	
	@Column(name = "latitude")
	private Double latitude;
	
	@Column(name = "longitude")
	private Double longitude;
	
	@Column(name = "altitude")
	private float altitude;
	
	@Column(name = "speed")
	private float speed;
	
	@Column(name = "course")
	private float course;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "attributes")
	private String attributes;
	
	@Column(name = "accuracy")
	private Double accuracy;
	
	@Column(name = "network")
	private String network;
	
	@Column(name = "is_sent")
	private Integer is_sent;
	
	@Column(name = "is_offline")
	private Integer is_offline;
	
	@Column(name = "weight")
	private Float weight;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getServertime() {
		return servertime;
	}

	public void setServertime(String servertime) {
		this.servertime = servertime;
	}

	public String getDevicetime() {
		return devicetime;
	}

	public void setDevicetime(String devicetime) {
		this.devicetime = devicetime;
	}

	public String getFixtime() {
		return fixtime;
	}

	public void setFixtime(String fixtime) {
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

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}


	
	
}
