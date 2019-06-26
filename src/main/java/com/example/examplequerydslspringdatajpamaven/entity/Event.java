package com.example.examplequerydslspringdatajpamaven.entity;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tc_events" , schema = "sareb_blue")
public class Event {
	

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@Column(name = "type")
	private String type;
	
	@Column(name = "servertime")
	private Timestamp servertime;
	
	//@Column(name = "deviceid")
	//private Integer deviceid;
	
	@Column(name = "positionid")
	private Integer positionid;
	
	@Column(name = "geofenceid")
	private Integer geofenceid;
	
	@Column(name = "attributes")
	private String attributes;
	
	@Column(name = "is_sent")
	private Integer is_sent;
	
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "maintenanceid")
	private Integer maintenanceid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Timestamp getServertime() {
		return servertime;
	}

	public void setServertime(Timestamp servertime) {
		this.servertime = servertime;
	}

	/*public Integer getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(Integer deviceid) {
		this.deviceid = deviceid;
	}*/

	public Integer getPositionid() {
		return positionid;
	}

	public void setPositionid(Integer positionid) {
		this.positionid = positionid;
	}

	public Integer getGeofenceid() {
		return geofenceid;
	}

	public void setGeofenceid(Integer geofenceid) {
		this.geofenceid = geofenceid;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public Integer getIs_sent() {
		return is_sent;
	}

	public void setIs_sent(Integer is_sent) {
		this.is_sent = is_sent;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getMaintenanceid() {
		return maintenanceid;
	}

	public void setMaintenanceid(Integer maintenanceid) {
		this.maintenanceid = maintenanceid;
	}
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="deviceid", nullable=false)
    private Device device;

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
	


	
}
