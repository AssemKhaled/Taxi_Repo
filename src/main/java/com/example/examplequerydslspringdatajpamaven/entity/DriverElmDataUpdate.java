package com.example.examplequerydslspringdatajpamaven.entity;

public class DriverElmDataUpdate {

	String identityNumber;
	String mobileNumber;
	String email;
	
	public DriverElmDataUpdate() {
		super();
	}
	
	public DriverElmDataUpdate(String identityNumber, String mobileNumber, String email) {
		super();
		this.identityNumber = identityNumber;
		this.mobileNumber = mobileNumber;
		this.email = email;
	}
	
	public String getIdentityNumber() {
		return identityNumber;
	}
	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
