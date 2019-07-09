package com.example.examplequerydslspringdatajpamaven.entity;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@SqlResultSetMappings({
	@SqlResultSetMapping(
	        name="myMapping",
	        classes={
	           @ConstructorResult(
	                targetClass=EventReport.class,
	                  columns={
	                     @ColumnResult(name="id"),
	                     @ColumnResult(name="type"),
	                     @ColumnResult(name="name")
	                     }
	           )
	        }
	)
,
@SqlResultSetMapping(
        name="myMappingTest",
        classes={
           @ConstructorResult(
                targetClass=EventReport.class,
                  columns={
                     @ColumnResult(name="id"),
                     @ColumnResult(name="name")
                     }
           )
        }
)

	
})

@NamedNativeQueries({
	
@NamedNativeQuery(name="Event.getEvents", 
     resultSetMapping="myMapping", 
     query="SELECT tc_events.id, tc_events.type , tc_devices.name FROM tc_events"
     		+ " INNER JOIN tc_devices ON tc_devices.id=tc_events.deviceid where tc_events.deviceid = 25")
,
@NamedNativeQuery(name="Event.getEventsTest", 
resultSetMapping="myMappingTest", 
query="SELECT tc_events.id, tc_devices.name FROM tc_events"
		+ " INNER JOIN tc_devices ON tc_devices.id=tc_events.deviceid where tc_events.deviceid = 25")
})

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
	
	/*@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="geofenceid", nullable=false,insertable=false, updatable=false)
    private Geofence geofence;

	public Geofence getGeofence() {
		return geofence;
	}

	public void setGeofence(Geofence geofence) {
		this.geofence = geofence;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="positionid", nullable=false,insertable=false, updatable=false)
    private Position position;

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}*/
	

	
}
