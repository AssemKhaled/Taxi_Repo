package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;

public interface DriverService {
	
	public ResponseEntity<?> getAllDrivers(Long id,int offset,String search);
	public ResponseEntity<?> getDriverById(Long driverId);
	public Driver findById(Long driverId);
	public ResponseEntity<?> deleteDriver(Long driverId);
	public List<Driver> checkDublicateDriverInAdd(Long userId,String name,String uniqueId,String mobileNum);
	public ResponseEntity<?> addDriver(Driver driver,Long id);
	public List<Driver> checkDublicateDriverInEdit(Long driverId,Long userId,String name,String uniqueId,String mobileNum);
	public ResponseEntity<?> editDriver(Driver driver,Long id);
	
	// added by maryam 
	public ResponseEntity<?> getUnassignedDrivers(Long userId);
	
	public Integer getTotalNumberOfUserDrivers(Long userId);



}
