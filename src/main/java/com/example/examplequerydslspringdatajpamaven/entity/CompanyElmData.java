package com.example.examplequerydslspringdatajpamaven.entity;

public class CompanyElmData {

	String identityNumber;
	String commercialRecordNumber;
	String commercialRecordIssueDateHijri;
	String phoneNumber;
	String extensionNumber;
	String emailAddress;
	String managerName;
	String managerPhoneNumber;
	String managerMobileNumber;
	String activity;

	
	public CompanyElmData() {
		super();
	}
	
	public CompanyElmData(String identityNumber, String commercialRecordNumber, String commercialRecordIssueDateHijri,
			String phoneNumber, String extensionNumber, String emailAddress, String managerName,
			String managerPhoneNumber, String managerMobileNumber, String activity) {
		super();
		this.identityNumber = identityNumber;
		this.commercialRecordNumber = commercialRecordNumber;
		this.commercialRecordIssueDateHijri = commercialRecordIssueDateHijri;
		this.phoneNumber = phoneNumber;
		this.extensionNumber = extensionNumber;
		this.emailAddress = emailAddress;
		this.managerName = managerName;
		this.managerPhoneNumber = managerPhoneNumber;
		this.managerMobileNumber = managerMobileNumber;
		this.activity = activity;
	}

	public CompanyElmData(String identityNumber, String commercialRecordNumber, String managerName,
			String managerPhoneNumber, String managerMobileNumber) {
		super();
		this.identityNumber = identityNumber;
		this.commercialRecordNumber = commercialRecordNumber;
		this.managerName = managerName;
		this.managerPhoneNumber = managerPhoneNumber;
		this.managerMobileNumber = managerMobileNumber;
	}

	public CompanyElmData(String identityNumber, String commercialRecordNumber, String commercialRecordIssueDateHijri,
			String phoneNumber, String extensionNumber, String emailAddress, String managerName,
			String managerPhoneNumber, String managerMobileNumber) {
		super();
		this.identityNumber = identityNumber;
		this.commercialRecordNumber = commercialRecordNumber;
		this.commercialRecordIssueDateHijri = commercialRecordIssueDateHijri;
		this.phoneNumber = phoneNumber;
		this.extensionNumber = extensionNumber;
		this.emailAddress = emailAddress;
		this.managerName = managerName;
		this.managerPhoneNumber = managerPhoneNumber;
		this.managerMobileNumber = managerMobileNumber;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}
	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}
	public String getCommercialRecordNumber() {
		return commercialRecordNumber;
	}
	public void setCommercialRecordNumber(String commercialRecordNumber) {
		this.commercialRecordNumber = commercialRecordNumber;
	}
	public String getCommercialRecordIssueDateHijri() {
		return commercialRecordIssueDateHijri;
	}
	public void setCommercialRecordIssueDateHijri(String commercialRecordIssueDateHijri) {
		this.commercialRecordIssueDateHijri = commercialRecordIssueDateHijri;
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
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getManagerPhoneNumber() {
		return managerPhoneNumber;
	}
	public void setManagerPhoneNumber(String managerPhoneNumber) {
		this.managerPhoneNumber = managerPhoneNumber;
	}
	public String getManagerMobileNumber() {
		return managerMobileNumber;
	}
	public void setManagerMobileNumber(String managerMobileNumber) {
		this.managerMobileNumber = managerMobileNumber;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	

	
}
