package com.example.examplequerydslspringdatajpamaven.entity;

import javax.persistence.Entity;

import org.springframework.beans.factory.annotation.Value;


public interface CustomDeviceEntity {
	
	 @Value("#{target.name + ' ' + target.name}")
     String getFullName();
//
//	 @Value("{target.id}")
	   Long getId();
	 
//     @Value("{target.name}")
      String getName();
// 
//     @Value("#{uniqueid")
     String getUniqueId();
////     
//     @Value("#{sequence_number")
     String getSequenceNumber();
////     
//    @Value("#{lastupdate")
    String getLastUpdate();


}
