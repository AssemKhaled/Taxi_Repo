package com.example.examplequerydslspringdatajpamaven.responses;

import java.util.List;

public class GetObjectResponse {
	
	private Integer responseCode;
	private String  message;
	private List<?> entity;
	
   public GetObjectResponse(Integer responseCode, String message,List<?> entity) {
	   this.responseCode = responseCode;
	   this.message = message;
	   this.entity = entity;
	  
   }

public Integer getResponseCode() {
	return responseCode;
}

public void setResponseCode(Integer responseCode) {
	this.responseCode = responseCode;
}

public String getMessage() {
	return message;
}

public void setMessage(String message) {
	this.message = message;
}

public List<?> getEntity() {
	return entity;
}

public void setEntity(List<?> entity) {
	this.entity = entity;
}
   
  

}
