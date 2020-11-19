package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Map;

/**
 * Model to return any action on elm services
 * @author fuinco
 *
 */
public class ElmReturn {

	private Map body;
	private Long statusCode;
	private String message;
	
	public ElmReturn() {
		
	}
	
	public ElmReturn(Map body, Long statusCode, String message) {
		super();
		this.body = body;
		this.statusCode = statusCode;
		this.message = message;
	}
	
	public ElmReturn(String message) {
		this.message = message;
	}
	
	public ElmReturn(Map body, Long statusCode) {
		this.body = body;
		this.statusCode = statusCode;
	}
	
	public Map getBody() {
		return body;
	}
	public void setBody(Map body) {
		this.body = body;
	}
	public Long getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Long statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	

	
	
}
