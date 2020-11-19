package com.example.examplequerydslspringdatajpamaven.exceptions;


/**
 * Handel Error Exceptions in returns 
 * @author fuinco
 *
 */
@SuppressWarnings("serial")
public class UserExceptions extends Exception   {
	
	String description;
	 String status;

	public UserExceptions(String description, String status) {
		this.description=description;
		this.status=status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
