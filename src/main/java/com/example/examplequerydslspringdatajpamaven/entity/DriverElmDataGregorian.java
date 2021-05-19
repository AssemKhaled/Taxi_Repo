package com.example.examplequerydslspringdatajpamaven.entity;

public class DriverElmDataGregorian {

	String identityNumber;
	String mobileNumber;
	String dateOfBirthGregorian;
	String activity;

	
	public DriverElmDataGregorian() {
		super();
	}

	public DriverElmDataGregorian(String identityNumber, String mobileNumber, String dateOfBirthGregorian) {
		super();
		this.identityNumber = identityNumber;
		this.mobileNumber = mobileNumber;
		this.dateOfBirthGregorian = dateOfBirthGregorian;
	}
	
	public DriverElmDataGregorian(String identityNumber, String mobileNumber, String dateOfBirthGregorian,
			String activity) {
		super();
		this.identityNumber = identityNumber;
		this.mobileNumber = mobileNumber;
		this.dateOfBirthGregorian = dateOfBirthGregorian;
		this.activity = activity;
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
	public String getDateOfBirthGregorian() {
		return dateOfBirthGregorian;
	}
	public void setDateOfBirthGregorian(String dateOfBirthGregorian) {
		this.dateOfBirthGregorian = dateOfBirthGregorian;
	}
	
	
}
