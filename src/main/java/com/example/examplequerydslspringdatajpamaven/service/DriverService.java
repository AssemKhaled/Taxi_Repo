package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;

public interface DriverService {
	
	public ResponseEntity<?> getAllDrivers(String TOKEN,Long id,int offset,String search);
	public Driver getDriverById(Long driverId);
	public ResponseEntity<?> getDriverSelect(String TOKEN,Long userId);
	public ResponseEntity<?> findById(String TOKEN,Long driverId,Long userId);
	public ResponseEntity<?> deleteDriver(String Token,Long driverId, Long userId);
	public List<Driver> checkDublicateDriverInAddEmail(Long userId,String name);
	public List<Driver> checkDublicateDriverInAddUniqueMobile(String uniqueId,String mobileNum);

	public ResponseEntity<?> addDriver(String TOKEN,Driver driver,Long id);
	public List<Driver> checkDublicateDriverInEditEmail(Long driverId,Long userId,String email);
	public List<Driver> checkDublicateDriverInEditMobileUnique(Long driverId,String uniqueId,String mobileNum);

	
	public ResponseEntity<?> editDriver(String TOKEN,Driver driver,Long id);
	
	// added by maryam 
	public ResponseEntity<?> getUnassignedDrivers(String TOKEN,Long userId);
	
	public Integer getTotalNumberOfUserDrivers(List<Long> userId);
	
	// added by maryam
	public ResponseEntity<?> assignDriverToUser(String TOKEN,Long userId,Long driverId , Long toUserId);



	public ResponseEntity<?> assignClientDrivers(String TOKEN,Long loggedUserId,Long userId,Long [] driverIds);

	public ResponseEntity<?> getClientDrivers(String TOKEN,Long loggedUserId,Long userId);

}
