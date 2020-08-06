package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

@Entity
@Table(name = "tc_elm_last_locations_tbl")
public class ElmLastLocations {
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@Column(name = "positionid")
	private Long positionid;

	@Column(name = "elm_data" , length = 10000000)
	private String elm_data;
	
	@Column(name = "sendtime")
	private String sendtime;
	
	@Column(name = "responsetime")
	private String responsetime;
	
	@Column(name = "responsetype")
	private Integer responsetype;
	
	@Column(name = "vehicleid")
	private Long vehicleid;
	
	@Column(name = "vehiclename")
	private String vehiclename;
	
	@Column(name = "vehicleReferenceKey")
	private String vehicleReferenceKey;
	 
	@Column(name = "driverid")
	private Long driverid;
	
	@Column(name = "drivername")
	private String drivername;
	
	@Column(name = "driverReferenceKey")
	private String driverReferenceKey;
	
	@Column(name = "user_id")
	private Long user_id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "userReferenceKey")
	private String userReferenceKey;
	
	@Column(name = "reason")
	private String reason;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPositionid() {
		return positionid;
	}

	public void setPositionid(Long positionid) {
		this.positionid = positionid;
	}

	public String getElm_data() {
		return elm_data;
	}

	public void setElm_data(String elm_data) {
		this.elm_data = elm_data;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getResponsetime() {
		return responsetime;
	}

	public void setResponsetime(String responsetime) {
		this.responsetime = responsetime;
	}

	public Integer getResponsetype() {
		return responsetype;
	}

	public void setResponsetype(Integer responsetype) {
		this.responsetype = responsetype;
	}

	public Long getVehicleid() {
		return vehicleid;
	}

	public void setVehicleid(Long vehicleid) {
		this.vehicleid = vehicleid;
	}

	public String getVehiclename() {
		return vehiclename;
	}

	public void setVehiclename(String vehiclename) {
		this.vehiclename = vehiclename;
	}

	public String getVehicleReferenceKey() {
		return vehicleReferenceKey;
	}

	public void setVehicleReferenceKey(String vehicleReferenceKey) {
		this.vehicleReferenceKey = vehicleReferenceKey;
	}

	public Long getDriverid() {
		return driverid;
	}

	public void setDriverid(Long driverid) {
		this.driverid = driverid;
	}

	public String getDrivername() {
		return drivername;
	}

	public void setDrivername(String drivername) {
		this.drivername = drivername;
	}

	public String getDriverReferenceKey() {
		return driverReferenceKey;
	}

	public void setDriverReferenceKey(String driverReferenceKey) {
		this.driverReferenceKey = driverReferenceKey;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserReferenceKey() {
		return userReferenceKey;
	}

	public void setUserReferenceKey(String userReferenceKey) {
		this.userReferenceKey = userReferenceKey;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	


	
	
	   
	  
}
