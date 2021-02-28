package com.example.examplequerydslspringdatajpamaven.exceptions;

/**
 * Handel error exceptions
 * @author fuinco
 *
 */
public enum UserExceptionEnum {
	Server_Error(101), Data_Base_Connection_Error(102), Failed_To_Retrive_The_Requested_Page(103),
	Connecttion_error(104),User_Not_found_Exception(201), Invalid_username(202), Invalid_password(203),
	You_arenot_allowed_To_Login(204),Please_refer_To_Your_Vendor(205);

	private int errorCode;

	private UserExceptionEnum(int errorCode) {
		this.errorCode = errorCode;

	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
