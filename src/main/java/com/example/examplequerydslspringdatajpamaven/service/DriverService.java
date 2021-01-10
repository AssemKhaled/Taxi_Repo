package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Driver;

@Service
public interface DriverService {
	
	public ResponseEntity<?> getAllDrivers(String TOKEN,Long id,int offset,String search,String exportData);
	public Driver getDriverById(Long driverId);
	
	public ResponseEntity<?> getDriverSelect(String TOKEN,Long loggedUserId,Long userId);
	public ResponseEntity<?> getDriverUnSelectOfClient(String TOKEN,Long loggedUserId, Long userId);
	public ResponseEntity<?> getDriverSelectGroup(String TOKEN,Long loggedUserId,Long userId,Long groupId);

	public ResponseEntity<?> findById(String TOKEN,Long driverId,Long userId);
	public ResponseEntity<?> deleteDriver(String Token,Long driverId, Long userId);
	public List<Driver> checkDublicateDriverInAddEmail(Long userId,String name);
	public List<Driver> checkDublicateDriverInAddUniqueMobile(String uniqueId,String mobileNum,String email);

	public ResponseEntity<?> addDriver(String TOKEN,Driver driver,Long id);
	public List<Driver> checkDublicateDriverInEditEmail(Long driverId,Long userId,String name);
	public List<Driver> checkDublicateDriverInEditMobileUnique(Long driverId,String uniqueId,String mobileNum,String email);

	
	public ResponseEntity<?> editDriver(String TOKEN,Driver driver,Long id);
	
	public ResponseEntity<?> getUnassignedDrivers(String TOKEN,Long loggedUserId,Long userId,Long deviceId);
	
	public Integer getTotalNumberOfUserDrivers(List<Long> userId);
	
	public ResponseEntity<?> assignDriverToUser(String TOKEN,Long userId,Long driverId , Long toUserId);



	public ResponseEntity<?> assignClientDrivers(String TOKEN,Long loggedUserId,Long userId,Long [] driverIds);

	public ResponseEntity<?> getClientDrivers(String TOKEN,Long loggedUserId,Long userId);

}
