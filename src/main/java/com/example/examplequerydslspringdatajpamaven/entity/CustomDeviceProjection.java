package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Entity;

import org.springframework.stereotype.Component;

@Component
public class CustomDeviceProjection {
	
	private Long id;
	private String name;
	private String uniqueid;
	private String sequence_number;
	private String lastupdate;
	
//	public CustomDeviceProjection(int id,String uniqueid, String sequence_number,String lastupdate) {
//		this.id = id;
//		this.uniqueid = uniqueid;
//		this.sequence_number = sequence_number;
//		this.lastupdate =lastupdate;
//	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUniqueid() {
		return uniqueid;
	}

	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}

	public String getSequence_number() {
		return sequence_number;
	}

	public void setSequence_number(String sequence_number) {
		this.sequence_number = sequence_number;
	}

	public String getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(String lastupdate) {
		this.lastupdate = lastupdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
