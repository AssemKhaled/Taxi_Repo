package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Date;

/**
 * Bind data list on this model
 * @author fuinco
 *
 */
public class NewcustomerDivice {

	public NewcustomerDivice(int id, String deviceName, Date lastUpdate, Integer positionId, String driverName,
			String leftLetter, String middleLetter, String rightLetter) {
		this.id = id;
		this.deviceName = deviceName;
		this.lastUpdate = lastUpdate;
		this.positionId = positionId;
		this.driverName = driverName;
		this.leftLetter = leftLetter;
		this.middleLetter = middleLetter;
		this.rightLetter = rightLetter;
	}
	private int id;
	private String deviceName;
	private Date lastUpdate;	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public Integer getPositionId() {
		return positionId;
	}
	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getLeftLetter() {
		return leftLetter;
	}
	public void setLeftLetter(String leftLetter) {
		this.leftLetter = leftLetter;
	}
	public String getMiddleLetter() {
		return middleLetter;
	}
	public void setMiddleLetter(String middleLetter) {
		this.middleLetter = middleLetter;
	}
	public String getRightLetter() {
		return rightLetter;
	}
	public void setRightLetter(String rightLetter) {
		this.rightLetter = rightLetter;
	}
	private Integer positionId;
    private String driverName;
	private String leftLetter;
	private String middleLetter;
	private String rightLetter;

}
