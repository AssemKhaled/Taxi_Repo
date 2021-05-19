package com.example.examplequerydslspringdatajpamaven.entity;

public class DriverElmDataHijri {

	String identityNumber;
	String mobileNumber;
	String dateOfBirthHijri;
	String activity;

	
	
	public DriverElmDataHijri() {
		super();
	}


	public DriverElmDataHijri(String identityNumber, String mobileNumber, String dateOfBirthHijri, String activity) {
		super();
		this.identityNumber = identityNumber;
		this.mobileNumber = mobileNumber;
		this.dateOfBirthHijri = dateOfBirthHijri;
		this.activity = activity;
	}


	public DriverElmDataHijri(String identityNumber, String mobileNumber, String dateOfBirthHijri) {
		super();
		this.identityNumber = identityNumber;
		this.mobileNumber = mobileNumber;
		this.dateOfBirthHijri = dateOfBirthHijri;
	}
	
	
	public String getActivity() {
		return activity;
	}


	public void setActivity(String activity) {
		this.activity = activity;
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
	public String getDateOfBirthHijri() {
		return dateOfBirthHijri;
	}
	public void setDateOfBirthHijri(String dateOfBirthHijri) {
		this.dateOfBirthHijri = dateOfBirthHijri;
	}
	
	
}
