package com.example.examplequerydslspringdatajpamaven.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

//@Service
public interface ElmService {
	
	public ResponseEntity<?> companyRegistrtaion(String TOKEN,Long userId,Long loggedUserId);
	public ResponseEntity<?> companyUpdate(String TOKEN,Map<String, String> data,Long userId,Long loggedUserId);
	public ResponseEntity<?> companyDelete(String TOKEN,Long userId,Long loggedUserId);

	public ResponseEntity<?> deviceRegistrtaion(String TOKEN,Long deviceId,Long userId);
	public ResponseEntity<?> deviceUpdate(String TOKEN,Map<String, String> data,Long deviceId,Long userId);
	public ResponseEntity<?> deviceDelete(String TOKEN,Long deviceId,Long userId);
	public ResponseEntity<?> deleteVehicleFromElm(String TOKEN,Long deviceId,Long userId,Map<String, String> data);

	public ResponseEntity<?> driverRegistrtaion(String TOKEN,Long driverId,Long userId);
	public ResponseEntity<?> driverUpdate(String TOKEN,Map<String, String> data,Long driverId, Long userId);
	public ResponseEntity<?> driverDelete(String TOKEN,Long driverId,Long userId);

	public ResponseEntity<?> deviceInquery(String TOKEN,Long deviceId,Long userId);
	public ResponseEntity<?> companyInquery(String TOKEN,Long userId,Long loggedUserId);
	public ResponseEntity<?> driverInquery(String TOKEN,Long driverId,Long userId);

	public void lastLocations();
	public ResponseEntity<?> getExpiredVehicles();
	public ResponseEntity<?> getRemoveOldLogs();
	public ResponseEntity<?> getRemoveOldPositions();
	public ResponseEntity<?> getRemoveOldEvents();
	public ResponseEntity<?> checkBySequenceNumber(String sequenceNumber);

	ResponseEntity<?> findLastPositionsSequenceNumberSpeedZero(String sequenceNumber);

	ResponseEntity<?> findLastPositionsSequenceNumberNoneSpeedZero(String sequenceNumber);

	ResponseEntity<?> findLastZeroVelocityPositionsBySequenceNumber(String sequenceNumber) ;

	ResponseEntity<?> findLastNoneZeroVelocityPositionsBySequenceNumber(String sequenceNumber) ;

	ResponseEntity<?> findDeviceData(String sequenceNumber);

	ResponseEntity<?> findDeviceLastPosition(String sequenceNumber);

	ResponseEntity<?> getLogs(String TOKEN,Long id,Long userId,Long driverId,Long deviceId,int offset,String search);
	
	ResponseEntity<?> deleteOldExpiredData();

	ResponseEntity<?> lastLocationsForTowCar();

	ResponseEntity<?> lastLocationsForTowCarForPythonCall(int size);

}
