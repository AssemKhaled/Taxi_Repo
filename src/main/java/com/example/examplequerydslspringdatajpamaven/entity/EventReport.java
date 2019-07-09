package com.example.examplequerydslspringdatajpamaven.entity;

import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventReport {

	private int id;
	private String type;
	private String name;

	public EventReport (int id ,String type,String name){
		
	   this.id = id;
       this.type = "";
       this.name= null;
	   
	
	}
	
	public EventReport (int id ,String name){
		
		   this.id = id;
	       this.name= name;
		  
	       
		
	}
		
	
	public int getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	
	 
	 
}
