package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.entity.Driver;

public interface DriverService {
	
	public List<Driver> getAllDrivers(Long id,int offset,String search);
	public Driver getDriverById(Long driverId);
	public void deleteDriver(Long driverId);
	public List<Driver> checkDublicateDriverInAdd(Long userId,String name,String uniqueId,String mobileNum);
	public String addDriver(Driver driver);
	public List<Driver> checkDublicateDriverInEdit(Long driverId,Long userId,String name,String uniqueId,String mobileNum);
	public void editDriver(Driver driver);
	
	// added by maryam 
	public ResponseEntity<?> getUnassignedDrivers(Long userId);
	
	public Integer getTotalNumberOfUserDrivers(Long userId);



}
