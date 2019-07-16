package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;


import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;

public interface DeviceService {
	
	public Device findById(Long deviceId);

	public ResponseEntity<?>  getAllUserDevices(Long userId , int offset, String search);
	
	public ResponseEntity<?> createDevice(Device device,Long userId);
	
	public ResponseEntity<?> editDevice(Device device,Long userId);
	
	public List<Integer> checkDeviceDuplication(Device device);
	
	public  ResponseEntity<?> deleteDevice(Long userId, Long deviceId);
	
	public ResponseEntity<?>  findDeviceById(Long deviceId);

	public ResponseEntity<?> getDeviceSelect(Long userId);

	public ResponseEntity<?> assignDeviceToDriver(Long deviceId , Long driverId);
	
	public ResponseEntity<?> assignDeviceToGeofences(Long deviceId,Long [] geoIds );

	public ResponseEntity<?> testgetDeviceById();

	public ResponseEntity<?> getDeviceDriver(Long deviceId);
	
		
	public ResponseEntity<?> getDeviceGeofences(Long deviceId);
	
	public ResponseEntity<?> getDeviceStatus(Long userId);
	
	public ResponseEntity<?> getAllDeviceLiveData(Long userId,int offset,String search);
}