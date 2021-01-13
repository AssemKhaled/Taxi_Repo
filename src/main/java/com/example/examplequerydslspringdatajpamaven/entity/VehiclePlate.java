package com.example.examplequerydslspringdatajpamaven.entity;

public class VehiclePlate {

	String number;
	String rightLetter;
	String middleLetter;
	String leftLetter;
	
	public VehiclePlate() {
		super();
	}
	
	public VehiclePlate(String number, String rightLetter, String middleLetter, String leftLetter) {
		super();
		this.number = number;
		this.rightLetter = rightLetter;
		this.middleLetter = middleLetter;
		this.leftLetter = leftLetter;
	}
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getRightLetter() {
		return rightLetter;
	}
	public void setRightLetter(String rightLetter) {
		this.rightLetter = rightLetter;
	}
	public String getMiddleLetter() {
		return middleLetter;
	}
	public void setMiddleLetter(String middleLetter) {
		this.middleLetter = middleLetter;
	}
	public String getLeftLetter() {
		return leftLetter;
	}
	public void setLeftLetter(String leftLetter) {
		this.leftLetter = leftLetter;
	}
	
	

}
