package com.example.examplequerydslspringdatajpamaven.entity;

public class CompanyElmDataUpdate {

	String identityNumber; 
	String commercialRecordNumber;
	String managerName;
	String managerPhoneNumber;
	String managerMobileNumber;
	

	public CompanyElmDataUpdate() {
		super();
	}
	public CompanyElmDataUpdate(String identityNumber, String commercialRecordNumber, String managerName,
			String managerPhoneNumber, String managerMobileNumber) {
		super();
		this.identityNumber = identityNumber;
		this.commercialRecordNumber = commercialRecordNumber;
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
	
	
}
