package com.example.examplequerydslspringdatajpamaven.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;


import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DeviceSelect;

public interface DeviceService {
	
	public Device findById(Long deviceId);

	public List<?> getAllUserDevices(Long userId , int offset, String search);
	
	public ResponseEntity<?> createDevice(Device device,Long userId);
	
	public ResponseEntity<?> editDevice(Device device,Long userId);
	
	public List<Integer> checkDeviceDuplication(Device device);
	
	public  ResponseEntity<?> deleteDevice(Long userId, Long deviceId);
	
	public ResponseEntity<?>  findDeviceById(Long deviceId);

	public  List<DeviceSelect> getDeviceSelect(Long userId);

	public String assignDeviceToDriver(Device device);
	
	public String assignDeviceToGeofences(Device device);
	
	public ResponseEntity<?> testgetDeviceById();
}
