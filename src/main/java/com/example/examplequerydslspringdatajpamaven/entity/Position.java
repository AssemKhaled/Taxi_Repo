package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;
import java.util.Set;
import javax.jdo.annotations.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

@Entity
@Table(name = "tc_positions" , schema = "sareb_gold")
//@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Position {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;
	
	@Column(name = "protocol")
	private String protocol;
	
	@CsvBindByName
	@CsvDate(value = "E MMM d HH:mm:ss zzz yyyy")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "servertime")
	private Date servertime;
	
	@CsvBindByName
	@CsvDate(value = "E MMM d HH:mm:ss zzz yyyy")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "devicetime")
	private Date devicetime;
	
	@CsvBindByName
	@CsvDate(value = "E MMM d HH:mm:ss zzz yyyy")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fixtime")
	private Date fixtime;
	
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
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deviceid", nullable = false)
    @JsonIgnore
    private Device device;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
	
	/*@OneToMany(mappedBy="position", fetch=FetchType.EAGER)
	private Set<Event> events;

	public Set<Event> getEvents() {
		return events;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}
	*/
	

}
