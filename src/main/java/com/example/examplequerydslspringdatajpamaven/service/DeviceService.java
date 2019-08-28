package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;


import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;

public interface DeviceService {
	
	public Device findById(Long deviceId);

	public ResponseEntity<?>  getAllUserDevices(String TOKEN,Long userId , int offset, String search);
	
	public ResponseEntity<?> createDevice(String TOKEN,Device device,Long userId);
	
	public ResponseEntity<?> editDevice(String TOKEN,Device device,Long userId);
	
	public List<Integer> checkDeviceDuplication(Device device);
	
	public  ResponseEntity<?> deleteDevice(String TOKEN,Long userId, Long deviceId);
	
	public ResponseEntity<?>  findDeviceById(String TOKEN,Long deviceId,Long userId);

	public ResponseEntity<?> getDeviceSelect(String TOKEN,Long userId);

	public ResponseEntity<?> assignDeviceToDriver(String TOKEN,Long deviceId , Long driverId , Long userId);
	
	public ResponseEntity<?> assignDeviceToGeofences(String TOKEN,Long deviceId,Long [] geoIds,Long userId );

	public ResponseEntity<?> testgetDeviceById();

	public ResponseEntity<?> getDeviceDriver(String TOKEN,Long deviceId);
		
	public ResponseEntity<?> getDeviceGeofences(String TOKEN,Long deviceId);
	
	public ResponseEntity<?> getDeviceStatus(String TOKEN,Long userId);
	
	public ResponseEntity<?> getAllDeviceLiveData(String TOKEN,Long userId,int offset,String search);
	
	public ResponseEntity<?> getDeviceLiveData(String TOKEN,Long deviceId,Long userId);

	public ResponseEntity<?> getDeviceLiveDataMap(String TOKEN,Long deviceId,Long userId);

	public ResponseEntity<?> vehicleInfo(String TOKEN,Long deviceId,Long userId);
	
	public ResponseEntity<?> assignDeviceToUser(Long userId,Long deviceId , Long toUserId);

	public ResponseEntity<?> getAllDeviceLiveDataMap(String TOKEN,Long userId);



}