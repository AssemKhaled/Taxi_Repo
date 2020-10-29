package com.example.examplequerydslspringdatajpamaven.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface ElmService {
	
	public ResponseEntity<?> companyRegistrtaion(String TOKEN,Long userId,Long loggedUserId);
	public ResponseEntity<?> companyUpdate(String TOKEN,Map<String, String> data,Long userId,Long loggedUserId);
	public ResponseEntity<?> companyDelete(String TOKEN,Long userId,Long loggedUserId);

	public ResponseEntity<?> deviceRegistrtaion(String TOKEN,Long deviceId,Long userId);
	public ResponseEntity<?> deviceUpdate(String TOKEN,Map<String, String> data,Long deviceId,Long userId);
	public ResponseEntity<?> deviceDelete(String TOKEN,Long deviceId,Long userId);

	public ResponseEntity<?> driverRegistrtaion(String TOKEN,Long driverId,Long userId);
	public ResponseEntity<?> driverUpdate(String TOKEN,Map<String, String> data,Long driverId, Long userId);
	public ResponseEntity<?> driverDelete(String TOKEN,Long driverId,Long userId);

	public ResponseEntity<?> deviceInquery(String TOKEN,Long deviceId,Long userId);
	public ResponseEntity<?> companyInquery(String TOKEN,Long userId,Long loggedUserId);
	public ResponseEntity<?> driverInquery(String TOKEN,Long driverId,Long userId);

	public ResponseEntity<?> lastLocations();
	public ResponseEntity<?> getExpiredVehicles();
	public ResponseEntity<?> checkBySequenceNumber(String sequenceNumber);

	public ResponseEntity<?> getLogs(String TOKEN,Long id,Long userId,Long driverId,Long deviceId,int offset,String search);
	

}
