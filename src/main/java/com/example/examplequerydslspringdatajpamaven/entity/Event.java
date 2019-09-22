package com.example.examplequerydslspringdatajpamaven.entity;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

@SqlResultSetMappings({
	@SqlResultSetMapping(
	        name="eventReport",
	        classes={
	           @ConstructorResult(
	                targetClass=EventReport.class,
	                  columns={
	                		  
	                     @ColumnResult(name="eventId",type=Long.class),
	                     @ColumnResult(name="eventType",type=String.class),
	                     @ColumnResult(name="serverTime",type=String.class),
	                     @ColumnResult(name="attributes",type=String.class),
	                     @ColumnResult(name="deviceId",type=Long.class),
	                     @ColumnResult(name="deviceName",type=String.class),
	                     @ColumnResult(name="driverId",type=Long.class),
	                     @ColumnResult(name="driverName",type=String.class),
	                     @ColumnResult(name="geofenceId",type=Long.class),
	                     @ColumnResult(name="geofenceName",type=String.class),
	                     @ColumnResult(name="positionId",type=Long.class),
	                     @ColumnResult(name="latitude",type=String.class),
	                     @ColumnResult(name="longitude",type=String.class)
	                     
	                     }
	           )
	        }
	),
	@SqlResultSetMapping(
	        name="notification",
	        classes={
	           @ConstructorResult(
	                targetClass=EventReport.class,
	                  columns={
	                		  
	                     @ColumnResult(name="eventId",type=Long.class),
	                     @ColumnResult(name="eventType",type=String.class),
	                     @ColumnResult(name="serverTime",type=String.class),
	                     @ColumnResult(name="attributes",type=String.class),
	                     @ColumnResult(name="deviceId",type=Long.class),
	                     @ColumnResult(name="deviceName",type=String.class),
	                     @ColumnResult(name="driverId",type=Long.class),
	                     @ColumnResult(name="driverName",type=String.class)
	                     
	                     }
	           )
	        }
	)
	

	
})

@NamedNativeQueries({
	@NamedNativeQuery(name="getEvents", 
		     resultSetMapping="eventReport", 
		     query="SELECT tc_events.id as eventId,"
		     		+ " tc_events.type as eventType"
		     		+ " , tc_events.servertime as serverTime,tc_events.attributes as attributes,"
		     		+ " tc_devices.id as deviceId,tc_devices.name as deviceName,"
		     		+ " tc_drivers.id as driverId,tc_drivers.name as driverName ,"
		     		+ " tc_geofences.id as geofenceId,tc_geofences.name as geofenceName,"
		     		+ " tc_positions.id as positionId,tc_positions.latitude as latitude ,tc_positions.longitude as longitude"
		     		+ " FROM tc_events INNER JOIN tc_devices ON tc_devices.id=tc_events.deviceid"
		     		+ " LEFT JOIN tc_positions ON tc_positions.id=tc_events.positionid "
		     		+ " LEFT JOIN tc_geofences ON tc_geofences.id=tc_events.geofenceid"
		     		+ " LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_events.deviceid"
		     		+ " LEFT JOIN tc_drivers ON tc_device_driver.driverid=tc_drivers.id"
		     		+ " WHERE tc_events.servertime BETWEEN :start AND :end "
		     		+ " and tc_events.deviceid =:deviceId AND tc_devices.delete_date IS NULL AND"
		     		+ " tc_drivers.delete_date IS NULL AND tc_geofences.delete_date IS NULL"
					+ " and ( (tc_events.type Like :search) OR (tc_events.attributes Like :search) OR (tc_events.servertime Like :search) OR (tc_geofences.name Like :search) OR (tc_drivers.name Like :search)  OR (tc_devices.name Like :search) )"
		     		+ " ORDER BY tc_events.servertime DESC LIMIT :offset, 10")
	,
	@NamedNativeQuery(name="getEventsToExcel", 
    resultSetMapping="eventReport", 
    query="SELECT tc_events.id as eventId,"
    		+ " tc_events.type as eventType"
    		+ " , tc_events.servertime as serverTime,tc_events.attributes as attributes,"
    		+ " tc_devices.id as deviceId,tc_devices.name as deviceName,"
    		+ " tc_drivers.id as driverId,tc_drivers.name as driverName ,"
    		+ " tc_geofences.id as geofenceId,tc_geofences.name as geofenceName,"
    		+ " tc_positions.id as positionId,tc_positions.latitude as latitude ,tc_positions.longitude as longitude"
    		+ " FROM tc_events INNER JOIN tc_devices ON tc_devices.id=tc_events.deviceid"
    		+ " LEFT JOIN tc_positions ON tc_positions.id=tc_events.positionid "
    		+ " LEFT JOIN tc_geofences ON tc_geofences.id=tc_events.geofenceid"
    		+ " LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_events.deviceid"
    		+ " LEFT JOIN tc_drivers ON tc_device_driver.driverid=tc_drivers.id"
    		+ " WHERE tc_events.servertime BETWEEN :start AND :end "
    		+ " and tc_events.deviceid =:deviceId AND tc_devices.delete_date IS NULL AND"
    		+ " tc_drivers.delete_date IS NULL AND tc_geofences.delete_date IS NULL")
    ,
	@NamedNativeQuery(name="getNotifications", 
    resultSetMapping="notification", 
    query="SELECT tc_events.id as eventId,tc_events.type as eventType,tc_events.servertime as serverTime,"
    		+ " tc_events.attributes as attributes,"
     		+ " tc_devices.id as deviceId,tc_devices.name as deviceName,"
     		+ " tc_drivers.id as driverId,tc_drivers.name as driverName "
    		+ " FROM tc_user_device " 
    		+ " INNER JOIN tc_events ON tc_user_device.deviceid=tc_events.deviceid" 
    		+ " INNER JOIN tc_devices ON tc_events.deviceid=tc_devices.id "
    		+ " LEFT JOIN tc_device_driver ON tc_device_driver.deviceid=tc_events.deviceid "
    		+ " LEFT JOIN tc_drivers ON tc_device_driver.driverid=tc_drivers.id " 
    		+ " WHERE tc_user_device.userid IN(:userId) AND tc_devices.delete_date IS NULL AND tc_drivers.delete_date IS NULL "
    		+ " AND Date(tc_events.servertime)=CURRENT_DATE() " 
    		+ " and ( (tc_events.type Like :search)  OR (tc_events.attributes Like :search) OR (tc_events.servertime Like :search) OR (tc_drivers.name Like :search)  OR (tc_devices.name Like :search) )"
    		+ " ORDER BY tc_events.servertime DESC LIMIT :offset, 10")


})	


@Entity
@Table(name = "tc_events" , schema = "sareb_new")
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
