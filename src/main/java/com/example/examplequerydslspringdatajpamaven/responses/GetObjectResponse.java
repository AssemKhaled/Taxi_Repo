package com.example.examplequerydslspringdatajpamaven.responses;

import java.util.List;
import java.util.Map;

/**
 * Return data from any response of serviece get in this object
 * @author fuinco
 *
 */
public class GetObjectResponse {
	
	private Integer responseCode;
	private String  message;
	private List<?> entity;
	private Map<Object, Object>  sensorSettings;
	private Integer size;
	
	
   public GetObjectResponse(Integer responseCode, String message,List<?> entity) {
	   this.responseCode = responseCode;
	   this.message = message;
	   this.entity = entity;
	  
   }
   public GetObjectResponse(Integer responseCode, String message,List<?> entity, Map<Object, Object>  sensorSettings) {
	   this.responseCode = responseCode;
	   this.message = message;
	   this.entity = entity;
	   this.sensorSettings = sensorSettings;

   }
   public GetObjectResponse(Integer responseCode, String message,List<?> entity,Integer size) {
	   this.responseCode = responseCode;
	   this.message = message;
	   this.entity = entity;
	   this.size = size;

   }
   public GetObjectResponse(Integer responseCode, String message,List<?> entity, Map<Object, Object>  sensorSettings,Integer size) {
	   this.responseCode = responseCode;
	   this.message = message;
	   this.entity = entity;
	   this.sensorSettings = sensorSettings;
	   this.size = size;

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
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public  Map<Object, Object>  getSensorSettings() {
		return sensorSettings;
	}
	public void setSensorSettings( Map<Object, Object>  sensorSettings) {
		this.sensorSettings = sensorSettings;
	}
	   
	  

}
