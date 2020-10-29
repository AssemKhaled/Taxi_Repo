package com.example.examplequerydslspringdatajpamaven.entity;


public class LastElmData {


	private Object elm_data;
	
	private String sendtime;
	
	private String vehiclename;

	private String drivername;

	public LastElmData() {
		super();
	}
	
	public LastElmData(Object elm_data, String sendtime, String vehiclename, String drivername) {
		super();
		this.elm_data = elm_data;
		this.sendtime = sendtime;
		this.vehiclename = vehiclename;
		this.drivername = drivername;
	}

	public Object getElm_data() {
		return elm_data;
	}

	public void setElm_data(Object elm_data) {
		this.elm_data = elm_data;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getVehiclename() {
		return vehiclename;
	}

	public void setVehiclename(String vehiclename) {
		this.vehiclename = vehiclename;
	}

	public String getDrivername() {
		return drivername;
	}

	public void setDrivername(String drivername) {
		this.drivername = drivername;
	}

	
}
