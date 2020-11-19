package com.example.examplequerydslspringdatajpamaven.entity;

/**
 * Initial Model to bind data with query
 * @author fuinco
 *
 */
public class BillingsList {
	private Long deviceNumbers;
	private String workingDate;
	private String ownerName;
	
	public BillingsList(Long deviceNumbers, String workingDate, String ownerName) {
		super();
		this.deviceNumbers = deviceNumbers;
		this.workingDate = workingDate;
		this.ownerName = ownerName;
	}
	
	public Long getDeviceNumbers() {
		return deviceNumbers;
	}
	public void setDeviceNumbers(Long deviceNumbers) {
		this.deviceNumbers = deviceNumbers;
	}
	public String getWorkingDate() {
		return workingDate;
	}
	public void setWorkingDate(String workingDate) {
		this.workingDate = workingDate;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	
	
	
	
	
}
