package com.example.examplequerydslspringdatajpamaven.entity;

import javax.jdo.annotations.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tc_positions" , schema = "sareb_blue")
public class Position {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;
	
	@Column(name = "protocol")
	private String protocol;
	
	

}
