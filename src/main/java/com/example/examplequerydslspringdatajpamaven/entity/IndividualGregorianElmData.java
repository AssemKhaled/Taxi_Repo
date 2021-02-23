package com.example.examplequerydslspringdatajpamaven.entity;

public class IndividualGregorianElmData {

	String identityNumber;
	String phoneNumber;
	String extensionNumber;
	String emailAddress;
	String dateOfBirthGregorian;
	String activity;

	public IndividualGregorianElmData() {
		super();
	}
	
	public IndividualGregorianElmData(String identityNumber, String phoneNumber, String extensionNumber,
			String emailAddress, String dateOfBirthGregorian) {
		super();
		this.identityNumber = identityNumber;
		this.phoneNumber = phoneNumber;
		this.extensionNumber = extensionNumber;
		this.emailAddress = emailAddress;
		this.dateOfBirthGregorian = dateOfBirthGregorian;
	}
	
	public IndividualGregorianElmData(String identityNumber, String phoneNumber, String extensionNumber,
			String emailAddress, String dateOfBirthGregorian, String activity) {
		super();
		this.identityNumber = identityNumber;
		this.phoneNumber = phoneNumber;
		this.extensionNumber = extensionNumber;
		this.emailAddress = emailAddress;
		this.dateOfBirthGregorian = dateOfBirthGregorian;
		this.activity = activity;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}
	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getExtensionNumber() {
		return extensionNumber;
	}
	public void setExtensionNumber(String extensionNumber) {
		this.extensionNumber = extensionNumber;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getDateOfBirthGregorian() {
		return dateOfBirthGregorian;
	}
	public void setDateOfBirthGregorian(String dateOfBirthGregorian) {
		this.dateOfBirthGregorian = dateOfBirthGregorian;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}
	
	
}
