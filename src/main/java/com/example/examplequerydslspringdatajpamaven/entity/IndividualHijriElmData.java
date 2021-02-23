package com.example.examplequerydslspringdatajpamaven.entity;

public class IndividualHijriElmData {

	String identityNumber;
	String phoneNumber;
	String extensionNumber;
	String emailAddress;
	String dateOfBirthHijri;
	String activity;

	
	public IndividualHijriElmData() {
		super();
	}
	
	public IndividualHijriElmData(String identityNumber, String phoneNumber, String extensionNumber,
			String emailAddress, String dateOfBirthHijri) {
		super();
		this.identityNumber = identityNumber;
		this.phoneNumber = phoneNumber;
		this.extensionNumber = extensionNumber;
		this.emailAddress = emailAddress;
		this.dateOfBirthHijri = dateOfBirthHijri;
	}
	
	
	public IndividualHijriElmData(String identityNumber, String phoneNumber, String extensionNumber,
			String emailAddress, String dateOfBirthHijri, String activity) {
		super();
		this.identityNumber = identityNumber;
		this.phoneNumber = phoneNumber;
		this.extensionNumber = extensionNumber;
		this.emailAddress = emailAddress;
		this.dateOfBirthHijri = dateOfBirthHijri;
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
	public String getDateOfBirthHijri() {
		return dateOfBirthHijri;
	}
	public void setDateOfBirthHijri(String dateOfBirthHijri) {
		this.dateOfBirthHijri = dateOfBirthHijri;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}
	
	

}
